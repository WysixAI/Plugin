package gg.anarchia.itemy.util;

import gg.anarchia.itemy.Keys;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public final class ItemUtil {

    private ItemUtil() {
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getType().isAir() || stack.getAmount() <= 0;
    }

    public static String getId(ItemStack stack) {
        if (isEmpty(stack) || !stack.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(Keys.ITEM_ID, PersistentDataType.STRING);
    }

    public static boolean hasId(ItemStack stack, String id) {
        return id != null && id.equals(getId(stack));
    }

    public static String getId(Entity entity) {
        if (entity == null) {
            return null;
        }
        return entity.getPersistentDataContainer().get(Keys.ITEM_ID, PersistentDataType.STRING);
    }

    public static void tag(Entity entity, String id) {
        if (entity == null || id == null) {
            return;
        }
        entity.getPersistentDataContainer().set(Keys.ITEM_ID, PersistentDataType.STRING, id);
    }

    public static int getInt(ItemStack stack, org.bukkit.NamespacedKey key, int def) {
        if (isEmpty(stack)) {
            return def;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return def;
        }
        Integer value = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        return value == null ? def : value;
    }

    public static void setInt(ItemStack stack, org.bukkit.NamespacedKey key, int value) {
        if (isEmpty(stack)) {
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
        stack.setItemMeta(meta);
    }

    public static String getString(ItemStack stack, org.bukkit.NamespacedKey key) {
        if (isEmpty(stack)) {
            return null;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public static void setString(ItemStack stack, org.bukkit.NamespacedKey key, String value) {
        if (isEmpty(stack)) {
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (value == null) {
            container.remove(key);
        } else {
            container.set(key, PersistentDataType.STRING, value);
        }
        stack.setItemMeta(meta);
    }

    /** Zabiera jedna sztuke przedmiotu z reki gracza. */
    public static void consumeOne(Player player, EquipmentSlot hand) {
        if (player == null) {
            return;
        }
        ItemStack stack = hand == EquipmentSlot.OFF_HAND
                ? player.getInventory().getItemInOffHand()
                : player.getInventory().getItemInMainHand();
        if (isEmpty(stack)) {
            return;
        }
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE) {
            return;
        }
        int amount = stack.getAmount() - 1;
        if (amount <= 0) {
            if (hand == EquipmentSlot.OFF_HAND) {
                player.getInventory().setItemInOffHand(null);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        } else {
            stack.setAmount(amount);
        }
    }

    public static void give(Player player, ItemStack stack) {
        if (player == null || isEmpty(stack)) {
            return;
        }
        Map<Integer, ItemStack> left = player.getInventory().addItem(stack);
        for (ItemStack rest : left.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), rest);
        }
    }

    public static boolean holds(Player player, String id) {
        return hasId(player.getInventory().getItemInMainHand(), id) || hasId(player.getInventory().getItemInOffHand(), id);
    }

    public static ItemStack findInInventory(Player player, String id) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (hasId(stack, id)) {
                return stack;
            }
        }
        return null;
    }
}
