package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/** Kupa Anarchii - trzymając w ręce dostajesz Siłę II na zawsze. */
public class KupaAnarchii extends CustomItem implements Handlers.Hold {

    public KupaAnarchii() {
        super("kupaanarchi", Material.BROWN_DYE, "&6&lKupa Anarchii");
        category("Efekty");
        model(10016);
        lore("&7Trzymając ją w ręce otrzymujesz:",
                "&8• &cSiła II",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("strength-level", 2);
    }

    @Override
    public void onHold(Player player, ItemStack item) {
        Effects.refresh(player, Effects.STRENGTH, 100, Math.max(0, settingInt("strength-level") - 1));
    }
}
