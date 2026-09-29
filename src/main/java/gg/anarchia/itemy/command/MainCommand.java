package gg.anarchia.itemy.command;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.util.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** /anarchiaitemy - glowna komenda pluginu. */
public class MainCommand implements CommandExecutor, TabCompleter {

    private final AnarchiaItemy plugin;
    private final Map<String, SubCommand> subCommands = new LinkedHashMap<>();

    public MainCommand(AnarchiaItemy plugin) {
        this.plugin = plugin;
        register(new GiveItemCommand(plugin));
        register(new ReloadCommand(plugin));
        register(new PanelCommand(plugin));
        register(new SetKillsCommand(plugin));
        register(new MenuPreviewCommand(plugin));
        register(new RegionCommand(plugin));
        register(new EventCommand(plugin));
        register(new EnchantCommand(plugin));
    }

    private void register(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(Locale.ROOT), subCommand);
    }

    public Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            help(sender, label);
            return true;
        }
        String name = args[0].toLowerCase(Locale.ROOT);
        if (name.equals("help") || name.equals("pomoc")) {
            help(sender, label);
            return true;
        }
        SubCommand subCommand = subCommands.get(name);
        if (subCommand == null) {
            plugin.messages().send(sender, "unknown-command");
            return true;
        }
        if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
            plugin.messages().send(sender, "no-permission");
            return true;
        }
        if (subCommand.playerOnly() && !(sender instanceof Player)) {
            plugin.messages().send(sender, "player-only");
            return true;
        }
        try {
            subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        } catch (Exception exception) {
            Text.send(sender, plugin.messages().prefix() + "&cBlad podczas wykonywania komendy: &f" + exception.getMessage());
            plugin.getLogger().warning("Blad komendy /" + label + " " + String.join(" ", args) + ": " + exception);
        }
        return true;
    }

    private void help(CommandSender sender, String label) {
        Text.send(sender, "&8&m--------------------------------");
        Text.send(sender, "&c&lAnarchiaItemy &7- &feventowki z Anarchia.GG");
        for (SubCommand subCommand : subCommands.values()) {
            if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
                continue;
            }
            Text.send(sender, "&8» &c/" + label + " " + subCommand.getUsage() + " &8- &7" + subCommand.getDescription());
        }
        Text.send(sender, "&7Tekstury: &fteksturepack Anarchia.GG &8(&7discord.gg/yfnDmhhv5Q&8)");
        Text.send(sender, "&8&m--------------------------------");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            for (SubCommand subCommand : subCommands.values()) {
                if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
                    continue;
                }
                if (subCommand.getName().startsWith(prefix)) {
                    completions.add(subCommand.getName());
                }
            }
            if ("help".startsWith(prefix)) {
                completions.add("help");
            }
            return completions;
        }
        SubCommand subCommand = subCommands.get(args[0].toLowerCase(Locale.ROOT));
        if (subCommand == null || (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission()))) {
            return completions;
        }
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        String last = subArgs[subArgs.length - 1].toLowerCase(Locale.ROOT);
        for (String option : subCommand.complete(sender, subArgs)) {
            if (option != null && option.toLowerCase(Locale.ROOT).startsWith(last)) {
                completions.add(option);
            }
        }
        return completions;
    }
}
