package gg.anarchia.itemy.command;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.Keys;
import gg.anarchia.itemy.gui.RegionsGui;
import gg.anarchia.itemy.region.Region;
import gg.anarchia.itemy.region.RegionManager;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** /anarchiaitemy region ... */
public class RegionCommand implements SubCommand {

    private final AnarchiaItemy plugin;

    public RegionCommand(AnarchiaItemy plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "region";
    }

    @Override
    public String getDescription() {
        return "Zarzadzanie systemem regionow";
    }

    @Override
    public String getUsage() {
        return "region <create|delete|list|info|pos1|pos2|wand|flag|tp|gui>";
    }

    @Override
    public String getPermission() {
        return "Anarchiaitemy.region";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            plugin.messages().send(sender, "region-usage");
            return;
        }
        String action = args[0].toLowerCase(Locale.ROOT);
        Player player = sender instanceof Player p ? p : null;

        switch (action) {
            case "pos1", "pos2" -> {
                if (player == null) {
                    plugin.messages().send(sender, "player-only");
                    return;
                }
                int index = action.equals("pos1") ? 0 : 1;
                Location location = player.getLocation();
                plugin.regions().setSelection(player, index, location);
                plugin.messages().send(sender, "region-pos",
                        "%pos%", String.valueOf(index + 1),
                        "%x%", String.valueOf(location.getBlockX()),
                        "%y%", String.valueOf(location.getBlockY()),
                        "%z%", String.valueOf(location.getBlockZ()));
            }
            case "wand" -> {
                if (player == null) {
                    plugin.messages().send(sender, "player-only");
                    return;
                }
                ItemUtil.give(player, createWand());
                plugin.messages().send(sender, "region-wand-given");
            }
            case "create" -> {
                if (player == null) {
                    plugin.messages().send(sender, "player-only");
                    return;
                }
                if (args.length < 2) {
                    plugin.messages().send(sender, "region-usage");
                    return;
                }
                String name = args[1];
                if (plugin.regions().get(name) != null) {
                    plugin.messages().send(sender, "region-exists", "%region%", name);
                    return;
                }
                if (!plugin.regions().hasSelection(player)) {
                    plugin.messages().send(sender, "region-need-selection");
                    return;
                }
                plugin.regions().create(name, plugin.regions().getSelection(player, 0), plugin.regions().getSelection(player, 1));
                plugin.regions().clearSelection(player);
                plugin.messages().send(sender, "region-created", "%region%", name);
            }
            case "delete", "remove", "usun" -> {
                if (args.length < 2) {
                    plugin.messages().send(sender, "region-usage");
                    return;
                }
                if (!plugin.regions().delete(args[1])) {
                    plugin.messages().send(sender, "region-not-found", "%region%", args[1]);
                    return;
                }
                plugin.messages().send(sender, "region-deleted", "%region%", args[1]);
            }
            case "list", "lista" -> {
                Text.send(sender, "&8&m--------------------------------");
                Text.send(sender, "&c&lRegiony &7(" + plugin.regions().all().size() + ")");
                for (Region region : plugin.regions().all()) {
                    Text.send(sender, "&8» &c" + region.getName() + " &7(" + region.getWorld() + ") &8- &7"
                            + region.getMinX() + "," + region.getMinY() + "," + region.getMinZ() + " &8=> &7"
                            + region.getMaxX() + "," + region.getMaxY() + "," + region.getMaxZ());
                }
                Text.send(sender, "&8&m--------------------------------");
            }
            case "info" -> {
                Region region = args.length > 1 ? plugin.regions().get(args[1])
                        : (player == null ? null : plugin.regions().at(player.getLocation()));
                if (region == null) {
                    plugin.messages().send(sender, "region-not-found", "%region%", args.length > 1 ? args[1] : "?");
                    return;
                }
                Text.send(sender, "&8&m--------------------------------");
                Text.send(sender, "&c&lRegion: &f" + region.getName());
                Text.send(sender, "&7Świat: &f" + region.getWorld());
                Text.send(sender, "&7Priorytet: &f" + region.getPriority());
                Text.send(sender, "&7Rozmiar: &f" + region.volume() + " bloków");
                for (Map.Entry<String, Boolean> entry : region.getFlags().entrySet()) {
                    Text.send(sender, "&8» &7" + entry.getKey() + ": " + (entry.getValue() ? "&atak" : "&cnie"));
                }
                Text.send(sender, "&8&m--------------------------------");
            }
            case "flag", "flaga" -> {
                if (args.length < 4) {
                    plugin.messages().send(sender, "region-usage");
                    return;
                }
                Region region = plugin.regions().get(args[1]);
                if (region == null) {
                    plugin.messages().send(sender, "region-not-found", "%region%", args[1]);
                    return;
                }
                String flag = args[2].toLowerCase(Locale.ROOT);
                if (!RegionManager.FLAGS.contains(flag)) {
                    plugin.messages().send(sender, "region-flag-unknown", "%flags%", String.join(", ", RegionManager.FLAGS));
                    return;
                }
                boolean value = args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("tak");
                region.setFlag(flag, value);
                plugin.regions().save();
                plugin.messages().send(sender, "region-flag-set",
                        "%flag%", flag, "%region%", region.getName(), "%value%", String.valueOf(value));
            }
            case "priority" -> {
                if (args.length < 3) {
                    plugin.messages().send(sender, "region-usage");
                    return;
                }
                Region region = plugin.regions().get(args[1]);
                if (region == null) {
                    plugin.messages().send(sender, "region-not-found", "%region%", args[1]);
                    return;
                }
                try {
                    region.setPriority(Integer.parseInt(args[2]));
                    plugin.regions().save();
                    Text.send(sender, plugin.messages().prefix() + "&aUstawiono priorytet &f" + args[2]);
                } catch (NumberFormatException exception) {
                    plugin.messages().send(sender, "region-usage");
                }
            }
            case "tp" -> {
                if (player == null) {
                    plugin.messages().send(sender, "player-only");
                    return;
                }
                if (args.length < 2) {
                    plugin.messages().send(sender, "region-usage");
                    return;
                }
                Region region = plugin.regions().get(args[1]);
                if (region == null || region.center() == null) {
                    plugin.messages().send(sender, "region-not-found", "%region%", args[1]);
                    return;
                }
                player.teleport(region.center());
            }
            case "gui", "menu" -> {
                if (player == null) {
                    plugin.messages().send(sender, "player-only");
                    return;
                }
                new RegionsGui(plugin, 0).open(player);
            }
            default -> plugin.messages().send(sender, "region-usage");
        }
    }

    private ItemStack createWand() {
        Material material = Material.matchMaterial(plugin.getConfig().getString("regions.wand-material", "GOLDEN_AXE"));
        ItemStack wand = new ItemStack(material == null ? Material.GOLDEN_AXE : material);
        ItemMeta meta = wand.getItemMeta();
        if (meta != null) {
            meta.displayName(Text.parse("&c&lRóżdżka Regionów"));
            meta.lore(Text.parse(List.of("&7LPM &8- &7pozycja 1", "&7PPM &8- &7pozycja 2", "", "&8» &cAnarchiaItemy")));
            meta.getPersistentDataContainer().set(Keys.REGION_WAND, PersistentDataType.BYTE, (byte) 1);
            wand.setItemMeta(meta);
        }
        return wand;
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("create", "delete", "list", "info", "pos1", "pos2", "wand", "flag", "priority", "tp", "gui");
        }
        List<String> names = new ArrayList<>();
        for (Region region : plugin.regions().all()) {
            names.add(region.getName());
        }
        if (args.length == 2) {
            return names;
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("flag") || args[0].equalsIgnoreCase("flaga"))) {
            return RegionManager.FLAGS;
        }
        if (args.length == 4 && (args[0].equalsIgnoreCase("flag") || args[0].equalsIgnoreCase("flaga"))) {
            return List.of("true", "false");
        }
        return List.of();
    }
}
