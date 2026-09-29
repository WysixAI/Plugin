package gg.anarchia.itemy.command;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.events.GameEvent;
import gg.anarchia.itemy.gui.EventsGui;
import gg.anarchia.itemy.region.Region;
import gg.anarchia.itemy.util.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** /anarchiaitemy event <start|stop|list|gui> [nazwa] */
public class EventCommand implements SubCommand {

    private final AnarchiaItemy plugin;

    public EventCommand(AnarchiaItemy plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "event";
    }

    @Override
    public String getDescription() {
        return "Uruchamia wybrany event na serwerze";
    }

    @Override
    public String getUsage() {
        return "event <start|stop|list|gui> [nazwa]";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            plugin.messages().send(sender, "event-usage");
            return;
        }
        String action = args[0].toLowerCase(Locale.ROOT);
        switch (action) {
            case "start" -> {
                if (args.length < 2) {
                    plugin.messages().send(sender, "event-usage");
                    return;
                }
                String[] extra = args.length > 2 ? Arrays.copyOfRange(args, 2, args.length) : new String[0];
                plugin.events().start(sender, args[1], extra);
            }
            case "stop" -> {
                if (!plugin.events().isRunning()) {
                    plugin.messages().send(sender, "event-not-running");
                    return;
                }
                String name = plugin.events().active().getName();
                plugin.events().stop();
                plugin.messages().send(sender, "event-stopped", "%event%", name);
            }
            case "list", "lista" -> {
                Text.send(sender, "&8&m--------------------------------");
                Text.send(sender, "&c&lDostępne eventy:");
                for (GameEvent event : plugin.events().all()) {
                    Text.send(sender, "&8» &c" + event.getId() + " &8- &7" + event.getDescription()
                            + (event.isRunning() ? " &8(&aTRWA&8)" : ""));
                }
                Text.send(sender, "&8&m--------------------------------");
            }
            case "gui", "menu" -> {
                if (!(sender instanceof Player player)) {
                    plugin.messages().send(sender, "player-only");
                    return;
                }
                new EventsGui(plugin).open(player);
            }
            default -> plugin.messages().send(sender, "event-usage");
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("start", "stop", "list", "gui");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            return plugin.events().ids();
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("start") && args[1].equalsIgnoreCase("koth")) {
            List<String> names = new ArrayList<>();
            for (Region region : plugin.regions().all()) {
                names.add(region.getName());
            }
            return names;
        }
        return List.of();
    }
}
