package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.DamageGuard;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.PlayerUtil;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;

/** Trójząb Posejdona - w miejscu uderzenia przyzywa piorun i odpycha wrogów. */
public class TrojzabPosejdona extends CustomItem implements Handlers.Hit {

    public TrojzabPosejdona() {
        super("trojzabposejdona", Material.TRIDENT, "&b&lTrójząb Posejdona");
        category("Bronie");
        model(10040);
        lore("&7Po rzuceniu w miejsce uderzenia",
                "&7uderza &cpiorun&7, który zadaje obrażenia",
                "&7i odpycha pobliskich przeciwników.",
                "",
                "&8» &cAnarchia.GG");
        enchant("loyalty", 3);
        enchant("impaling", 5);
        enchant("unbreaking", 5);
        unbreakable();
        setting("radius", 5.0D);
        setting("damage", 7.0D);
        setting("knockback", 1.4D);
        setting("real-lightning", false);
        cooldown(8.0D);
    }

    @Override
    public void onProjectileHit(Player shooter, Projectile projectile, ProjectileHitEvent event) {
        Location center = event.getHitBlock() != null
                ? event.getHitBlock().getLocation().add(0.5D, 1.0D, 0.5D)
                : projectile.getLocation();
        if (center.getWorld() == null) {
            return;
        }
        if (settingBool("real-lightning")) {
            center.getWorld().strikeLightning(center);
        } else {
            center.getWorld().strikeLightningEffect(center);
        }
        for (LivingEntity target : PlayerUtil.nearbyLiving(center, settingDouble("radius"), shooter)) {
            PlayerUtil.knockback(target, center, settingDouble("knockback"), 0.55D);
            DamageGuard.damage(target, settingDouble("damage"), shooter);
        }
        Particles.spawn(center, Particles.ELECTRIC_SPARK, 40, 1.0D, 1.0D, 1.0D, 0.1D);
        Particles.circle(center, Particles.SPLASH, 3.0D, 40);
        Sounds.play(center, Sounds.THUNDER, 1.5F, 1.0F);
    }
}
