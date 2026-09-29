package gg.anarchia.itemy.gui;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.events.GameEvent;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

/** Menu eventow. */
public class EventsGui extends Gui {

    private final List<GameEvent> events;

    public EventsGui(AnarchiaItemy plugin) {
        super(plugin, plugin.getConfig().getString("gui.events-title", "&8» &c&lEventy"), 4);
        this.events = new ArrayList<>(plugin.events().all());
        build();
    }

    private void build() {
        int slot = 10;
        for (GameEvent event : events) {
            if (slot % 9 == 8) {
                slot += 3;
            }
            List<String> lore = new ArrayList<>();
            lore.add("&7" + event.getDescription());
            lore.add("");
            lore.add("&7Status: " + (event.isRunning() ? "&aTRWA" : "&cnieaktywny"));
            lore.add("&8ID: &7" + event.getId());
            lore.add("");
            lore.add(event.isRunning() ? "&eKliknij, aby zatrzymać" : "&eKliknij, aby wystartować");
            inventory.setItem(slot, icon(event.getIcon(), "&c&l" + event.getName(), lore));
            slot++;
        }
        inventory.setItem(31, icon(Material.BARRIER, plugin.messages().raw("gui-back")));
        fill();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == 31) {
            new PanelGui(plugin).open(player);
            return;
        }
        int index = 0;
        int current = 10;
        for (GameEvent gameEvent : events) {
            if (current % 9 == 8) {
                current += 3;
            }
            if (current == slot) {
                if (gameEvent.isRunning()) {
                    plugin.events().stop();
                    plugin.messages().send(player, "event-stopped", "%event%", gameEvent.getName());
                } else {
                    plugin.events().start(player, gameEvent.getId(), new String[0]);
                }
                Sounds.play(player, Sounds.PLING, 1.0F, 1.3F);
                new EventsGui(plugin).open(player);
                return;
            }
            current++;
            index++;
        }
    }
}
