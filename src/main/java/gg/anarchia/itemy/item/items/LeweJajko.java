package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/** Lewe jajko - wyrzuca trafionego przeciwnika w powietrze. */
public class LeweJajko extends CustomItem implements Handlers.RightClick, Handlers.Hit {

    public LeweJajko() {
        super("lewejajko", Material.EGG, "&f&lLewe Jajko");
        category("Sabotaż");
        model(10017);
        lore("&7Rzuć w przeciwnika, aby wyrzucić",
                "&7go wysoko w &cpowietrze&7!",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("power", 2.0D);
        setting("speed", 1.6D);
        setting("consume", true);
        cooldown(3.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        Egg egg = player.launchProjectile(Egg.class);
        egg.setVelocity(player.getLocation().getDirection().multiply(settingDouble("speed")));
        ItemUtil.tag(egg, getId());
        Sounds.play(player.getLocation(), "entity.egg.throw", 1.0F, 1.0F);
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
        victim.setVelocity(new Vector(victim.getVelocity().getX(), settingDouble("power"), victim.getVelocity().getZ()));
        Particles.spawn(victim.getLocation(), Particles.CLOUD, 20);
        Sounds.play(victim.getLocation(), "entity.chicken.egg", 1.0F, 1.4F);
    }
}
