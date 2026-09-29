package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

/** Totem ułaskawienia - po śmierci nie tracisz przedmiotów. */
public class TotemUlaskawienia extends CustomItem implements Handlers.Death {

    public TotemUlaskawienia() {
        super("totemulaskawienia", Material.TOTEM_OF_UNDYING, "&d&lTotem Ułaskawienia");
        category("Wsparcie");
        model(10039);
        lore("&7Trzymając go w ekwipunku po śmierci",
                "&7&cnie stracisz swoich przedmiotów&7.",
                "&7Totem znika po użyciu.",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("keep-experience", true);
        setting("consume", true);
        setting("require-in-hand", false);
    }

    @Override
    public boolean onDeath(Player player, ItemStack item, PlayerDeathEvent event) {
        if (settingBool("require-in-hand") && !matches(player.getInventory().getItemInMainHand())
                && !matches(player.getInventory().getItemInOffHand())) {
            return false;
        }
        event.setKeepInventory(true);
        event.getDrops().clear();
        if (settingBool("keep-experience")) {
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }
        Particles.spawn(player.getLocation().add(0, 1.0D, 0), Particles.TOTEM, 60, 0.5D, 1.0D, 0.5D, 0.3D);
        Sounds.play(player.getLocation(), Sounds.TOTEM, 1.0F, 1.0F);
        plugin.messages().send(player, "totem-saved");
        return settingBool("consume");
    }
}
