package gg.anarchia.itemy.command;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/** /anarchiaitemy giveitem <id> [gracz] [ilosc] */
public class GiveItemCommand implements SubCommand {

    private final AnarchiaItemy plugin;

    public GiveItemCommand(AnarchiaItemy plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "giveitem";
    }

    @Override
    public String getDescription() {
        return "Daje przedmiot tobie lub innemu graczowi";
    }

    @Override
    public String getUsage() {
        return "giveitem <id> [gracz] [ilosc]";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            plugin.messages().send(sender, "item-not-found", "%item%", "?");
            return;
        }
        String id = args[0];
        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                plugin.messages().send(sender, "player-not-found", "%player%", args[1]);
                return;
            }
        } else if (sender instanceof Player player) {
            target = player;
        } else {
            plugin.messages().send(sender, "player-only");
            return;
        }
        int amount = 1;
        if (args.length > 2) {
            try {
                amount = Math.max(1, Math.min(64, Integer.parseInt(args[2])));
            } catch (NumberFormatException ignored) {
                amount = 1;
            }
        }

        List<CustomItem> toGive = new ArrayList<>();
        if (plugin.items().isGroup(id)) {
            toGive.addAll(plugin.items().groupItems(id));
        } else {
            CustomItem item = plugin.items().byId(id);
            if (item != null) {
                toGive.add(item);
            }
        }
        if (toGive.isEmpty()) {
            plugin.messages().send(sender, "item-not-found", "%item%", id);
            return;
        }
        String name = toGive.size() == 1 ? toGive.get(0).getDisplayName() : "&c" + id;
        for (CustomItem item : toGive) {
            ItemUtil.give(target, item.build(amount));
        }
        plugin.messages().send(target, "item-given", "%item%", name);
        Sounds.play(target, Sounds.ORB, 1.0F, 1.3F);
        if (!target.equals(sender)) {
            plugin.messages().send(sender, "item-given-other", "%item%", name, "%player%", target.getName());
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return plugin.items().ids();
        }
        if (args.length == 2) {
            List<String> names = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                names.add(player.getName());
            }
            return names;
        }
        if (args.length == 3) {
            return List.of("1", "8", "16", "32", "64");
        }
        return List.of();
    }
}
