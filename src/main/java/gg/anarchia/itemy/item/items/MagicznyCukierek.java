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

/** Magiczny cukierek - PPM daje SPEED V na 5 sekund. */
public class MagicznyCukierek extends CustomItem implements Handlers.RightClick {

    public MagicznyCukierek() {
        super("magicznycukierek", Material.HONEYCOMB, "&d&lMagiczny Cukierek");
        category("Wsparcie");
        model(10022);
        lore("&7PPM: &cSzybkość V &7na &c%seconds% sekund&7!",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("speed-level", 5);
        setting("seconds", 5.0D);
        setting("consume", true);
        cooldown(10.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        Effects.apply(player, Effects.SPEED, (int) (settingDouble("seconds") * 20), Math.max(0, settingInt("speed-level") - 1));
        Particles.spawn(player.getLocation(), Particles.HAPPY_VILLAGER, 20);
        Sounds.play(player, Sounds.EAT, 1.0F, 1.6F);
        if (settingBool("consume")) {
            ItemUtil.consumeOne(player, event.getHand());
        }
    }
}
