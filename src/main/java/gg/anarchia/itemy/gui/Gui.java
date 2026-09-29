package gg.anarchia.itemy.gui;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Bazowa klasa wszystkich menu pluginu. */
public abstract class Gui implements InventoryHolder {

    protected final AnarchiaItemy plugin;
    protected final Inventory inventory;

    protected Gui(AnarchiaItemy plugin, String title, int rows) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, Math.max(9, Math.min(6, rows) * 9), Text.parse(title));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    /** Czy gracz moze przekladac przedmioty w tym menu. */
    public boolean allowInteraction() {
        return false;
    }

    public abstract void onClick(InventoryClickEvent event);

    public void onClose(InventoryCloseEvent event) {
    }

    protected ItemStack icon(Material material, String name, String... lore) {
        return icon(material, name, Arrays.asList(lore));
    }

    protected ItemStack icon(Material material, String name, List<String> lore) {
        ItemStack stack = new ItemStack(material == null ? Material.STONE : material);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.displayName(Text.parse(name));
            List<String> lines = new ArrayList<>(lore);
            if (!lines.isEmpty()) {
                meta.lore(Text.parse(lines));
            }
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    protected void fill() {
        Material filler = Material.matchMaterial(plugin.getConfig().getString("gui.filler", "GRAY_STAINED_GLASS_PANE"));
        if (filler == null) {
            filler = Material.GRAY_STAINED_GLASS_PANE;
        }
        ItemStack stack = icon(filler, "&7");
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, stack);
            }
        }
    }

    protected String cfg(String path, String def) {
        return plugin.getConfig().getString(path, def);
    }
}
