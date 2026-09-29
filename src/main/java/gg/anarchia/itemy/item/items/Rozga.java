package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import org.bukkit.Material;

/** Rózga - patyk z zaklęciem KNOCKBACK 4. */
public class Rozga extends CustomItem {

    public Rozga() {
        super("rozga", Material.STICK, "&e&lRózga");
        category("Bronie");
        model(10032);
        lore("&7Zwykły patyk... z &cKnockback IV&7!",
                "&7Odrzuć przeciwnika na drugi koniec mapy.",
                "",
                "&8» &cAnarchia.GG");
        enchant("knockback", 4);
        unbreakable();
        flags("HIDE_ENCHANTS", "HIDE_ATTRIBUTES", "HIDE_UNBREAKABLE");
    }
}
