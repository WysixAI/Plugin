package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

/** Wampirze jabłko - po zjedzeniu daje Siłę II na krótki czas. */
public class WampirzeJablko extends CustomItem implements Handlers.Consume {

    public WampirzeJablko() {
        super("wampirzejablko", Material.GOLDEN_APPLE, "&4&lWampirze Jabłko");
        category("Wsparcie");
        model(10043);
        lore("&7Po zjedzeniu otrzymujesz",
                "&cSiłę II &7na &c%seconds% sekund&7.",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("strength-level", 2);
        setting("seconds", 20.0D);
        setting("regeneration-level", 2);
        flags("HIDE_POTION_EFFECTS");
    }

    @Override
    public void onConsume(Player player, ItemStack item, PlayerItemConsumeEvent event) {
        int ticks = (int) (settingDouble("seconds") * 20);
        Effects.apply(player, Effects.STRENGTH, ticks, Math.max(0, settingInt("strength-level") - 1));
        if (settingInt("regeneration-level") > 0) {
            Effects.apply(player, Effects.REGENERATION, ticks / 2, Math.max(0, settingInt("regeneration-level") - 1));
        }
        Particles.spawn(player.getLocation().add(0, 1.0D, 0), Particles.HEART, 15);
        Sounds.play(player, Sounds.BURP, 1.0F, 0.6F);
    }
}
