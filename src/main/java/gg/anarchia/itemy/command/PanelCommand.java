package gg.anarchia.itemy.command;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.gui.PanelGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** /anarchiaitemy panel */
public class PanelCommand implements SubCommand {

    private final AnarchiaItemy plugin;

    public PanelCommand(AnarchiaItemy plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "panel";
    }

    @Override
    public String getDescription() {
        return "Otwiera glowne menu pluginu";
    }

    @Override
    public String getUsage() {
        return "panel";
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        new PanelGui(plugin).open((Player) sender);
    }
}
