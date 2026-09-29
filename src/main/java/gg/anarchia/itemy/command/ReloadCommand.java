package gg.anarchia.itemy.command;

import gg.anarchia.itemy.AnarchiaItemy;
import org.bukkit.command.CommandSender;

/** /anarchiaitemy reload */
public class ReloadCommand implements SubCommand {

    private final AnarchiaItemy plugin;

    public ReloadCommand(AnarchiaItemy plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Przeladowuje konfiguracje pluginu";
    }

    @Override
    public String getUsage() {
        return "reload";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.reloadEverything();
        plugin.messages().send(sender, "reloaded",
                "%items%", String.valueOf(plugin.items().all().size()),
                "%regions%", String.valueOf(plugin.regions().all().size()));
    }
}
