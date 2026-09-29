package gg.anarchia.itemy.listener;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.gui.Gui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

/** Obsluga menu pluginu. */
public class GuiListener implements Listener {

    private final AnarchiaItemy plugin;

    public GuiListener(AnarchiaItemy plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Gui gui)) {
            return;
        }
        boolean topInventory = event.getRawSlot() >= 0 && event.getRawSlot() < event.getInventory().getSize();
        if (!gui.allowInteraction()) {
            event.setCancelled(true);
        }
        if (topInventory || gui.allowInteraction()) {
            try {
                gui.onClick(event);
            } catch (Exception exception) {
                plugin.getLogger().warning("Blad w menu: " + exception);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Gui gui && !gui.allowInteraction()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Gui gui) {
            try {
                gui.onClose(event);
            } catch (Exception exception) {
                plugin.getLogger().warning("Blad przy zamykaniu menu: " + exception);
            }
        }
    }
}
