package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/** Złamane serce - po uderzeniu: spowolnienie + powolne opadanie. */
public class ZlamaneSerce extends CustomItem implements Handlers.Attack {

    public ZlamaneSerce() {
        super("zlamaneserce", Material.RED_DYE, "&4&lZłamane Serce");
        category("Bronie");
        model(10053);
        lore("&7Po uderzeniu przeciwnika otrzymuje on",
                "&cSpowolnienie &7oraz &cPowolne opadanie&7.",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("slowness-seconds", 5.0D);
        setting("slowness-level", 2);
        setting("slow-falling-seconds", 6.0D);
        cooldown(3.0D);
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, ItemStack item, EntityDamageByEntityEvent event) {
        if (!checkCooldown(attacker)) {
            return;
        }
        Effects.apply(victim, Effects.SLOWNESS, (int) (settingDouble("slowness-seconds") * 20), Math.max(0, settingInt("slowness-level") - 1));
        Effects.apply(victim, Effects.SLOW_FALLING, (int) (settingDouble("slow-falling-seconds") * 20), 0);
        Particles.spawn(victim.getLocation().add(0, 1.5D, 0), Particles.HEART, 8);
        Sounds.play(victim.getLocation(), Sounds.GLASS, 1.0F, 0.6F);
    }
}
