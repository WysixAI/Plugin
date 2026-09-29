package gg.anarchia.itemy.command;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/** Pojedyncza podkomenda /anarchiaitemy. */
public interface SubCommand {

    String getName();

    String getDescription();

    String getUsage();

    default String getPermission() {
        return "iAnarchiaitemy.admin";
    }

    default boolean playerOnly() {
        return false;
    }

    void execute(CommandSender sender, String[] args);

    default List<String> complete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
