package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/** Wędka surferka - przyciąga cię do bloków i graczy. */
public class WedkaSurferka extends CustomItem implements Handlers.Fish {

    public WedkaSurferka() {
        super("wedkasurferka", Material.FISHING_ROD, "&b&lWędka Surferka");
        category("Wędki");
        model(10047);
        lore("&7Przyciąga cię do bloków i graczy -",
                "&7zupełnie nowe możliwości eksploracji i walki!",
                "",
                "&8» &cAnarchia.GG");
        unbreakable();
        glow();
        setting("power", 1.25D);
        setting("vertical-bonus", 0.35D);
        setting("cancel-fall-damage", true);
        cooldown(0.8D);
    }

    @Override
    public void onFish(Player player, ItemStack item, PlayerFishEvent event) {
        PlayerFishEvent.State state = event.getState();
        if (state != PlayerFishEvent.State.IN_GROUND
                && state != PlayerFishEvent.State.CAUGHT_ENTITY
                && state != PlayerFishEvent.State.REEL_IN
                && state != PlayerFishEvent.State.FAILED_ATTEMPT) {
            return;
        }
        if (!checkCooldown(player)) {
            return;
        }
        Location hook = event.getHook().getLocation();
        Vector direction = hook.toVector().subtract(player.getLocation().toVector());
        if (direction.lengthSquared() < 0.5D) {
            return;
        }
        double power = settingDouble("power");
        Vector velocity = direction.multiply(0.22D * power);
        velocity.setY(Math.min(1.4D, velocity.getY() + settingDouble("vertical-bonus")));
        player.setVelocity(velocity);
        player.setFallDistance(0.0F);
        Particles.line(player.getLocation().add(0, 1.0D, 0), hook, Particles.CLOUD, 0.5D);
        Sounds.play(player, Sounds.FISHING_RETRIEVE, 1.0F, 1.6F);
        if (settingBool("cancel-fall-damage")) {
            new BukkitRunnable() {
                private int ticks = 0;

                @Override
                public void run() {
                    ticks++;
                    if (!player.isOnline() || ticks > 100) {
                        cancel();
                        return;
                    }
                    player.setFallDistance(0.0F);
                    if (player.isOnGround() && ticks > 5) {
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 1L, 1L);
        }
    }
}
