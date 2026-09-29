package gg.anarchia.itemy.region;

import gg.anarchia.itemy.AnarchiaItemy;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/** Zarzadzanie regionami (regions.yml) oraz sprawdzanie flag. */
public class RegionManager {

    public static final List<String> FLAGS = Arrays.asList("pvp", "build", "interact", "items", "explosions", "enter");

    private final AnarchiaItemy plugin;
    private final Map<String, Region> regions = new LinkedHashMap<>();
    private final Map<UUID, Location[]> selections = new HashMap<>();
    private final Map<UUID, String> lastRegion = new HashMap<>();

    public RegionManager(AnarchiaItemy plugin) {
        this.plugin = plugin;
        load();
    }

    public final void load() {
        regions.clear();
        FileConfiguration configuration = plugin.configs().regions();
        ConfigurationSection root = configuration.getConfigurationSection("regions");
        if (root == null) {
            return;
        }
        for (String name : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(name);
            if (section == null) {
                continue;
            }
            try {
                regions.put(name.toLowerCase(Locale.ROOT), Region.load(name, section));
            } catch (Exception exception) {
                plugin.getLogger().warning("Nie udalo sie wczytac regionu " + name + ": " + exception.getMessage());
            }
        }
    }

    public void save() {
        FileConfiguration configuration = plugin.configs().regions();
        configuration.set("regions", null);
        for (Region region : regions.values()) {
            ConfigurationSection section = configuration.createSection("regions." + region.getName());
            region.save(section);
        }
        plugin.configs().saveRegions();
    }

    public boolean enabled() {
        return plugin.getConfig().getBoolean("regions.enabled", true);
    }

    public Collection<Region> all() {
        return new ArrayList<>(regions.values());
    }

    public Region get(String name) {
        return name == null ? null : regions.get(name.toLowerCase(Locale.ROOT));
    }

    public Region at(Location location) {
        if (!enabled() || location == null) {
            return null;
        }
        Region best = null;
        for (Region region : regions.values()) {
            if (!region.contains(location)) {
                continue;
            }
            if (best == null || region.getPriority() > best.getPriority()) {
                best = region;
            }
        }
        return best;
    }

    public Region create(String name, Location first, Location second) {
        Region region = new Region(name, first, second);
        ConfigurationSection defaults = plugin.getConfig().getConfigurationSection("regions.default-flags");
        if (defaults != null) {
            for (String key : defaults.getKeys(false)) {
                region.setFlag(key, defaults.getBoolean(key));
            }
        }
        for (String flag : FLAGS) {
            if (!region.getFlags().containsKey(flag)) {
                region.setFlag(flag, !flag.equals("build") && !flag.equals("explosions"));
            }
        }
        regions.put(name.toLowerCase(Locale.ROOT), region);
        save();
        return region;
    }

    public boolean delete(String name) {
        if (name == null || regions.remove(name.toLowerCase(Locale.ROOT)) == null) {
            return false;
        }
        save();
        return true;
    }

    // ------------------------------------------------------------------
    //  Zaznaczanie
    // ------------------------------------------------------------------

    public void setSelection(Player player, int index, Location location) {
        Location[] selection = selections.computeIfAbsent(player.getUniqueId(), uuid -> new Location[2]);
        selection[index] = location;
    }

    public Location getSelection(Player player, int index) {
        Location[] selection = selections.get(player.getUniqueId());
        return selection == null ? null : selection[index];
    }

    public boolean hasSelection(Player player) {
        return getSelection(player, 0) != null && getSelection(player, 1) != null;
    }

    public void clearSelection(Player player) {
        selections.remove(player.getUniqueId());
    }

    public String getLastRegion(Player player) {
        return lastRegion.get(player.getUniqueId());
    }

    public void setLastRegion(Player player, String name) {
        if (name == null) {
            lastRegion.remove(player.getUniqueId());
        } else {
            lastRegion.put(player.getUniqueId(), name);
        }
    }

    // ------------------------------------------------------------------
    //  Flagi
    // ------------------------------------------------------------------

    private boolean bypass(Player player) {
        return player != null && player.hasPermission("iAnarchiaitemy.bypass.region");
    }

    public boolean check(Player player, Location location, String flag, boolean def) {
        if (!enabled()) {
            return true;
        }
        if (bypass(player)) {
            return true;
        }
        Region region = at(location);
        return region == null || region.getFlag(flag, def);
    }

    public boolean canBuild(Player player, Location location) {
        return check(player, location, "build", true);
    }

    public boolean canUseItems(Player player, Location location) {
        return check(player, location, "items", true);
    }

    public boolean canPvp(Player player, Location location) {
        return check(player, location, "pvp", true);
    }

    public boolean canInteract(Player player, Location location) {
        return check(player, location, "interact", true);
    }

    public boolean allowsExplosions(Location location) {
        if (!enabled()) {
            return true;
        }
        Region region = at(location);
        return region == null || region.getFlag("explosions", true);
    }

    public boolean canEnter(Player player, Location location) {
        return check(player, location, "enter", true);
    }
}
