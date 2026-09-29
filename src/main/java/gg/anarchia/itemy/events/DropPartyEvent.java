package gg.anarchia.itemy.events;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Drop Party - z nieba spadaja eventowki. */
public class DropPartyEvent extends GameEvent {

    private BukkitTask task;
    private Location location;

    public DropPartyEvent(AnarchiaItemy plugin) {
        super(plugin, "dropparty", "Drop Party", "Z nieba spadają eventowki - łap co się da!", Material.CHEST);
    }

    @Override
    public boolean start(CommandSender sender, String[] args) {
        location = sender instanceof Player player
                ? player.getLocation().clone()
                : Bukkit.getWorlds().get(0).getSpawnLocation();
        int amount = config().getInt("amount", 40);
        long interval = Math.max(1L, config().getLong("interval", 10L));
        List<String> rewards = config().getStringList("rewards");
        running = true;
        plugin.messages().broadcast("event-dropparty-start");
        final int[] dropped = {0};
        task = Tasks.timer(() -> {
            if (!running || location == null || location.getWorld() == null) {
                stop();
                return;
            }
            if (dropped[0]++ >= amount) {
                plugin.events().stop();
                return;
            }
            ItemStack reward = plugin.events().randomReward(rewards);
            if (reward == null) {
                return;
            }
            double offsetX = ThreadLocalRandom.current().nextDouble(-4.0D, 4.0D);
            double offsetZ = ThreadLocalRandom.current().nextDouble(-4.0D, 4.0D);
            Location drop = location.clone().add(offsetX, 12.0D, offsetZ);
            location.getWorld().dropItem(drop, reward);
            Particles.spawn(drop, Particles.FIREWORK, 10);
            Sounds.play(drop, Sounds.FIREWORK, 0.6F, 1.4F);
        }, 40L, interval);
        return true;
    }

    @Override
    public void stop() {
        running = false;
        Tasks.cancel(task);
        task = null;
    }
}
