package gg.anarchia.itemy.gui;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/** Glowne menu pluginu (/anarchiaitemy panel). */
public class PanelGui extends Gui {

    public PanelGui(AnarchiaItemy plugin) {
        super(plugin, plugin.getConfig().getString("gui.panel-title", "&8» &c&lAnarchiaItemy &8- &7Panel"), 5);
        build();
    }

    private void build() {
        inventory.setItem(10, icon(Material.NETHERITE_SWORD, "&c&lPrzedmioty eventowe",
                "&7Podgląd wszystkich eventowek.",
                "&7Załadowanych: &c" + plugin.items().all().size(),
                "",
                "&eKliknij, aby otworzyć"));
        inventory.setItem(12, icon(Material.FIREWORK_ROCKET, "&c&lEventy",
                "&7Zarządzaj eventami na serwerze.",
                "&7Aktywny: &c" + (plugin.events().active() == null ? "brak" : plugin.events().active().getName()),
                "",
                "&eKliknij, aby otworzyć"));
        inventory.setItem(14, icon(Material.GOLDEN_AXE, "&c&lRegiony",
                "&7Zarządzaj regionami i flagami.",
                "&7Regionów: &c" + plugin.regions().all().size(),
                "",
                "&eKliknij, aby otworzyć"));
        inventory.setItem(16, icon(Material.ENCHANTING_TABLE, "&c&lZaczarowanie",
                "&7Menu zaczarowywania przedmiotów.",
                "",
                "&eKliknij, aby otworzyć"));
        inventory.setItem(30, icon(Material.REDSTONE, "&c&lPrzeładuj konfigurację",
                "&7Przeładowuje config.yml, items.yml,",
                "&7messages.yml oraz regions.yml.",
                "",
                "&eKliknij, aby przeładować"));
        inventory.setItem(32, icon(Material.BOOK, "&c&lInformacje",
                "&7Plugin: &fAnarchiaItemy",
                "&7Wersja: &f" + plugin.getDescription().getVersion(),
                "&7Tekstury: &fteksturepack z Anarchia.GG",
                "&7Discord: &fdiscord.gg/yfnDmhhv5Q"));
        inventory.setItem(40, icon(Material.BARRIER, plugin.messages().raw("gui-close")));
        fill();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        switch (event.getRawSlot()) {
            case 10 -> new ItemsGui(plugin, 0).open(player);
            case 12 -> new EventsGui(plugin).open(player);
            case 14 -> new RegionsGui(plugin, 0).open(player);
            case 16 -> new EnchantGui(plugin).open(player);
            case 30 -> {
                plugin.reloadEverything();
                plugin.messages().send(player, "reloaded",
                        "%items%", String.valueOf(plugin.items().all().size()),
                        "%regions%", String.valueOf(plugin.regions().all().size()));
                Sounds.play(player, Sounds.LEVEL_UP, 1.0F, 1.4F);
                player.closeInventory();
            }
            case 40 -> player.closeInventory();
            default -> {
            }
        }
    }
}
