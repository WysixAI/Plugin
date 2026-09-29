package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.PlayerUtil;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Structures;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Wyrzutnia Hydro Klatki - wystrzeliwuje pocisk tworzący wodną klatkę. */
public class WyrzutniaHydroKlatki extends CustomItem implements Handlers.RightClick, Handlers.Hit {

    public WyrzutniaHydroKlatki() {
        super("wyrzutniahydroklatki", Material.DISPENSER, "&3&lWyrzutnia Hydro Klatki");
        category("Sabotaż");
        model(10048);
        lore("&7PPM: wystrzeliwuje pocisk, który tworzy",
                "&7&bwodną klatkę &7więżąc przeciwników w miejscu!",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("speed", 1.8D);
        setting("radius", 2.5D);
        setting("duration-seconds", 8.0D);
        setting("slowness-level", 3);
        setting("consume", false);
        cooldown(25.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(player.getLocation().getDirection().multiply(settingDouble("speed")));
        ItemUtil.tag(projectile, getId());
        Sounds.play(player.getLocation(), Sounds.WATER, 1.0F, 1.4F);
        if (settingBool("consume")) {
            ItemUtil.consumeOne(player, event.getHand());
        }
    }

    @Override
    public void onProjectileHit(Player shooter, Projectile projectile, ProjectileHitEvent event) {
        Location center = event.getHitEntity() != null
                ? event.getHitEntity().getLocation()
                : projectile.getLocation();
        projectile.remove();
        long ticks = (long) (settingDouble("duration-seconds") * 20);
        double radius = settingDouble("radius");
        Structures.sphere(plugin, shooter, center, radius, Material.WATER, ticks);
        int level = Math.max(0, settingInt("slowness-level") - 1);
        for (LivingEntity target : PlayerUtil.nearbyLiving(center, radius + 1.0D, shooter)) {
            Effects.apply(target, Effects.SLOWNESS, (int) ticks, level);
            Effects.apply(target, Effects.MINING_FATIGUE, (int) ticks, 2);
        }
        Particles.spawn(center, Particles.SPLASH, 60, 1.5D, 1.5D, 1.5D, 0.1D);
        Sounds.play(center, Sounds.SPLASH, 1.5F, 0.8F);
    }
}
