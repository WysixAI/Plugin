package gg.anarchia.itemy.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

/**
 * Czastki rozwiazywane w runtime po nazwie (nazwy zmienialy sie w 1.20.5+),
 * dzieki czemu plugin dziala na wielu wersjach serwera.
 */
public final class Particles {

    public static final Particle EXPLOSION = get("EXPLOSION", "EXPLOSION_LARGE");
    public static final Particle EXPLOSION_EMITTER = get("EXPLOSION_EMITTER", "EXPLOSION_HUGE");
    public static final Particle POOF = get("POOF", "EXPLOSION_NORMAL");
    public static final Particle CLOUD = get("CLOUD");
    public static final Particle FLAME = get("FLAME");
    public static final Particle SOUL_FIRE_FLAME = get("SOUL_FIRE_FLAME");
    public static final Particle HEART = get("HEART");
    public static final Particle SNOWFLAKE = get("SNOWFLAKE", "ITEM_SNOWBALL");
    public static final Particle CRIT = get("CRIT");
    public static final Particle ENCHANTED_HIT = get("ENCHANTED_HIT", "CRIT_MAGIC");
    public static final Particle PORTAL = get("PORTAL");
    public static final Particle DRAGON_BREATH = get("DRAGON_BREATH");
    public static final Particle WITCH = get("WITCH", "SPELL_WITCH");
    public static final Particle HAPPY_VILLAGER = get("HAPPY_VILLAGER", "VILLAGER_HAPPY");
    public static final Particle ANGRY_VILLAGER = get("ANGRY_VILLAGER", "VILLAGER_ANGRY");
    public static final Particle SPLASH = get("SPLASH", "WATER_SPLASH");
    public static final Particle BUBBLE = get("BUBBLE", "WATER_BUBBLE");
    public static final Particle ELECTRIC_SPARK = get("ELECTRIC_SPARK");
    public static final Particle END_ROD = get("END_ROD");
    public static final Particle SMOKE = get("SMOKE", "SMOKE_NORMAL");
    public static final Particle LARGE_SMOKE = get("LARGE_SMOKE", "SMOKE_LARGE");
    public static final Particle FIREWORK = get("FIREWORK", "FIREWORKS_SPARK");
    public static final Particle TOTEM = get("TOTEM_OF_UNDYING", "TOTEM");
    public static final Particle SWEEP = get("SWEEP_ATTACK");
    public static final Particle NOTE = get("NOTE");
    public static final Particle SCULK_SOUL = get("SCULK_SOUL", "SOUL");

    private Particles() {
    }

    public static Particle get(String... names) {
        for (String name : names) {
            try {
                return Particle.valueOf(name);
            } catch (IllegalArgumentException | NoSuchFieldError ignored) {
                // nastepna nazwa
            }
        }
        return null;
    }

    public static void spawn(Location location, Particle particle, int count, double offsetX, double offsetY, double offsetZ, double speed) {
        if (particle == null || location == null) {
            return;
        }
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
    }

    public static void spawn(Location location, Particle particle, int count) {
        spawn(location, particle, count, 0.3D, 0.3D, 0.3D, 0.02D);
    }

    public static void circle(Location center, Particle particle, double radius, int points) {
        if (particle == null || center == null || center.getWorld() == null) {
            return;
        }
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI * i) / points;
            Location point = center.clone().add(Math.cos(angle) * radius, 0.2D, Math.sin(angle) * radius);
            center.getWorld().spawnParticle(particle, point, 1, 0, 0, 0, 0);
        }
    }

    public static void line(Location from, Location to, Particle particle, double step) {
        if (particle == null || from == null || to == null || from.getWorld() == null) {
            return;
        }
        if (!from.getWorld().equals(to.getWorld())) {
            return;
        }
        double distance = from.distance(to);
        if (distance <= 0.01D) {
            return;
        }
        org.bukkit.util.Vector direction = to.toVector().subtract(from.toVector()).normalize().multiply(step);
        Location current = from.clone();
        for (double travelled = 0; travelled < distance; travelled += step) {
            from.getWorld().spawnParticle(particle, current, 1, 0, 0, 0, 0);
            current.add(direction);
        }
    }
}
