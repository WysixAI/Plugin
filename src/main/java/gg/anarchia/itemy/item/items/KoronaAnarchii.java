package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/** Korona Anarchii - założona na głowę daje Speed 2, Fire Res 1, Siła 2, Odporność 3, Szczęście 1. */
public class KoronaAnarchii extends CustomItem implements Handlers.Wear {

    public KoronaAnarchii() {
        super("koronaanarchi", Material.GOLDEN_HELMET, "&6&lKorona Anarchii");
        category("Zbroje");
        model(10012);
        lore("&7Załóż na głowę, aby otrzymać:",
                "&8• &cSzybkość II",
                "&8• &cOdporność na ogień I",
                "&8• &cSiła II",
                "&8• &cOdporność III",
                "&8• &cSzczęście I",
                "",
                "&8» &cAnarchia.GG");
        unbreakable();
        glow();
        setting("speed-level", 2);
        setting("fire-resistance-level", 1);
        setting("strength-level", 2);
        setting("resistance-level", 3);
        setting("luck-level", 1);
        flags("HIDE_ENCHANTS", "HIDE_ATTRIBUTES", "HIDE_UNBREAKABLE");
    }

    @Override
    public void onWear(Player player, ItemStack item) {
        ItemStack helmet = player.getInventory().getHelmet();
        if (!matches(helmet)) {
            return;
        }
        Effects.refresh(player, Effects.SPEED, 100, level("speed-level"));
        Effects.refresh(player, Effects.FIRE_RESISTANCE, 100, level("fire-resistance-level"));
        Effects.refresh(player, Effects.STRENGTH, 100, level("strength-level"));
        Effects.refresh(player, Effects.RESISTANCE, 100, level("resistance-level"));
        Effects.refresh(player, Effects.LUCK, 100, level("luck-level"));
    }

    private int level(String path) {
        return Math.max(0, settingInt(path) - 1);
    }
}
