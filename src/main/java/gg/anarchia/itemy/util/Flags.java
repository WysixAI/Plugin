package gg.anarchia.itemy.util;

import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Parsowanie flag przedmiotow z configu.
 * Obsluguje rowniez stare nazwy (np. HIDE_POTION_EFFECTS = HIDE_ADDITIONAL_TOOLTIP w 1.20.5+).
 */
public final class Flags {

    private static final String[][] ALIASES = {
            {"HIDE_POTION_EFFECTS", "HIDE_ADDITIONAL_TOOLTIP"},
            {"HIDE_ADDITIONAL_TOOLTIP", "HIDE_POTION_EFFECTS"},
            {"HIDE_EFFECTS", "HIDE_ADDITIONAL_TOOLTIP"},
    };

    private Flags() {
    }

    public static ItemFlag parse(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        String normalized = name.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        ItemFlag direct = find(normalized);
        if (direct != null) {
            return direct;
        }
        for (String[] alias : ALIASES) {
            if (alias[0].equals(normalized)) {
                ItemFlag flag = find(alias[1]);
                if (flag != null) {
                    return flag;
                }
            }
        }
        return null;
    }

    private static ItemFlag find(String name) {
        try {
            return ItemFlag.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static List<ItemFlag> parse(List<String> names) {
        List<ItemFlag> flags = new ArrayList<>();
        if (names == null) {
            return flags;
        }
        for (String name : names) {
            ItemFlag flag = parse(name);
            if (flag != null && !flags.contains(flag)) {
                flags.add(flag);
            }
        }
        return flags;
    }
}
