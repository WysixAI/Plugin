package gg.anarchia.itemy.util;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Mapowanie nazw zaklec z configu na obiekty Enchantment.
 */
public final class Enchants {

    private static final Map<String, Enchantment> BY_NAME = new HashMap<>();

    static {
        put("protection", Enchantment.PROTECTION);
        put("fire_protection", Enchantment.FIRE_PROTECTION);
        put("feather_falling", Enchantment.FEATHER_FALLING);
        put("blast_protection", Enchantment.BLAST_PROTECTION);
        put("projectile_protection", Enchantment.PROJECTILE_PROTECTION);
        put("respiration", Enchantment.RESPIRATION);
        put("aqua_affinity", Enchantment.AQUA_AFFINITY);
        put("thorns", Enchantment.THORNS);
        put("depth_strider", Enchantment.DEPTH_STRIDER);
        put("frost_walker", Enchantment.FROST_WALKER);
        put("soul_speed", Enchantment.SOUL_SPEED);
        put("swift_sneak", Enchantment.SWIFT_SNEAK);
        put("sharpness", Enchantment.SHARPNESS);
        put("smite", Enchantment.SMITE);
        put("bane_of_arthropods", Enchantment.BANE_OF_ARTHROPODS);
        put("knockback", Enchantment.KNOCKBACK);
        put("fire_aspect", Enchantment.FIRE_ASPECT);
        put("looting", Enchantment.LOOTING);
        put("sweeping_edge", Enchantment.SWEEPING_EDGE);
        put("efficiency", Enchantment.EFFICIENCY);
        put("silk_touch", Enchantment.SILK_TOUCH);
        put("unbreaking", Enchantment.UNBREAKING);
        put("fortune", Enchantment.FORTUNE);
        put("power", Enchantment.POWER);
        put("punch", Enchantment.PUNCH);
        put("flame", Enchantment.FLAME);
        put("infinity", Enchantment.INFINITY);
        put("luck_of_the_sea", Enchantment.LUCK_OF_THE_SEA);
        put("lure", Enchantment.LURE);
        put("loyalty", Enchantment.LOYALTY);
        put("impaling", Enchantment.IMPALING);
        put("riptide", Enchantment.RIPTIDE);
        put("channeling", Enchantment.CHANNELING);
        put("multishot", Enchantment.MULTISHOT);
        put("quick_charge", Enchantment.QUICK_CHARGE);
        put("piercing", Enchantment.PIERCING);
        put("mending", Enchantment.MENDING);
        put("binding_curse", Enchantment.BINDING_CURSE);
        put("vanishing_curse", Enchantment.VANISHING_CURSE);
        put("density", Enchantment.DENSITY);
        put("breach", Enchantment.BREACH);
        put("wind_burst", Enchantment.WIND_BURST);
    }

    private Enchants() {
    }

    private static void put(String name, Enchantment enchantment) {
        if (enchantment != null) {
            BY_NAME.put(name, enchantment);
        }
    }

    public static Enchantment byName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        String key = name.toLowerCase(Locale.ROOT).replace(' ', '_');
        Enchantment enchantment = BY_NAME.get(key);
        if (enchantment != null) {
            return enchantment;
        }
        try {
            return Enchantment.getByKey(NamespacedKey.minecraft(key));
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static String keyOf(Enchantment enchantment) {
        if (enchantment == null) {
            return "unknown";
        }
        return enchantment.getKey().getKey();
    }

    public static String prettyName(Enchantment enchantment) {
        String key = keyOf(enchantment).replace('_', ' ');
        String[] parts = key.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(' ');
        }
        return builder.toString().trim();
    }

    public static String roman(int number) {
        String[] romans = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        if (number >= 0 && number < romans.length) {
            return romans[number];
        }
        return String.valueOf(number);
    }
}
