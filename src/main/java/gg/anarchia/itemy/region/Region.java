package gg.anarchia.itemy.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashMap;
import java.util.Map;

/** Prostopadloscienny region z flagami. */
public class Region {

    private final String name;
    private String world;
    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;
    private int priority;
    private final Map<String, Boolean> flags = new LinkedHashMap<>();

    public Region(String name, Location first, Location second) {
        this.name = name;
        this.world = first.getWorld() == null ? "world" : first.getWorld().getName();
        this.minX = Math.min(first.getBlockX(), second.getBlockX());
        this.minY = Math.min(first.getBlockY(), second.getBlockY());
        this.minZ = Math.min(first.getBlockZ(), second.getBlockZ());
        this.maxX = Math.max(first.getBlockX(), second.getBlockX());
        this.maxY = Math.max(first.getBlockY(), second.getBlockY());
        this.maxZ = Math.max(first.getBlockZ(), second.getBlockZ());
    }

    private Region(String name) {
        this.name = name;
    }

    public static Region load(String name, ConfigurationSection section) {
        Region region = new Region(name);
        region.world = section.getString("world", "world");
        region.minX = section.getInt("min.x");
        region.minY = section.getInt("min.y");
        region.minZ = section.getInt("min.z");
        region.maxX = section.getInt("max.x");
        region.maxY = section.getInt("max.y");
        region.maxZ = section.getInt("max.z");
        region.priority = section.getInt("priority", 0);
        ConfigurationSection flagSection = section.getConfigurationSection("flags");
        if (flagSection != null) {
            for (String key : flagSection.getKeys(false)) {
                region.flags.put(key.toLowerCase(java.util.Locale.ROOT), flagSection.getBoolean(key));
            }
        }
        return region;
    }

    public void save(ConfigurationSection section) {
        section.set("world", world);
        section.set("min.x", minX);
        section.set("min.y", minY);
        section.set("min.z", minZ);
        section.set("max.x", maxX);
        section.set("max.y", maxY);
        section.set("max.z", maxZ);
        section.set("priority", priority);
        for (Map.Entry<String, Boolean> entry : flags.entrySet()) {
            section.set("flags." + entry.getKey(), entry.getValue());
        }
    }

    public boolean contains(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }
        if (!location.getWorld().getName().equalsIgnoreCase(world)) {
            return false;
        }
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public Location center() {
        World bukkitWorld = Bukkit.getWorld(world);
        if (bukkitWorld == null) {
            return null;
        }
        return new Location(bukkitWorld,
                (minX + maxX) / 2.0D + 0.5D,
                Math.max(minY, maxY) + 1.0D,
                (minZ + maxZ) / 2.0D + 0.5D);
    }

    public boolean getFlag(String flag, boolean def) {
        return flags.getOrDefault(flag.toLowerCase(java.util.Locale.ROOT), def);
    }

    public void setFlag(String flag, boolean value) {
        flags.put(flag.toLowerCase(java.util.Locale.ROOT), value);
    }

    public Map<String, Boolean> getFlags() {
        return flags;
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int volume() {
        return (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
    }
}
