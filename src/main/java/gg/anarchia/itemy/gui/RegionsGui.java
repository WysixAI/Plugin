package gg.anarchia.itemy.gui;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.region.Region;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Lista regionow + przelaczanie flag. */
public class RegionsGui extends Gui {

    private static final int PER_PAGE = 45;

    private final List<Region> regions;
    private final int page;

    public RegionsGui(AnarchiaItemy plugin, int page) {
        super(plugin, plugin.getConfig().getString("gui.regions-title", "&8» &c&lRegiony"), 6);
        this.regions = new ArrayList<>(plugin.regions().all());
        this.page = Math.max(0, page);
        build();
    }

    private void build() {
        int start = page * PER_PAGE;
        for (int i = 0; i < PER_PAGE && start + i < regions.size(); i++) {
            Region region = regions.get(start + i);
            List<String> lore = new ArrayList<>();
            lore.add("&7Świat: &f" + region.getWorld());
            lore.add("&7Od: &f" + region.getMinX() + ", " + region.getMinY() + ", " + region.getMinZ());
            lore.add("&7Do: &f" + region.getMaxX() + ", " + region.getMaxY() + ", " + region.getMaxZ());
            lore.add("");
            for (Map.Entry<String, Boolean> flag : region.getFlags().entrySet()) {
                lore.add("&8• &7" + flag.getKey() + ": " + (flag.getValue() ? "&atak" : "&cnie"));
            }
            lore.add("");
            lore.add("&eLPM &7- teleportacja");
            lore.add("&ePPM &7- przełącz flagę &fpvp");
            lore.add("&eShift + PPM &7- przełącz flagę &fitems");
            inventory.setItem(i, icon(Material.FILLED_MAP, "&c&l" + region.getName(), lore));
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
        if (slot == 49) {
            new PanelGui(plugin).open(player);
            return;
        }
        int index = page * PER_PAGE + slot;
        if (slot < 0 || slot >= PER_PAGE || index >= regions.size()) {
            return;
        }
        Region region = regions.get(index);
        ClickType click = event.getClick();
        if (click == ClickType.RIGHT) {
            toggle(player, region, "pvp");
        } else if (click == ClickType.SHIFT_RIGHT) {
            toggle(player, region, "items");
        } else if (click == ClickType.LEFT) {
            if (region.center() != null) {
                player.closeInventory();
                player.teleport(region.center());
                Sounds.play(player, Sounds.ENDER_TP, 1.0F, 1.0F);
            }
        }
    }

    private void toggle(Player player, Region region, String flag) {
        boolean value = !region.getFlag(flag, true);
        region.setFlag(flag, value);
        plugin.regions().save();
        plugin.messages().send(player, "region-flag-set",
                "%flag%", flag, "%region%", region.getName(), "%value%", String.valueOf(value));
        new RegionsGui(plugin, page).open(player);
    }
}
