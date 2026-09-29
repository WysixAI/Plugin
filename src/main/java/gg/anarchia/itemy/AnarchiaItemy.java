package gg.anarchia.itemy;

import gg.anarchia.itemy.command.MainCommand;
import gg.anarchia.itemy.config.ConfigManager;
import gg.anarchia.itemy.config.Messages;
import gg.anarchia.itemy.events.EventManager;
import gg.anarchia.itemy.gui.Gui;
import gg.anarchia.itemy.item.ItemManager;
import gg.anarchia.itemy.listener.BookListener;
import gg.anarchia.itemy.listener.GuiListener;
import gg.anarchia.itemy.listener.ItemListener;
import gg.anarchia.itemy.listener.MobListener;
import gg.anarchia.itemy.listener.RegionListener;
import gg.anarchia.itemy.region.RegionManager;
import gg.anarchia.itemy.util.BlockRestore;
import gg.anarchia.itemy.util.Cooldowns;
import gg.anarchia.itemy.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * AnarchiaItemy - eventowki (przedmioty eventowe) w stylu Anarchia.GG.
 *
 * <p>Uwaga: aby przedmioty posiadaly tekstury, potrzebny jest oryginalny
 * teksturepack z serwera Anarchia.GG (discord.gg/yfnDmhhv5Q).</p>
 */
public final class AnarchiaItemy extends JavaPlugin {

    private static AnarchiaItemy instance;

    private ConfigManager configs;
    private Messages messages;
    private ItemManager items;
    private RegionManager regions;
    private EventManager events;
    private BlockRestore blocks;

    public static AnarchiaItemy get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Tasks.init(this);
        Keys.init(this);

        configs = new ConfigManager(this);
        messages = new Messages(this, configs);
        blocks = new BlockRestore();
        regions = new RegionManager(this);
        items = new ItemManager(this);
        items.load();
        events = new EventManager(this);

        registerListeners();
        registerCommands();
        items.startTask();

        getLogger().info("=================================");
        getLogger().info(" AnarchiaItemy v" + getDescription().getVersion());
        getLogger().info(" Zaladowano " + items.all().size() + " eventowek");
        getLogger().info(" Regionow: " + regions.all().size() + ", eventow: " + events.all().size());
        getLogger().info(" Tekstury: teksturepack z Anarchia.GG");
        getLogger().info("=================================");
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof Gui) {
                player.closeInventory();
            }
        }
        if (events != null) {
            events.shutdown();
        }
        if (items != null) {
            items.shutdown();
        }
        if (blocks != null) {
            blocks.restoreAll();
        }
        if (regions != null) {
            regions.save();
        }
        Cooldowns.clearAll();
        getLogger().info("AnarchiaItemy wylaczony. Do zobaczenia na Anarchia.GG!");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GuiListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BookListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MobListener(this), this);
        Bukkit.getPluginManager().registerEvents(new RegionListener(this), this);
    }

    private void registerCommands() {
        PluginCommand command = getCommand("anarchiaitemy");
        if (command == null) {
            getLogger().severe("Nie znaleziono komendy /anarchiaitemy w plugin.yml!");
            return;
        }
        MainCommand executor = new MainCommand(this);
        command.setExecutor(executor);
        command.setTabCompleter(executor);
    }

    /** Przeladowuje wszystkie pliki konfiguracyjne. */
    public void reloadEverything() {
        configs.reload();
        messages.load();
        items.load();
        regions.load();
        items.startTask();
    }

    public boolean isWorldEnabled(World world) {
        if (world == null) {
            return false;
        }
        List<String> worlds = getConfig().getStringList("settings.enabled-worlds");
        return worlds.isEmpty() || worlds.contains(world.getName());
    }

    public ConfigManager configs() {
        return configs;
    }

    public Messages messages() {
        return messages;
    }

    public ItemManager items() {
        return items;
    }

    public RegionManager regions() {
        return regions;
    }

    public EventManager events() {
        return events;
    }

    public BlockRestore blocks() {
        return blocks;
    }
}
