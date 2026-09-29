package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.PlayerUtil;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Parawan - po użyciu odrzuca wszystkich przeciwników w okolicy. */
public class Parawan extends CustomItem implements Handlers.RightClick {

    public Parawan() {
        super("parawan", Material.BLUE_BANNER, "&9&lParawan");
        category("Wsparcie");
        model(10026);
        lore("&7PPM: odrzuca wszystkich przeciwników",
                "&7w promieniu &c%radius% &7kratek.",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("radius", 8.0D);
        setting("power", 2.2D);
        setting("vertical", 0.5D);
        setting("consume", false);
        cooldown(20.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        for (LivingEntity entity : PlayerUtil.nearbyLiving(player.getLocation(), settingDouble("radius"), player)) {
            PlayerUtil.knockback(entity, player.getLocation(), settingDouble("power"), settingDouble("vertical"));
        }
        Particles.circle(player.getLocation(), Particles.CLOUD, 3.0D, 50);
        Sounds.play(player.getLocation(), "entity.player.attack.sweep", 1.0F, 0.8F);
        if (settingBool("consume")) {
            ItemUtil.consumeOne(player, event.getHand());
        }
    }
}
