package gg.anarchia.itemy.config;

import gg.anarchia.itemy.AnarchiaItemy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Obsluga plikow konfiguracyjnych: config.yml, items.yml, messages.yml, regions.yml
 */
public class ConfigManager {

    private final AnarchiaItemy plugin;

    private File itemsFile;
    private FileConfiguration items;
    private File messagesFile;
    private FileConfiguration messages;
    private File regionsFile;
    private FileConfiguration regions;

    public ConfigManager(AnarchiaItemy plugin) {
        this.plugin = plugin;
        load();
    }

    public final void load() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Nie udalo sie utworzyc folderu pluginu!");
        }
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        itemsFile = new File(plugin.getDataFolder(), "items.yml");
        items = YamlConfiguration.loadConfiguration(itemsFile);

        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);

        regionsFile = new File(plugin.getDataFolder(), "regions.yml");
        regions = YamlConfiguration.loadConfiguration(regionsFile);
    }

    public void reload() {
        load();
    }

    public FileConfiguration main() {
        return plugin.getConfig();
    }

    public FileConfiguration items() {
        return items;
    }

    public FileConfiguration messages() {
        return messages;
    }

    public FileConfiguration regions() {
        return regions;
    }

    public void saveItems() {
        save(items, itemsFile);
    }

    public void saveMessages() {
        save(messages, messagesFile);
    }

    public void saveRegions() {
        save(regions, regionsFile);
    }

    private void save(FileConfiguration configuration, File file) {
        if (configuration == null || file == null) {
            return;
        }
        try {
            configuration.save(file);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Nie udalo sie zapisac pliku " + file.getName(), exception);
        }
    }
}
