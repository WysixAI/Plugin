package gg.anarchia.itemy.command;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.gui.ItemsGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** /anarchiaitemy menupreview */
public class MenuPreviewCommand implements SubCommand {

    private final AnarchiaItemy plugin;

    public MenuPreviewCommand(AnarchiaItemy plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "menupreview";
    }

    @Override
    public String getDescription() {
        return "Otwiera menu podgladu przedmiotow";
    }

    @Override
    public String getUsage() {
        return "menupreview [strona]";
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        int page = 0;
        if (args.length > 0) {
            try {
                page = Math.max(0, Integer.parseInt(args[0]) - 1);
            } catch (NumberFormatException ignored) {
                page = 0;
            }
        }
        new ItemsGui(plugin, page).open((Player) sender);
    }
}
