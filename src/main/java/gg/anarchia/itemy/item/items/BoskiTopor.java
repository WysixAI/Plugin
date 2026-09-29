package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.PlayerUtil;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Tasks;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Boski topór - odpycha graczy wokół i daje nieśmiertelność na 3 sekundy. */
public class BoskiTopor extends CustomItem implements Handlers.RightClick {

    public BoskiTopor() {
        super("boskitopor", Material.NETHERITE_AXE, "&b&lBoski Topór");
        category("Bronie");
        model(10008);
        lore("&7PPM: odpychasz wszystkich wokół siebie",
                "&7oraz zyskujesz &cnieśmiertelność &7na &c%seconds%s&7.",
                "",
                "&8» &cAnarchia.GG");
        enchant("sharpness", 5);
        unbreakable();
        setting("radius", 7.0D);
        setting("power", 1.6D);
        setting("immortality-seconds", 3.0D);
        cooldown(35.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        double radius = settingDouble("radius");
        double power = settingDouble("power");
        for (LivingEntity entity : PlayerUtil.nearbyLiving(player.getLocation(), radius, player)) {
            PlayerUtil.knockback(entity, player.getLocation(), power, 0.6D);
        }
        int ticks = (int) (settingDouble("immortality-seconds") * 20);
        player.setInvulnerable(true);
        Particles.circle(player.getLocation(), Particles.END_ROD, 2.5D, 40);
        Particles.spawn(player.getLocation(), Particles.EXPLOSION, 3, 0.5D, 0.5D, 0.5D, 0.0D);
        Sounds.play(player.getLocation(), Sounds.TOTEM, 1.0F, 1.2F);
        Tasks.later(() -> {
            if (player.isOnline() && player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                player.setInvulnerable(false);
                Particles.spawn(player.getLocation(), Particles.SMOKE, 15);
            }
        }, ticks);
    }
}
