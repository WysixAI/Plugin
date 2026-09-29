package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.DamageGuard;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.PlayerUtil;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Bombarda maxima - po wystrzeleniu wybucha i niszczy każdy blok oprócz skały macierzystej. */
public class BombardaMaxima extends CustomItem implements Handlers.Hit, Handlers.Shoot {

    private static final Set<Material> PROTECTED = new HashSet<>(Arrays.asList(
            Material.BEDROCK, Material.BARRIER, Material.END_PORTAL, Material.END_PORTAL_FRAME,
            Material.END_GATEWAY, Material.NETHER_PORTAL, Material.COMMAND_BLOCK,
            Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.STRUCTURE_BLOCK,
            Material.JIGSAW, Material.LIGHT, Material.MOVING_PISTON));

    public BombardaMaxima() {
        super("bombardamaxima", Material.BOW, "&4&lBombarda Maxima");
        category("Bronie");
        model(10007);
        lore("&7Po wystrzeleniu strzała eksploduje",
                "&7i niszczy każdy blok oprócz &cskały macierzystej&7.",
                "",
                "&8» &cAnarchia.GG");
        enchant("power", 3);
        enchant("unbreaking", 5);
        unbreakable();
        setting("radius", 4);
        setting("damage", 8.0D);
        setting("destroy-blocks", true);
        cooldown(6.0D);
    }

    @Override
    public void onShoot(Player player, ItemStack item, EntityShootBowEvent event) {
        if (!checkCooldown(player)) {
            event.setCancelled(true);
            return;
        }
        Sounds.play(player, Sounds.WITHER_SHOOT, 1.0F, 1.4F);
    }

    @Override
    public void onProjectileHit(Player shooter, Projectile projectile, ProjectileHitEvent event) {
        Location center = event.getHitBlock() != null
                ? event.getHitBlock().getLocation().add(0.5D, 0.5D, 0.5D)
                : projectile.getLocation();
        projectile.remove();
        int radius = Math.max(1, settingInt("radius"));
        if (settingBool("destroy-blocks") && plugin.regions().allowsExplosions(center)) {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x * x + y * y + z * z > radius * radius) {
                            continue;
                        }
                        Block block = center.clone().add(x, y, z).getBlock();
                        if (block.getType().isAir() || PROTECTED.contains(block.getType())) {
                            continue;
                        }
                        if (!plugin.regions().canBuild(shooter, block.getLocation())) {
                            continue;
                        }
                        block.setType(Material.AIR, false);
                    }
                }
            }
        }
        List<LivingEntity> targets = PlayerUtil.nearbyLiving(center, radius + 1.0D, shooter);
        for (LivingEntity target : targets) {
            PlayerUtil.knockback(target, center, 1.2D, 0.5D);
            DamageGuard.damage(target, settingDouble("damage"), shooter);
        }
        Particles.spawn(center, Particles.EXPLOSION_EMITTER, 3, 1.0D, 1.0D, 1.0D, 0.0D);
        Sounds.play(center, Sounds.EXPLODE, 2.0F, 0.8F);
    }
}
