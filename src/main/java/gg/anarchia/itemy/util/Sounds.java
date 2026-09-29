package gg.anarchia.itemy.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Dzwieki wolane po kluczu (String) - dzieki temu plugin nie zalezy od enuma Sound,
 * ktory zmienia sie pomiedzy wersjami Minecrafta.
 */
public final class Sounds {

    public static final String EXPLODE = "entity.generic.explode";
    public static final String ANVIL_LAND = "block.anvil.land";
    public static final String ENDER_TP = "entity.enderman.teleport";
    public static final String LEVEL_UP = "entity.player.levelup";
    public static final String ORB = "entity.experience_orb.pickup";
    public static final String THUNDER = "entity.lightning_bolt.thunder";
    public static final String IMPACT = "entity.lightning_bolt.impact";
    public static final String DRAGON_GROWL = "entity.ender_dragon.growl";
    public static final String TOTEM = "item.totem.use";
    public static final String GLASS = "block.glass.break";
    public static final String FIREWORK = "entity.firework_rocket.blast";
    public static final String BURP = "entity.player.burp";
    public static final String EAT = "entity.generic.eat";
    public static final String DRINK = "entity.generic.drink";
    public static final String ILLUSIONER_CAST = "entity.illusioner.cast_spell";
    public static final String EVOKER_FANGS = "entity.evoker_fangs.attack";
    public static final String GUARDIAN_AMBIENT = "entity.elder_guardian.ambient";
    public static final String SPLASH = "entity.generic.splash";
    public static final String BELL = "block.note_block.bell";
    public static final String PLING = "block.note_block.pling";
    public static final String SHIELD_BLOCK = "item.shield.block";
    public static final String ARROW_HIT = "entity.arrow.hit_player";
    public static final String WITHER_SHOOT = "entity.wither.shoot";
    public static final String SNOW_BREAK = "block.snow.break";
    public static final String CHAIN = "block.chain.place";
    public static final String WATER = "item.bucket.empty";
    public static final String FISHING_RETRIEVE = "entity.fishing_bobber.retrieve";

    private Sounds() {
    }

    public static void play(Location location, String key, float volume, float pitch) {
        if (location == null || location.getWorld() == null || key == null) {
            return;
        }
        location.getWorld().playSound(location, key, volume, pitch);
    }

    public static void play(Player player, String key, float volume, float pitch) {
        if (player == null || key == null) {
            return;
        }
        player.playSound(player.getLocation(), key, volume, pitch);
    }
}
