package gg.anarchia.itemy.util;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public final class PlayerUtil {

    private PlayerUtil() {
    }

    public static double getMaxHealth(LivingEntity entity) {
        if (entity == null) {
            return 20.0D;
        }
        AttributeInstance attribute = entity.getAttribute(Attribute.MAX_HEALTH);
        return attribute == null ? 20.0D : attribute.getValue();
    }

    public static void heal(LivingEntity entity, double amount) {
        if (entity == null) {
            return;
        }
        double max = getMaxHealth(entity);
        entity.setHealth(Math.max(0.0D, Math.min(max, entity.getHealth() + amount)));
    }

    public static void healFull(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        entity.setHealth(getMaxHealth(entity));
        if (entity instanceof Player player) {
            player.setFoodLevel(20);
            player.setSaturation(20.0F);
        }
    }

    /** Czy mozna traktowac ta osobe jako przeciwnika (nie spectator/creative). */
    public static boolean isTargetable(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living.isDead()) {
            return false;
        }
        if (entity instanceof Player player) {
            return player.getGameMode() != GameMode.SPECTATOR && player.getGameMode() != GameMode.CREATIVE;
        }
        return true;
    }

    public static List<Player> nearbyPlayers(Location center, double radius, Player exclude) {
        List<Player> found = new ArrayList<>();
        if (center == null || center.getWorld() == null) {
            return found;
        }
        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(entity instanceof Player player)) {
                continue;
            }
            if (exclude != null && player.getUniqueId().equals(exclude.getUniqueId())) {
                continue;
            }
            if (!isTargetable(player)) {
                continue;
            }
            found.add(player);
        }
        return found;
    }

    public static List<LivingEntity> nearbyLiving(Location center, double radius, Entity exclude) {
        List<LivingEntity> found = new ArrayList<>();
        if (center == null || center.getWorld() == null) {
            return found;
        }
        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            if (exclude != null && entity.getUniqueId().equals(exclude.getUniqueId())) {
                continue;
            }
            if (!isTargetable(living)) {
                continue;
            }
            found.add(living);
        }
        return found;
    }

    public static void knockback(Entity entity, Location from, double power, double vertical) {
        if (entity == null || from == null || entity.getLocation().getWorld() == null) {
            return;
        }
        Vector direction = entity.getLocation().toVector().subtract(from.toVector());
        if (direction.lengthSquared() < 0.01D) {
            direction = new Vector(Math.random() - 0.5D, 0, Math.random() - 0.5D);
        }
        direction = direction.normalize().multiply(power).setY(vertical);
        entity.setVelocity(direction);
    }

    public static void pull(Entity entity, Location to, double power) {
        if (entity == null || to == null) {
            return;
        }
        Vector direction = to.toVector().subtract(entity.getLocation().toVector());
        if (direction.lengthSquared() < 0.01D) {
            return;
        }
        entity.setVelocity(direction.normalize().multiply(power).setY(Math.min(1.0D, direction.getY() * 0.15D + 0.35D)));
    }

    public static String name(Player player) {
        return player == null ? "?" : player.getName();
    }
}
