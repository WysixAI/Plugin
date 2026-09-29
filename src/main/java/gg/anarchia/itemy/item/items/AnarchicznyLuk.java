package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import org.bukkit.Material;

/** Anarchiczny Łuk - FLAME, POWER 6, PUNCH 3, UNBREAKING 5. */
public class AnarchicznyLuk extends CustomItem {

    public AnarchicznyLuk() {
        super("anarchicznyluk", Material.BOW, "&c&lAnarchiczny Łuk");
        category("Bronie");
        model(10002);
        lore("&7Łuk z zaklęciami:",
                "&8• &cFlame I&7, &cPower VI",
                "&8• &cPunch III&7, &cUnbreaking V",
                "",
                "&8» &cAnarchia.GG");
        enchant("flame", 1);
        enchant("power", 6);
        enchant("punch", 3);
        enchant("unbreaking", 5);
        unbreakable();
        flags("HIDE_ENCHANTS", "HIDE_ATTRIBUTES", "HIDE_UNBREAKABLE");
    }
}
