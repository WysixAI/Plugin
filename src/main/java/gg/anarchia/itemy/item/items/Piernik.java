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

/** Piernik - PPM daje HASTE 10 na 10 sekund. */
public class Piernik extends CustomItem implements Handlers.RightClick {

    public Piernik() {
        super("piernik", Material.COOKIE, "&6&lPiernik");
        category("Wsparcie");
        model(10028);
        lore("&7PPM: &cPośpiech X &7na &c%seconds% sekund&7!",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("haste-level", 10);
        setting("seconds", 10.0D);
        setting("consume", true);
        cooldown(15.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        Effects.apply(player, Effects.HASTE, (int) (settingDouble("seconds") * 20), Math.max(0, settingInt("haste-level") - 1));
        Particles.spawn(player.getLocation(), Particles.NOTE, 15);
        Sounds.play(player, Sounds.EAT, 1.0F, 1.2F);
        if (settingBool("consume")) {
            ItemUtil.consumeOne(player, event.getHand());
        }
    }
}
