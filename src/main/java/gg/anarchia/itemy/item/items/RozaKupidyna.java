package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import gg.anarchia.itemy.util.Particles;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/** Róża kupidyna - trzymając w ręce: Odporność I oraz Regeneracja I. */
public class RozaKupidyna extends CustomItem implements Handlers.Hold {

    public RozaKupidyna() {
        super("rozakupidyna", Material.POPPY, "&d&lRóża Kupidyna");
        category("Efekty");
        model(10030);
        lore("&7Trzymając ją w ręce otrzymujesz:",
                "&8• &cOdporność I",
                "&8• &cRegeneracja I",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("resistance-level", 1);
        setting("regeneration-level", 1);
        setting("particles", true);
    }

    @Override
    public void onHold(Player player, ItemStack item) {
        Effects.refresh(player, Effects.RESISTANCE, 100, Math.max(0, settingInt("resistance-level") - 1));
        Effects.refresh(player, Effects.REGENERATION, 100, Math.max(0, settingInt("regeneration-level") - 1));
        if (settingBool("particles")) {
            Particles.spawn(player.getLocation().add(0, 1.0D, 0), Particles.HEART, 1, 0.3D, 0.3D, 0.3D, 0.0D);
        }
    }
}
