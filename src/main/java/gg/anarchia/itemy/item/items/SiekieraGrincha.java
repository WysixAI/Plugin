package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.DamageGuard;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/** Siekiera Grincha - piorun w przeciwnika zabierający 30% jego życia. */
public class SiekieraGrincha extends CustomItem implements Handlers.Attack {

    public SiekieraGrincha() {
        super("siekieragrincha", Material.DIAMOND_AXE, "&2&lSiekiera Grincha");
        category("Bronie");
        model(10034);
        lore("&7Po uderzeniu w przeciwnika uderza piorun,",
                "&7zabierając mu &c30% &7aktualnego życia.",
                "",
                "&8» &cAnarchia.GG");
        enchant("sharpness", 4);
        unbreakable();
        setting("health-percent", 30.0D);
        setting("real-lightning", false);
        cooldown(12.0D);
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, ItemStack item, EntityDamageByEntityEvent event) {
        if (!checkCooldown(attacker)) {
            return;
        }
        World world = victim.getWorld();
        if (settingBool("real-lightning")) {
            world.strikeLightning(victim.getLocation());
        } else {
            world.strikeLightningEffect(victim.getLocation());
        }
        double percent = Math.max(0.0D, settingDouble("health-percent")) / 100.0D;
        double damage = victim.getHealth() * percent;
        Particles.spawn(victim.getLocation(), Particles.ELECTRIC_SPARK, 30);
        Sounds.play(victim.getLocation(), Sounds.IMPACT, 1.0F, 1.0F);
        DamageGuard.damage(victim, damage, attacker);
    }
}
