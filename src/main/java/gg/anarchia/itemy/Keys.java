package gg.anarchia.itemy;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

/**
 * Wszystkie klucze PersistentDataContainer uzywane przez plugin.
 */
public final class Keys {

    public static NamespacedKey ITEM_ID;
    public static NamespacedKey KILLS;
    public static NamespacedKey BACKPACK;
    public static NamespacedKey CHARGE;
    public static NamespacedKey BOOK_ENCHANT;
    public static NamespacedKey BOOK_LEVEL;
    public static NamespacedKey OWNER;
    public static NamespacedKey REGION_WAND;
    public static NamespacedKey TEMPORARY;
    public static NamespacedKey COMBO;

    private Keys() {
    }

    public static void init(Plugin plugin) {
        ITEM_ID = new NamespacedKey(plugin, "item_id");
        KILLS = new NamespacedKey(plugin, "kills");
        BACKPACK = new NamespacedKey(plugin, "backpack");
        CHARGE = new NamespacedKey(plugin, "charge");
        BOOK_ENCHANT = new NamespacedKey(plugin, "book_enchant");
        BOOK_LEVEL = new NamespacedKey(plugin, "book_level");
        OWNER = new NamespacedKey(plugin, "owner");
        REGION_WAND = new NamespacedKey(plugin, "region_wand");
        TEMPORARY = new NamespacedKey(plugin, "temporary");
        COMBO = new NamespacedKey(plugin, "combo");
    }
}
