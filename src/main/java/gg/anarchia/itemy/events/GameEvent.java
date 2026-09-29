package gg.anarchia.itemy.events;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

/** Bazowa klasa eventu serwerowego. */
public abstract class GameEvent {

    protected final AnarchiaItemy plugin;
    private final String id;
    private final String name;
    private final String description;
    private final Material icon;
    protected boolean running;

    protected GameEvent(AnarchiaItemy plugin, String id, String name, String description, Material icon) {
        this.plugin = plugin;
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Material getIcon() {
        return icon;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("events." + id + ".enabled", true);
    }

    protected ConfigurationSection config() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("events." + id);
        return section == null ? plugin.getConfig().createSection("events." + id) : section;
    }

    protected void announce(String message) {
        String prefix = plugin.getConfig().getString("events.broadcast-prefix", "&8[&c&lEVENT&8] &r");
        for (String line : message.split("\n")) {
            Bukkit.broadcast(Text.parse(prefix + line));
        }
    }

    protected void title(String title, String subtitle) {
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            Text.title(player, title, subtitle, 10, 50, 10);
        }
    }

    /** Uruchamia event. Zwraca false gdy sie nie udalo (np. zle argumenty). */
    public abstract boolean start(CommandSender sender, String[] args);

    /** Zatrzymuje event. */
    public abstract void stop();
}
