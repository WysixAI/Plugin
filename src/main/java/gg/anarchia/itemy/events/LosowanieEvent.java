package gg.anarchia.itemy.events;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/** Losowanie - kazdy gracz online dostaje losowa eventowke. */
public class LosowanieEvent extends GameEvent {

    public LosowanieEvent(AnarchiaItemy plugin) {
        super(plugin, "losowanie", "Losowanie", "Każdy gracz online otrzymuje losową eventowkę!", Material.ENDER_CHEST);
    }

    @Override
    public boolean start(CommandSender sender, String[] args) {
        List<String> rewards = config().getStringList("rewards");
        if (rewards.isEmpty()) {
            plugin.messages().send(sender, "event-unknown", "%events%", String.join(", ", plugin.events().ids()));
            return false;
        }
        running = true;
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack reward = plugin.events().randomReward(rewards);
            if (reward == null) {
                continue;
            }
            ItemUtil.give(player, reward);
            String name = reward.getType().name();
            org.bukkit.inventory.meta.ItemMeta meta = reward.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                name = Text.plain(meta.displayName());
            }
            plugin.messages().send(player, "event-losowanie", "%item%", name);
            Sounds.play(player, Sounds.ORB, 1.0F, 1.2F);
        }
        running = false;
        return true;
    }

    @Override
    public void stop() {
        running = false;
    }
}
