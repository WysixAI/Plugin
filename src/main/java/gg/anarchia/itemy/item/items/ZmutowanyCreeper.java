package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.DamageGuard;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.PlayerUtil;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Jajko zmutowanego creepera - przyzywa creepera zabierającego 50% życia. */
public class ZmutowanyCreeper extends CustomItem implements Handlers.RightClick {

    public ZmutowanyCreeper() {
        super("zmutowanycreeper", Material.CREEPER_SPAWN_EGG, "&a&lJajko Zmutowanego Creepera");
        category("Sabotaż");
        model(10054);
        lore("&7Przyzywa potężnego creepera, który po",
                "&7wybuchu zabiera przeciwnikom &c50% &7życia.",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("health-percent", 50.0D);
        setting("radius", 6.0D);
        setting("fuse-ticks", 50);
        setting("explosion-radius", 4);
        setting("destroy-blocks", false);
        setting("powered", true);
        setting("consume", true);
        cooldown(15.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        Block clicked = event.getClickedBlock();
        Location location = clicked != null
                ? clicked.getRelative(event.getBlockFace()).getLocation().add(0.5D, 0.0D, 0.5D)
                : player.getLocation().add(player.getLocation().getDirection().multiply(2));
        if (location.getWorld() == null) {
            return;
        }
        Creeper creeper = location.getWorld().spawn(location, Creeper.class, spawned -> {
            spawned.setPowered(settingBool("powered"));
            spawned.setMaxFuseTicks(Math.max(10, settingInt("fuse-ticks")));
            spawned.setExplosionRadius(Math.max(1, settingInt("explosion-radius")));
            spawned.customName(Text.parse("&a&lZMUTOWANY CREEPER"));
            spawned.setCustomNameVisible(true);
        });
        ItemUtil.tag(creeper, getId());
        creeper.getPersistentDataContainer().set(gg.anarchia.itemy.Keys.OWNER,
                org.bukkit.persistence.PersistentDataType.STRING, player.getUniqueId().toString());
        Particles.spawn(location, Particles.EXPLOSION, 3);
        Sounds.play(location, "entity.creeper.primed", 1.2F, 0.6F);
        if (settingBool("consume")) {
            ItemUtil.consumeOne(player, event.getHand());
        }
    }

    /** Wywolywane z listenera gdy creeper wybucha. */
    public void handleExplosion(Entity creeper) {
        double percent = Math.max(0.0D, settingDouble("health-percent")) / 100.0D;
        Location location = creeper.getLocation();
        for (LivingEntity target : PlayerUtil.nearbyLiving(location, settingDouble("radius"), creeper)) {
            double damage = target.getHealth() * percent;
            PlayerUtil.knockback(target, location, 1.0D, 0.4D);
            DamageGuard.damage(target, damage, creeper);
        }
        Particles.spawn(location, Particles.EXPLOSION_EMITTER, 5, 1.5D, 1.5D, 1.5D, 0.0D);
        Sounds.play(location, Sounds.EXPLODE, 2.0F, 0.6F);
    }
}
