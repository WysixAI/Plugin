package gg.anarchia.itemy.listener;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.Keys;
import gg.anarchia.itemy.gui.Gui;
import gg.anarchia.itemy.util.Enchants;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/** Uzywanie zaczarowanych ksiag pluginu (permisja iAnarchiaitemy.books). */
public class BookListener implements Listener {

    private final AnarchiaItemy plugin;

    public BookListener(AnarchiaItemy plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (event.getInventory().getHolder() instanceof Gui) {
            return;
        }
        ItemStack book = event.getCursor();
        ItemStack target = event.getCurrentItem();
        if (ItemUtil.isEmpty(book) || ItemUtil.isEmpty(target)) {
            return;
        }
        String enchantKey = ItemUtil.getString(book, Keys.BOOK_ENCHANT);
        if (enchantKey == null) {
            return;
        }
        event.setCancelled(true);
        if (!player.hasPermission("iAnarchiaitemy.books")) {
            plugin.messages().send(player, "no-permission-books");
            return;
        }
        Enchantment enchantment = Enchants.byName(enchantKey);
        int level = ItemUtil.getInt(book, Keys.BOOK_LEVEL, 1);
        if (enchantment == null || target.getType().isAir() || target.getType().getMaxStackSize() > 1) {
            plugin.messages().send(player, "book-failed");
            return;
        }
        ItemMeta meta = target.getItemMeta();
        if (meta == null) {
            plugin.messages().send(player, "book-failed");
            return;
        }
        meta.addEnchant(enchantment, level, true);
        target.setItemMeta(meta);
        if (book.getAmount() > 1) {
            book.setAmount(book.getAmount() - 1);
            player.setItemOnCursor(book);
        } else {
            player.setItemOnCursor(null);
        }
        plugin.messages().send(player, "book-applied",
                "%enchant%", Enchants.prettyName(enchantment), "%level%", Enchants.roman(level));
        Particles.spawn(player.getLocation().add(0, 1.0D, 0), Particles.ENCHANTED_HIT, 20);
        Sounds.play(player, Sounds.LEVEL_UP, 0.8F, 1.5F);
    }
}
