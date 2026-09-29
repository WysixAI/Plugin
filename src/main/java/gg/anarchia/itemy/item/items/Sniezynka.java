package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Śnieżynka - rzuć w gracza, aby go zamrozić na krótką chwilę. */
public class Sniezynka extends CustomItem implements Handlers.RightClick, Handlers.Hit {

    public Sniezynka() {
        super("sniezynka", Material.SNOWBALL, "&b&lŚnieżynka");
        category("Sabotaż");
        model(10037);
        lore("&7Rzuć w gracza, aby &czamrozić",
                "&7go na &c%seconds% sekundy&7.",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("freeze-seconds", 3.0D);
        setting("speed", 1.5D);
        setting("consume", true);
        cooldown(8.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        Snowball snowball = player.launchProjectile(Snowball.class);
        snowball.setVelocity(player.getLocation().getDirection().multiply(settingDouble("speed")));
        ItemUtil.tag(snowball, getId());
        Sounds.play(player.getLocation(), "entity.snowball.throw", 1.0F, 1.4F);
        if (settingBool("consume")) {
            ItemUtil.consumeOne(player, event.getHand());
        }
    }

    @Override
    public void onProjectileHit(Player shooter, Projectile projectile, ProjectileHitEvent event) {
        Entity hit = event.getHitEntity();
        projectile.remove();
        if (!(hit instanceof LivingEntity victim)) {
            return;
        }
        int ticks = (int) (settingDouble("freeze-seconds") * 20);
        Effects.apply(victim, Effects.SLOWNESS, ticks, 254);
        Effects.blockJump(victim, ticks);
        victim.setFreezeTicks(Math.max(victim.getFreezeTicks(), ticks + 60));
        Particles.spawn(victim.getLocation().add(0, 1.0D, 0), Particles.SNOWFLAKE, 40);
        Sounds.play(victim.getLocation(), Sounds.SNOW_BREAK, 1.0F, 0.8F);
    }
}
