package gg.anarchia.itemy.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class Tasks {

    private static Plugin plugin;

    private Tasks() {
    }

    public static void init(Plugin instance) {
        plugin = instance;
    }

    public static Plugin plugin() {
        return plugin;
    }

    public static BukkitTask run(Runnable runnable) {
        return Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static BukkitTask later(Runnable runnable, long delayTicks) {
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, Math.max(1L, delayTicks));
    }

    public static BukkitTask timer(Runnable runnable, long delayTicks, long periodTicks) {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, Math.max(0L, delayTicks), Math.max(1L, periodTicks));
    }

    public static void cancel(BukkitTask task) {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
}
