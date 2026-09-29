package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/** Lizak - trzymając w ręce dostajesz Siłę I na zawsze. */
public class Lizak extends CustomItem implements Handlers.Hold {

    public Lizak() {
        super("lizak", Material.SUGAR, "&d&lLizak");
        category("Efekty");
        model(10018);
        lore("&7Trzymając go w ręce otrzymujesz:",
                "&8• &cSiła I",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("strength-level", 1);
    }

    @Override
    public void onHold(Player player, ItemStack item) {
        Effects.refresh(player, Effects.STRENGTH, 100, Math.max(0, settingInt("strength-level") - 1));
    }
}
