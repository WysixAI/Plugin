package gg.anarchia.itemy.util;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Cooldowns {

    private static final Map<UUID, Map<String, Long>> DATA = new HashMap<>();

    private Cooldowns() {
    }

    public static boolean isReady(Player player, String key) {
        Map<String, Long> map = DATA.get(player.getUniqueId());
        if (map == null) {
            return true;
        }
        Long until = map.get(key);
        return until == null || until <= System.currentTimeMillis();
    }

    public static double remaining(Player player, String key) {
        Map<String, Long> map = DATA.get(player.getUniqueId());
        if (map == null) {
            return 0.0D;
        }
        Long until = map.get(key);
        if (until == null) {
            return 0.0D;
        }
        return Math.max(0.0D, (until - System.currentTimeMillis()) / 1000.0D);
    }

    public static void set(Player player, String key, double seconds) {
        if (seconds <= 0) {
            return;
        }
        DATA.computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>())
                .put(key, System.currentTimeMillis() + (long) (seconds * 1000L));
    }

    public static void clear(Player player, String key) {
        Map<String, Long> map = DATA.get(player.getUniqueId());
        if (map != null) {
            map.remove(key);
        }
    }

    public static void clear(UUID uuid) {
        DATA.remove(uuid);
    }

    public static void clearAll() {
        DATA.clear();
    }

    public static String format(double seconds) {
        if (seconds >= 10) {
            return String.valueOf((int) Math.ceil(seconds));
        }
        return String.format(java.util.Locale.ROOT, "%.1f", seconds);
    }
}
