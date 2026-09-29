package gg.anarchia.itemy.util;

import gg.anarchia.itemy.AnarchiaItemy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Budowanie tymczasowych konstrukcji (pulapki, klatki wodne, domki).
 */
public final class Structures {

    private Structures() {
    }

    public static boolean replaceable(Block block) {
        Material type = block.getType();
        return type.isAir() || type == Material.WATER || type == Material.LAVA || !type.isSolid();
    }

    private static void place(AnarchiaItemy plugin, Player owner, Block block, Material material, long ticks) {
        if (block == null || material == null) {
            return;
        }
        if (!plugin.regions().canBuild(owner, block.getLocation())) {
            return;
        }
        if (!replaceable(block)) {
            return;
        }
        plugin.blocks().set(block, material, ticks);
    }

    /** Pusty w srodku prostopadloscian (sciany + podloga + dach). */
    public static void box(AnarchiaItemy plugin, Player owner, Location center, int radius, int height,
                           Material wall, Material roof, Material floor, long ticks) {
        if (center == null || center.getWorld() == null) {
            return;
        }
        Location base = center.clone();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -1; y <= height; y++) {
                    boolean edge = Math.abs(x) == radius || Math.abs(z) == radius;
                    Block block = base.clone().add(x, y, z).getBlock();
                    if (y == -1) {
                        place(plugin, owner, block, floor, ticks);
                    } else if (y == height) {
                        place(plugin, owner, block, roof, ticks);
                    } else if (edge) {
                        place(plugin, owner, block, wall, ticks);
                    }
                }
            }
        }
    }

    /** Klatka 3x3x3 wokol podanego miejsca (srodek pozostaje pusty). */
    public static void cage(AnarchiaItemy plugin, Player owner, Location center, Material material, Material window, long ticks) {
        if (center == null || center.getWorld() == null) {
            return;
        }
        Location base = center.clone();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -1; z <= 1; z++) {
                    boolean shell = Math.abs(x) == 1 || Math.abs(z) == 1 || y == -1 || y == 2;
                    if (!shell) {
                        continue;
                    }
                    Block block = base.clone().add(x, y, z).getBlock();
                    boolean corner = Math.abs(x) == 1 && Math.abs(z) == 1;
                    place(plugin, owner, block, corner ? material : window, ticks);
                }
            }
        }
    }

    /** Kula z podanego materialu (np. woda). */
    public static void sphere(AnarchiaItemy plugin, Player owner, Location center, double radius, Material material, long ticks) {
        if (center == null || center.getWorld() == null) {
            return;
        }
        int r = (int) Math.ceil(radius);
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    if (x * x + y * y + z * z > radius * radius) {
                        continue;
                    }
                    place(plugin, owner, center.clone().add(x, y, z).getBlock(), material, ticks);
                }
            }
        }
    }
}
