package gg.anarchia.itemy.command;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.gui.EnchantGui;
import gg.anarchia.itemy.util.Enchants;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** /anarchiaitemy enchant [menu|remove|<zaklecie> <poziom>] */
public class EnchantCommand implements SubCommand {

    private final AnarchiaItemy plugin;

    public EnchantCommand(AnarchiaItemy plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "enchant";
    }

    @Override
    public String getDescription() {
        return "Zaczarowuje przedmiot w rece lub usuwa zaklecia";
    }

    @Override
    public String getUsage() {
        return "enchant [menu|remove|<zaklecie> <poziom>]";
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0 || args[0].equalsIgnoreCase("menu") || args[0].equalsIgnoreCase("gui")) {
            new EnchantGui(plugin).open(player);
            return;
        }
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (ItemUtil.isEmpty(hand)) {
            plugin.messages().send(player, "no-item-in-hand");
            return;
        }
        ItemMeta meta = hand.getItemMeta();
        if (meta == null) {
            plugin.messages().send(player, "no-item-in-hand");
            return;
        }
        String action = args[0].toLowerCase(Locale.ROOT);
        if (action.equals("remove") || action.equals("usun") || action.equals("clear")) {
            for (Enchantment enchantment : new ArrayList<>(meta.getEnchants().keySet())) {
                meta.removeEnchant(enchantment);
            }
            hand.setItemMeta(meta);
            plugin.messages().send(player, "enchant-removed");
            Sounds.play(player, Sounds.GLASS, 1.0F, 0.8F);
            return;
        }
        Enchantment enchantment = Enchants.byName(action);
        if (enchantment == null) {
            plugin.messages().send(player, "enchant-unknown", "%enchant%", action);
            return;
        }
        int level = 1;
        if (args.length > 1) {
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
                level = 1;
            }
        }
        int max = plugin.getConfig().getInt("enchanting.enchantments." + Enchants.keyOf(enchantment), 10);
        if (level > max) {
            plugin.messages().send(player, "enchant-max", "%level%", String.valueOf(max));
            return;
        }
        if (level <= 0) {
            meta.removeEnchant(enchantment);
        } else {
            meta.addEnchant(enchantment, level, true);
        }
        hand.setItemMeta(meta);
        plugin.messages().send(player, "enchant-applied",
                "%enchant%", Enchants.prettyName(enchantment), "%level%", Enchants.roman(level));
        Sounds.play(player, Sounds.LEVEL_UP, 0.8F, 1.6F);
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("menu");
            options.add("remove");
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("enchanting.enchantments");
            if (section != null) {
                options.addAll(section.getKeys(false));
            }
            return options;
        }
        if (args.length == 2) {
            return List.of("1", "2", "3", "5", "10");
        }
        return List.of();
    }
}
