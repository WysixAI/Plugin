package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import org.bukkit.Material;

/** Anarchiczny kilof - netherytowy kilof z EFFICIENCY 10, FORTUNE 5, UNBREAKING 5. */
public class AnarchicznyKilof extends CustomItem {

    public AnarchicznyKilof() {
        super("anarchicznykilof", Material.NETHERITE_PICKAXE, "&c&lAnarchiczny Kilof");
        category("Narzędzia");
        model(10001);
        lore("&7Netherytowy kilof z zaklęciami:",
                "&8• &cEfficiency X",
                "&8• &cFortune V&7, &cUnbreaking V",
                "",
                "&8» &cAnarchia.GG");
        enchant("efficiency", 10);
        enchant("fortune", 5);
        enchant("unbreaking", 5);
        unbreakable();
        flags("HIDE_ENCHANTS", "HIDE_ATTRIBUTES", "HIDE_UNBREAKABLE");
    }
}
