package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.PlayerUtil;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/** Marchewkowa kusza - przyciąga trafionego przeciwnika do siebie. */
public class MarchewkowaKusza extends CustomItem implements Handlers.ProjectileDamage {

    public MarchewkowaKusza() {
        super("marchewkowakusza", Material.CROSSBOW, "&6&lMarchewkowa Kusza");
        category("Bronie");
        model(10023);
        lore("&7Po trafieniu przyciąga",
                "&7przeciwnika prosto do ciebie!",
                "",
                "&8» &cAnarchia.GG");
        enchant("quick_charge", 3);
        enchant("piercing", 2);
        unbreakable();
        setting("power", 1.4D);
    }

    @Override
    public void onProjectileDamage(Player shooter, Projectile projectile, LivingEntity victim, EntityDamageByEntityEvent event) {
        if (shooter == null) {
            return;
        }
        Tasks.later(() -> {
            if (victim.isValid() && !victim.isDead()) {
                PlayerUtil.pull(victim, shooter.getLocation(), settingDouble("power"));
                Particles.line(victim.getLocation().add(0, 1.0D, 0), shooter.getLocation().add(0, 1.0D, 0), Particles.CRIT, 0.4D);
            }
        }, 1L);
        Sounds.play(shooter.getLocation(), Sounds.CHAIN, 1.0F, 1.2F);
    }
}
