package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Ciepłe mleko - PPM usuwa wszystkie negatywne efekty. */
public class CiepleMleko extends CustomItem implements Handlers.RightClick {

    public CiepleMleko() {
        super("cieplemleko", Material.MILK_BUCKET, "&f&lCiepłe Mleko");
        category("Wsparcie");
        model(10009);
        lore("&7PPM: usuwa wszystkie negatywne efekty",
                "&7oraz gasi płomienie.",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("consume", true);
        cooldown(5.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        Effects.clearNegative(player);
        Particles.spawn(player.getLocation().add(0, 1.0D, 0), Particles.CLOUD, 25);
        Sounds.play(player, Sounds.DRINK, 1.0F, 1.0F);
        if (settingBool("consume")) {
            ItemUtil.consumeOne(player, event.getHand());
        }
    }
}
