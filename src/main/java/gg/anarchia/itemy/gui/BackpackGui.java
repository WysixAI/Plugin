package gg.anarchia.itemy.gui;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.Keys;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Serializer;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

/** Przenosny ekwipunek (Plecak Drakuli). */
public class BackpackGui extends Gui {

    private final Player owner;
    private final int slot;
    private final boolean offHand;
    private final String itemId;

    public BackpackGui(AnarchiaItemy plugin, Player owner, ItemStack source, int slot, boolean offHand, String title, int rows) {
        super(plugin, title, rows);
        this.owner = owner;
        this.slot = slot;
        this.offHand = offHand;
        this.itemId = ItemUtil.getId(source);
        ItemStack[] contents = Serializer.fromBase64(ItemUtil.getString(source, Keys.BACKPACK), inventory.getSize());
        for (int i = 0; i < Math.min(contents.length, inventory.getSize()); i++) {
            inventory.setItem(i, contents[i]);
        }
    }

    @Override
    public boolean allowInteraction() {
        return true;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        if (isBackpack(clicked) || isBackpack(cursor)) {
            event.setCancelled(true);
            plugin.messages().send(owner, "backpack-blocked");
            return;
        }
        if (event.getAction() == InventoryAction.HOTBAR_SWAP) {
            ItemStack hotbar = event.getClick() == org.bukkit.event.inventory.ClickType.SWAP_OFFHAND
                    ? owner.getInventory().getItemInOffHand()
                    : owner.getInventory().getItem(event.getHotbarButton());
            if (isBackpack(hotbar)) {
                event.setCancelled(true);
                plugin.messages().send(owner, "backpack-blocked");
            }
        }
    }

    private boolean isBackpack(ItemStack stack) {
        return itemId != null && ItemUtil.hasId(stack, itemId);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        save();
        Sounds.play(owner, "block.shulker_box.close", 0.8F, 1.2F);
    }

    public void save() {
        ItemStack item = offHand ? owner.getInventory().getItemInOffHand() : owner.getInventory().getItem(slot);
        if (!ItemUtil.hasId(item, itemId)) {
            item = ItemUtil.findInInventory(owner, itemId);
        }
        if (item == null) {
            return;
        }
        String data = Serializer.toBase64(inventory.getContents());
        ItemUtil.setString(item, Keys.BACKPACK, data);
    }
}
