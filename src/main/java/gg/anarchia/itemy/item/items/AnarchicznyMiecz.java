package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import org.bukkit.Material;

/** Anarchiczny miecz - netherytowy miecz z SHARPNESS 6 oraz FIRE ASPECT 2. */
public class AnarchicznyMiecz extends CustomItem {

    public AnarchicznyMiecz() {
        super("anarchicznymiecz", Material.NETHERITE_SWORD, "&c&lAnarchiczny Miecz");
        category("Bronie");
        model(10003);
        lore("&7Netherytowy miecz z zaklęciami:",
                "&8• &cSharpness VI",
                "&8• &cFire Aspect II",
                "",
                "&8» &cAnarchia.GG");
        enchant("sharpness", 6);
        enchant("fire_aspect", 2);
        enchant("unbreaking", 5);
        unbreakable();
        flags("HIDE_ENCHANTS", "HIDE_ATTRIBUTES", "HIDE_UNBREAKABLE");
    }
}
