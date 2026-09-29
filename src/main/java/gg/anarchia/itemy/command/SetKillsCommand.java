package gg.anarchia.itemy.command;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.items.Excalibur;
import gg.anarchia.itemy.util.Enchants;
import gg.anarchia.itemy.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** /anarchiaitemy setkills <ilosc> [gracz] */
public class SetKillsCommand implements SubCommand {

    private final AnarchiaItemy plugin;

    public SetKillsCommand(AnarchiaItemy plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "setkills";
    }

    @Override
    public String getDescription() {
        return "Ustawia liczbe zabojstw na Excaliburze";
    }

    @Override
    public String getUsage() {
        return "setkills <ilosc> [gracz]";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            plugin.messages().send(sender, "kills-usage");
            return;
        }
        int kills;
        try {
            kills = Math.max(0, Integer.parseInt(args[0]));
        } catch (NumberFormatException exception) {
            plugin.messages().send(sender, "kills-usage");
            return;
        }
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
        CustomItem custom = plugin.items().byId("excalibur");
        if (!(custom instanceof Excalibur excalibur)) {
            plugin.messages().send(sender, "item-not-found", "%item%", "excalibur");
            return;
        }
        ItemStack hand = target.getInventory().getItemInMainHand();
        if (!excalibur.matches(hand)) {
            hand = ItemUtil.findInInventory(target, "excalibur");
        }
        if (hand == null || !excalibur.matches(hand)) {
            plugin.messages().send(sender, "kills-not-excalibur");
            return;
        }
        excalibur.applyKills(hand, kills);
        target.updateInventory();
        plugin.messages().send(sender, "kills-set",
                "%kills%", String.valueOf(kills),
                "%level%", Enchants.roman(excalibur.levelFor(kills)));
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("0", "5", "10", "25", "50");
        }
        if (args.length == 2) {
            List<String> names = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                names.add(player.getName());
            }
            return names;
        }
        return List.of();
    }
}
