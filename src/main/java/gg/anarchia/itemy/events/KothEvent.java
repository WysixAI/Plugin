package gg.anarchia.itemy.events;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.region.Region;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Tasks;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** KOTH - utrzymaj region przez okreslony czas, aby wygrac. */
public class KothEvent extends GameEvent {

    private BukkitTask task;
    private Region region;
    private final Map<UUID, Integer> progress = new HashMap<>();

    public KothEvent(AnarchiaItemy plugin) {
        super(plugin, "koth", "KOTH", "Utrzymaj punkt najdłużej, aby zgarnąć nagrodę!", Material.GOLDEN_HELMET);
    }

    @Override
    public boolean start(CommandSender sender, String[] args) {
        String name = args.length > 0 ? args[0] : null;
        if (name == null && sender instanceof Player player) {
            Region current = plugin.regions().at(player.getLocation());
            name = current == null ? null : current.getName();
        }
        region = plugin.regions().get(name);
        if (region == null) {
            plugin.messages().send(sender, "event-koth-need-region");
            return false;
        }
        progress.clear();
        running = true;
        int required = Math.max(5, config().getInt("capture-seconds", 120));
        title("&c&lKOTH", "&7Punkt: &f" + region.getName());
        task = Tasks.timer(() -> {
            if (!running || region == null) {
                return;
            }
            Player leader = null;
            int inside = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!region.contains(player.getLocation()) || player.isDead()) {
                    continue;
                }
                inside++;
                leader = player;
            }
            if (inside != 1 || leader == null) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (region.contains(player.getLocation())) {
                        Text.actionBar(player, "&c&lKOTH &8» &7Punkt jest kontestowany!");
                    }
                }
                return;
            }
            int seconds = progress.merge(leader.getUniqueId(), 1, Integer::sum);
            Text.actionBar(leader, "&c&lKOTH &8» &7Przejmowanie: &c" + seconds + "&7/&c" + required + "s");
            Particles.spawn(leader.getLocation().add(0, 1.0D, 0), Particles.HAPPY_VILLAGER, 5);
            if (seconds % 15 == 0) {
                plugin.messages().broadcast("event-koth-capture", "%player%", leader.getName(), "%time%", String.valueOf(required - seconds));
            }
            if (seconds >= required) {
                win(leader);
            }
        }, 20L, 20L);
        return true;
    }

    private void win(Player player) {
        plugin.messages().broadcast("event-koth-win", "%player%", player.getName());
        ItemStack reward = plugin.events().parseReward(config().getString("reward", "koronaanarchi"));
        if (reward != null) {
            ItemUtil.give(player, reward);
        }
        Sounds.play(player, Sounds.LEVEL_UP, 1.0F, 1.0F);
        Particles.spawn(player.getLocation(), Particles.TOTEM, 60, 1.0D, 1.0D, 1.0D, 0.4D);
        plugin.events().stop();
    }

    @Override
    public void stop() {
        running = false;
        Tasks.cancel(task);
        task = null;
        progress.clear();
    }
}
