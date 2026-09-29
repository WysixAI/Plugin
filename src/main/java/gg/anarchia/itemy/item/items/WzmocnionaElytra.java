package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.DamageGuard;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.PlayerUtil;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Wzmocniona elytra - po naładowaniu uwalnia falę uderzeniową przy zderzeniu z ziemią. */
public class WzmocnionaElytra extends CustomItem implements Handlers.Wear, Handlers.Damaged, Handlers.Cleanup {

    private final Map<UUID, Integer> charge = new HashMap<>();

    public WzmocnionaElytra() {
        super("wzmocnionaelytra", Material.ELYTRA, "&5&lWzmocniona Elytra");
        category("Zbroje");
        model(10049);
        lore("&7Leć, aby naładować elytrę do &c100%&7.",
                "&7Przy uderzeniu w ziemię uwolnisz",
                "&7potężną &cfalę uderzeniową&7!",
                "",
                "&8» &cAnarchia.GG");
        enchant("unbreaking", 10);
        unbreakable();
        setting("charge-per-tick", 4);
        setting("radius", 7.0D);
        setting("damage", 10.0D);
        setting("knockback", 1.8D);
        setting("cancel-fall-damage", true);
    }

    @Override
    public void onWear(Player player, ItemStack item) {
        if (!matches(player.getInventory().getChestplate())) {
            return;
        }
        UUID uuid = player.getUniqueId();
        int current = charge.getOrDefault(uuid, 0);
        if (player.isGliding()) {
            current = Math.min(100, current + Math.max(1, settingInt("charge-per-tick")));
            charge.put(uuid, current);
            if (current >= 100) {
                Text.actionBar(player, "&5&lELYTRA &8» &a&lNAŁADOWANA 100%");
                Particles.spawn(player.getLocation(), Particles.SOUL_FIRE_FLAME, 6, 0.3D, 0.3D, 0.3D, 0.01D);
            } else {
                Text.actionBar(player, "&5&lELYTRA &8» &7Ładowanie: &d" + current + "%");
            }
            return;
        }
        if (current >= 100 && player.isOnGround()) {
            shockwave(player);
        }
    }

    @Override
    public void onDamaged(Player player, ItemStack item, EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        if (charge.getOrDefault(player.getUniqueId(), 0) < 100) {
            return;
        }
        if (settingBool("cancel-fall-damage")) {
            event.setCancelled(true);
        }
        shockwave(player);
    }

    private void shockwave(Player player) {
        charge.put(player.getUniqueId(), 0);
        Location center = player.getLocation();
        double radius = settingDouble("radius");
        for (LivingEntity target : PlayerUtil.nearbyLiving(center, radius, player)) {
            PlayerUtil.knockback(target, center, settingDouble("knockback"), 0.9D);
            DamageGuard.damage(target, settingDouble("damage"), player);
        }
        Particles.circle(center, Particles.EXPLOSION, 3.0D, 30);
        Particles.spawn(center, Particles.EXPLOSION_EMITTER, 2, 1.0D, 0.5D, 1.0D, 0.0D);
        Sounds.play(center, Sounds.EXPLODE, 1.6F, 0.7F);
        Text.actionBar(player, "&5&lELYTRA &8» &c&lFALA UDERZENIOWA!");
    }

    @Override
    public void cleanup(Player player) {
        charge.remove(player.getUniqueId());
    }
}
