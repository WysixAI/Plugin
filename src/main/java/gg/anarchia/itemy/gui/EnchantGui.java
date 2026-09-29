package gg.anarchia.itemy.gui;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.Keys;
import gg.anarchia.itemy.util.Enchants;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Menu zaczarowywania przedmiotu trzymanego w rece. */
public class EnchantGui extends Gui {

    private final Map<Integer, String> slots = new LinkedHashMap<>();

    public EnchantGui(AnarchiaItemy plugin) {
        super(plugin, plugin.getConfig().getString("enchanting.title", "&8» &c&lZaczarowanie przedmiotu"), 6);
        build();
    }

    private Map<String, Integer> configured() {
        Map<String, Integer> map = new LinkedHashMap<>();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("enchanting.enchantments");
        if (section == null) {
            return map;
        }
        for (String key : section.getKeys(false)) {
            map.put(key, section.getInt(key, 5));
        }
        return map;
    }

    private void build() {
        int slot = 0;
        for (Map.Entry<String, Integer> entry : configured().entrySet()) {
            if (slot >= 45) {
                break;
            }
            Enchantment enchantment = Enchants.byName(entry.getKey());
            if (enchantment == null) {
                continue;
            }
            List<String> lore = new ArrayList<>();
            lore.add("&7Maksymalny poziom: &c" + Enchants.roman(entry.getValue()));
            lore.add("");
            lore.add("&eLPM &7- dodaj poziom");
            lore.add("&ePPM &7- odejmij poziom");
            lore.add("&eShift + LPM &7- maksymalny poziom");
            lore.add("&eShift + PPM &7- otrzymaj księgę");
            inventory.setItem(slot, icon(Material.ENCHANTED_BOOK, "&c&l" + Enchants.prettyName(enchantment), lore));
            slots.put(slot, entry.getKey());
            slot++;
        }
        inventory.setItem(49, icon(Material.LAVA_BUCKET, "&c&lUsuń wszystkie zaklęcia",
                "&7Czyści przedmiot trzymany w ręce.",
                "",
                "&eKliknij, aby wyczyścić"));
        inventory.setItem(45, icon(Material.BARRIER, plugin.messages().raw("gui-back")));
        fill();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == 45) {
            new PanelGui(plugin).open(player);
            return;
        }
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (slot == 49) {
            if (ItemUtil.isEmpty(hand)) {
                plugin.messages().send(player, "no-item-in-hand");
                return;
            }
            ItemMeta meta = hand.getItemMeta();
            if (meta != null) {
                for (Enchantment enchantment : new ArrayList<>(meta.getEnchants().keySet())) {
                    meta.removeEnchant(enchantment);
                }
                hand.setItemMeta(meta);
            }
            plugin.messages().send(player, "enchant-removed");
            Sounds.play(player, Sounds.GLASS, 1.0F, 0.8F);
            return;
        }
        String key = slots.get(slot);
        if (key == null) {
            return;
        }
        Enchantment enchantment = Enchants.byName(key);
        if (enchantment == null) {
            plugin.messages().send(player, "enchant-unknown", "%enchant%", key);
            return;
        }
        int max = Math.max(1, plugin.getConfig().getInt("enchanting.enchantments." + key, 5));
        ClickType click = event.getClick();

        if (click == ClickType.SHIFT_RIGHT) {
            if (!player.hasPermission("iAnarchiaitemy.books")) {
                plugin.messages().send(player, "no-permission-books");
                return;
            }
            ItemUtil.give(player, createBook(enchantment, max));
            Sounds.play(player, Sounds.ORB, 1.0F, 1.2F);
            return;
        }
        if (ItemUtil.isEmpty(hand)) {
            plugin.messages().send(player, "no-item-in-hand");
            return;
        }
        int cost = plugin.getConfig().getInt("enchanting.cost-levels", 0);
        if (cost > 0 && player.getLevel() < cost && player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            plugin.messages().send(player, "enchant-cost", "%levels%", String.valueOf(cost));
            return;
        }
        int current = hand.getEnchantmentLevel(enchantment);
        int target = switch (click) {
            case SHIFT_LEFT -> max;
            case RIGHT, SHIFT_RIGHT -> current - 1;
            default -> current + 1;
        };
        if (target > max) {
            plugin.messages().send(player, "enchant-max", "%level%", String.valueOf(max));
            return;
        }
        ItemMeta meta = hand.getItemMeta();
        if (meta == null) {
            return;
        }
        if (target <= 0) {
            meta.removeEnchant(enchantment);
            target = 0;
        } else {
            meta.addEnchant(enchantment, target, true);
        }
        hand.setItemMeta(meta);
        if (cost > 0 && player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
            player.setLevel(Math.max(0, player.getLevel() - cost));
        }
        plugin.messages().send(player, "enchant-applied",
                "%enchant%", Enchants.prettyName(enchantment), "%level%", Enchants.roman(target));
        Sounds.play(player, Sounds.LEVEL_UP, 0.8F, 1.6F);
    }

    /** Tworzy specjalna ksiege pluginu (wymaga permisji iAnarchiaitemy.books). */
    public ItemStack createBook(Enchantment enchantment, int level) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();
        if (meta instanceof EnchantmentStorageMeta storage) {
            storage.addStoredEnchant(enchantment, level, true);
        }
        if (meta != null) {
            meta.displayName(Text.parse("&c&l" + Enchants.prettyName(enchantment) + " " + Enchants.roman(level)));
            List<String> lore = new ArrayList<>();
            lore.add("&7Przeciągnij na przedmiot w ekwipunku,");
            lore.add("&7aby nałożyć zaklęcie.");
            lore.add("");
            lore.add("&8» &cAnarchia.GG");
            meta.lore(Text.parse(lore));
            meta.getPersistentDataContainer().set(Keys.BOOK_ENCHANT, PersistentDataType.STRING, Enchants.keyOf(enchantment));
            meta.getPersistentDataContainer().set(Keys.BOOK_LEVEL, PersistentDataType.INTEGER, level);
            book.setItemMeta(meta);
        }
        return book;
    }
}
