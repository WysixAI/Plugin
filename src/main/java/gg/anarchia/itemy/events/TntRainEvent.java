package gg.anarchia.itemy.events;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadLocalRandom;

/** TNT Rain - z nieba leci TNT. */
public class TntRainEvent extends GameEvent {

    private BukkitTask task;

    public TntRainEvent(AnarchiaItemy plugin) {
        super(plugin, "tntrain", "TNT Rain", "Z nieba leci TNT - uciekaj!", Material.TNT);
    }

    @Override
    public boolean start(CommandSender sender, String[] args) {
        running = true;
        int duration = Math.max(5, config().getInt("duration-seconds", 60));
        long interval = Math.max(5L, config().getLong("interval", 20L));
        int perWave = Math.max(1, config().getInt("per-wave", 6));
        double radius = Math.max(4.0D, config().getDouble("radius", 25.0D));
        int fuse = Math.max(10, config().getInt("fuse-ticks", 60));
        plugin.messages().broadcast("event-tntrain");
        final long endTime = System.currentTimeMillis() + duration * 1000L;
        task = Tasks.timer(() -> {
            if (!running || System.currentTimeMillis() > endTime) {
                plugin.events().stop();
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getGameMode() == org.bukkit.GameMode.SPECTATOR
                        || player.getGameMode() == org.bukkit.GameMode.CREATIVE) {
                    continue;
                }
                for (int i = 0; i < perWave; i++) {
                    double offsetX = ThreadLocalRandom.current().nextDouble(-radius, radius);
                    double offsetZ = ThreadLocalRandom.current().nextDouble(-radius, radius);
                    Location location = player.getLocation().clone().add(offsetX, 18.0D, offsetZ);
                    if (location.getWorld() == null) {
                        continue;
                    }
                    TNTPrimed tnt = location.getWorld().spawn(location, TNTPrimed.class, primed -> primed.setFuseTicks(fuse));
                    ItemUtil.tag(tnt, "tntrain");
                }
                Sounds.play(player, Sounds.THUNDER, 0.4F, 1.4F);
            }
        }, 20L, interval);
        return true;
    }

    @Override
    public void stop() {
        running = false;
        Tasks.cancel(task);
        task = null;
    }
}
