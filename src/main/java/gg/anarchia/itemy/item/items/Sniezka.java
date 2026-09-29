package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Śnieżka - po trafieniu zamieniasz się miejscem z przeciwnikiem. */
public class Sniezka extends CustomItem implements Handlers.RightClick, Handlers.Hit {

    public Sniezka() {
        super("sniezka", Material.SNOWBALL, "&b&lŚnieżka");
        category("Sabotaż");
        model(10036);
        lore("&7Po trafieniu zamieniasz się",
                "&7z przeciwnikiem &cmiejscem&7!",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("speed", 1.6D);
        setting("consume", true);
        cooldown(10.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setVelocity(player.getLocation().getDirection().multiply(settingDouble("speed")));
        ItemUtil.tag(snowball, getId());
        Sounds.play(player.getLocation(), "entity.snowball.throw", 1.0F, 1.0F);
        if (settingBool("consume")) {
            ItemUtil.consumeOne(player, event.getHand());
        }
    }

    @Override
    public void onProjectileHit(Player shooter, Projectile projectile, ProjectileHitEvent event) {
        Entity hit = event.getHitEntity();
        projectile.remove();
        if (shooter == null || !(hit instanceof Player target)) {
            return;
        }
        Location shooterLocation = shooter.getLocation().clone();
        Location targetLocation = target.getLocation().clone();
        shooter.teleport(targetLocation);
        target.teleport(shooterLocation);
        Particles.spawn(shooterLocation, Particles.PORTAL, 40);
        Particles.spawn(targetLocation, Particles.PORTAL, 40);
        Sounds.play(shooterLocation, Sounds.ENDER_TP, 1.0F, 1.0F);
        Sounds.play(targetLocation, Sounds.ENDER_TP, 1.0F, 1.0F);
    }
}
