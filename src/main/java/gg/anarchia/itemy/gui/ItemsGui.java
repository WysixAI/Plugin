package gg.anarchia.itemy.gui;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/** Podglad wszystkich przedmiotow (/anarchiaitemy menupreview). */
public class ItemsGui extends Gui {

    private static final int PER_PAGE = 45;

    private final List<CustomItem> items;
    private final int page;
    private final int pages;

    public ItemsGui(AnarchiaItemy plugin, int page) {
        super(plugin, title(plugin, page), 6);
        this.items = new ArrayList<>(plugin.items().all());
        this.pages = Math.max(1, (int) Math.ceil(items.size() / (double) PER_PAGE));
        this.page = Math.max(0, Math.min(page, pages - 1));
        build();
    }

    private static String title(AnarchiaItemy plugin, int page) {
        String raw = plugin.getConfig().getString("gui.items-title", "&8» &c&lPodgląd przedmiotów &7(%page%/%pages%)");
        int pages = Math.max(1, (int) Math.ceil(plugin.items().all().size() / (double) PER_PAGE));
        return Text.replace(raw, "%page%", String.valueOf(Math.max(0, Math.min(page, pages - 1)) + 1), "%pages%", String.valueOf(pages));
    }

    private void build() {
        int start = page * PER_PAGE;
        for (int i = 0; i < PER_PAGE; i++) {
            int index = start + i;
            if (index >= items.size()) {
                break;
            }
            CustomItem item = items.get(index);
            ItemStack stack = item.build();
            ItemMeta meta = stack.getItemMeta();
            if (meta != null) {
                List<String> lore = new ArrayList<>(item.getLore());
                lore.add("");
                lore.add("&8ID: &7" + item.getId());
                lore.add("&8Kategoria: &7" + item.getCategory());
                lore.add(item.isEnabled() ? "&aWłączony" : "&cWyłączony");
                lore.add("");
                lore.add("&eLPM &7- otrzymaj 1 sztukę");
                lore.add("&eShift + LPM &7- otrzymaj 64 sztuki");
                meta.lore(Text.parse(lore));
                stack.setItemMeta(meta);
            }
            inventory.setItem(i, stack);
        }
        if (page > 0) {
            inventory.setItem(45, icon(Material.ARROW, plugin.messages().raw("gui-prev")));
        }
        if (page < pages - 1) {
            inventory.setItem(53, icon(Material.ARROW, plugin.messages().raw("gui-next")));
        }
        inventory.setItem(49, icon(Material.BARRIER, plugin.messages().raw("gui-back")));
        fill();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == 45 && page > 0) {
            new ItemsGui(plugin, page - 1).open(player);
            return;
        }
        if (slot == 53 && page < pages - 1) {
            new ItemsGui(plugin, page + 1).open(player);
            return;
        }
        if (slot == 49) {
            new PanelGui(plugin).open(player);
            return;
        }
        if (slot < 0 || slot >= PER_PAGE) {
            return;
        }
        int index = page * PER_PAGE + slot;
        if (index >= items.size()) {
            return;
        }
        if (!player.hasPermission("iAnarchiaitemy.admin")) {
            plugin.messages().send(player, "no-permission");
            return;
        }
        CustomItem item = items.get(index);
        int amount = event.getClick() == ClickType.SHIFT_LEFT ? 64 : 1;
        ItemUtil.give(player, item.build(amount));
        plugin.messages().send(player, "item-given", "%item%", item.getDisplayName());
        Sounds.play(player, Sounds.ORB, 1.0F, 1.4F);
    }
}
