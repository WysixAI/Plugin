package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Smoczy miecz - PPM wystrzeliwuje perłę kresu. */
public class SmoczyMiecz extends CustomItem implements Handlers.RightClick {

    public SmoczyMiecz() {
        super("smoczymiecz", Material.DIAMOND_SWORD, "&5&lSmoczy Miecz");
        category("Bronie");
        model(10035);
        lore("&7PPM: wystrzeliwuje &5perłę kresu&7.",
                "",
                "&8» &cAnarchia.GG");
        enchant("sharpness", 5);
        enchant("fire_aspect", 1);
        unbreakable();
        setting("speed", 2.0D);
        cooldown(8.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        EnderPearl pearl = player.launchProjectile(EnderPearl.class);
        pearl.setVelocity(player.getLocation().getDirection().multiply(settingDouble("speed")));
        ItemUtil.tag(pearl, getId());
        Particles.spawn(player.getEyeLocation(), Particles.DRAGON_BREATH, 15);
        Sounds.play(player.getLocation(), Sounds.DRAGON_GROWL, 0.6F, 1.6F);
    }
}
