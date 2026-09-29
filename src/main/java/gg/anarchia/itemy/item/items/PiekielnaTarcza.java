package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.DamageGuard;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

/** Piekielna tarcza - 25% szansy na odbicie ataku wroga. */
public class PiekielnaTarcza extends CustomItem implements Handlers.Defend {

    public PiekielnaTarcza() {
        super("piekielnatarcza", Material.SHIELD, "&4&lPiekielna Tarcza");
        category("Zbroje");
        model(10027);
        lore("&7Posiada &c%chance%% &7szansy na",
                "&7&codbicie &7ataku wroga.",
                "",
                "&8» &cAnarchia.GG");
        enchant("unbreaking", 10);
        unbreakable();
        setting("chance", 25.0D);
        setting("reflect-percent", 100.0D);
        setting("fire-ticks", 40);
    }

    @Override
    public void onDefend(Player victim, Entity attacker, ItemStack item, EntityDamageByEntityEvent event) {
        if (!(attacker instanceof LivingEntity living)) {
            return;
        }
        if (ThreadLocalRandom.current().nextDouble(100.0D) > settingDouble("chance")) {
            return;
        }
        double damage = event.getDamage() * (settingDouble("reflect-percent") / 100.0D);
        event.setCancelled(true);
        DamageGuard.damage(living, damage, victim);
        int fireTicks = settingInt("fire-ticks");
        if (fireTicks > 0) {
            living.setFireTicks(fireTicks);
        }
        Particles.spawn(victim.getLocation().add(0, 1.0D, 0), Particles.FLAME, 25);
        Sounds.play(victim.getLocation(), Sounds.SHIELD_BLOCK, 1.0F, 0.6F);
        Text.actionBar(victim, "&4&lODBICIE! &7Atak został odbity");
    }
}
