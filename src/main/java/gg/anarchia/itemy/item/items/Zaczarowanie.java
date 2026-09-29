package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.gui.EnchantGui;
import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Zaczarowanie przedmiotu - otwiera menu do zaczarowywania. */
public class Zaczarowanie extends CustomItem implements Handlers.RightClick {

    public Zaczarowanie() {
        super("zaczarowanie", Material.ENCHANTING_TABLE, "&b&lZaczarowanie Przedmiotu");
        category("Wsparcie");
        model(10050);
        lore("&7PPM: otwiera menu zaczarowywania",
                "&7przedmiotu trzymanego w ręce.",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("consume", false);
        cooldown(1.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        new EnchantGui(plugin).open(player);
        Sounds.play(player, "block.enchantment_table.use", 1.0F, 1.2F);
    }
}
