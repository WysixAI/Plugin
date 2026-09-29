package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.gui.BackpackGui;
import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/** Plecak Drakuli - przenośny ekwipunek otwierany nawet podczas walki. */
public class PlecakDrakuli extends CustomItem implements Handlers.RightClick {

    public PlecakDrakuli() {
        super("plecakdrakuli", Material.SHULKER_BOX, "&5&lPlecak Drakuli");
        category("Wsparcie");
        model(10029);
        lore("&7PPM: otwiera przenośny ekwipunek,",
                "&7który działa nawet podczas walki.",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("rows", 3);
        cooldown(0.5D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (item.getAmount() > 1) {
            Text.send(player, plugin.messages().prefix() + "&cMusisz trzymać tylko jeden plecak w ręce!");
            return;
        }
        if (!checkCooldown(player)) {
            return;
        }
        boolean offHand = event.getHand() == EquipmentSlot.OFF_HAND;
        int slot = offHand ? -1 : player.getInventory().getHeldItemSlot();
        int rows = Math.max(1, Math.min(6, settingInt("rows")));
        String title = plugin.getConfig().getString("gui.backpack-title", "&8» &5&lPlecak Drakuli");
        new BackpackGui(plugin, player, item, slot, offHand, title, rows).open(player);
        Sounds.play(player, "block.shulker_box.open", 0.8F, 1.2F);
    }
}
