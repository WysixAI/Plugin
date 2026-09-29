package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Cooldowns;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Anarchiczny Trójząb - RIPTIDE 4, CHANNELING, IMPALING 5, LOYALTY 4, UNBREAKING 4 + 10s cooldown. */
public class AnarchicznyTrojzab extends CustomItem implements Handlers.RightClick {

    public AnarchicznyTrojzab() {
        super("anarchicznytrojzab", Material.TRIDENT, "&c&lAnarchiczny Trójząb");
        category("Bronie");
        model(10004);
        lore("&7Trójząb z zaklęciami:",
                "&8• &cRiptide IV&7, &cChanneling I",
                "&8• &cImpaling V&7, &cLoyalty IV",
                "&8• &cUnbreaking IV",
                "&7Przeładowanie: &c%cooldown%s",
                "",
                "&8» &cAnarchia.GG");
        enchant("riptide", 4);
        enchant("channeling", 1);
        enchant("impaling", 5);
        enchant("loyalty", 4);
        enchant("unbreaking", 4);
        unbreakable();
        cooldown(10.0D);
        flags("HIDE_ENCHANTS", "HIDE_ATTRIBUTES", "HIDE_UNBREAKABLE");
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        double seconds = settingDouble("cooldown");
        if (seconds <= 0) {
            return;
        }
        if (!Cooldowns.isReady(player, getId())) {
            event.setCancelled(true);
            plugin.messages().actionBar(player, "cooldown", "%time%", Cooldowns.format(Cooldowns.remaining(player, getId())));
            return;
        }
        Cooldowns.set(player, getId(), seconds);
        player.setCooldown(item.getType(), (int) (seconds * 20));
    }
}
