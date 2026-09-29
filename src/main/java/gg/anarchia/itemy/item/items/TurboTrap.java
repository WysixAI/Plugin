package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
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

/** Turbo-Trap - po wystrzeleniu zamyka pobliskich graczy w klatce. */
public class TurboTrap extends CustomItem implements Handlers.RightClick, Handlers.Hit {

    public TurboTrap() {
        super("turbotrap", Material.IRON_BARS, "&7&lTurbo-Trap");
        category("Sabotaż");
        model(10042);
        lore("&7Podczas wystrzelenia zamykasz pobliskich",
                "&7graczy w &cklatce &7w miejscu eksplozji!",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("speed", 1.6D);
        setting("radius", 4.0D);
        setting("cage-material", "OBSIDIAN");
        setting("window-material", "GLASS");
        setting("duration-seconds", 15.0D);
        setting("consume", true);
        cooldown(30.0D);
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
        Sounds.play(player.getLocation(), "entity.snowball.throw", 1.0F, 0.9F);
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
        Material cage = Material.matchMaterial(settingString("cage-material"));
        Material window = Material.matchMaterial(settingString("window-material"));
        long ticks = (long) (settingDouble("duration-seconds") * 20);
        java.util.List<LivingEntity> targets = PlayerUtil.nearbyLiving(center, settingDouble("radius"), shooter);
        if (targets.isEmpty()) {
            Structures.cage(plugin, shooter, center, cage == null ? Material.OBSIDIAN : cage,
                    window == null ? Material.GLASS : window, ticks);
        } else {
            for (LivingEntity target : targets) {
                Structures.cage(plugin, shooter, target.getLocation(), cage == null ? Material.OBSIDIAN : cage,
                        window == null ? Material.GLASS : window, ticks);
            }
        }
        Particles.spawn(center, Particles.EXPLOSION, 4, 1.0D, 1.0D, 1.0D, 0.0D);
        Sounds.play(center, Sounds.ANVIL_LAND, 1.0F, 1.2F);
    }
}
