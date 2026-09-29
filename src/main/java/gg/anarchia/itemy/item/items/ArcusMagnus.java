package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Tasks;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Arcus Magnus - łuk pozwalający na niszczycielskie combo. */
public class ArcusMagnus extends CustomItem implements Handlers.Shoot, Handlers.ProjectileDamage {

    private final Map<UUID, Integer> combo = new HashMap<>();
    private final Map<UUID, Long> lastHit = new HashMap<>();

    public ArcusMagnus() {
        super("arcusmagnus", Material.BOW, "&6&lArcus Magnus");
        category("Bronie");
        model(10006);
        lore("&7Łuk pozwalający na niszczycielskie combo.",
                "&7Każde kolejne trafienie w ciągu &c%window%s",
                "&7zadaje coraz większe obrażenia i podrzuca wroga.",
                "",
                "&8» &cAnarchia.GG");
        enchant("power", 4);
        enchant("infinity", 1);
        enchant("unbreaking", 5);
        unbreakable();
        setting("arrow-speed", 1.6D);
        setting("combo-seconds", 3.0D);
        setting("damage-per-combo", 1.2D);
        setting("max-combo", 5);
        setting("juggle-power", 0.45D);
    }

    @Override
    public void onShoot(Player player, ItemStack bow, EntityShootBowEvent event) {
        double speed = settingDouble("arrow-speed");
        if (speed > 0 && event.getProjectile() instanceof Projectile projectile) {
            projectile.setVelocity(projectile.getVelocity().multiply(speed));
        }
        Particles.spawn(player.getEyeLocation(), Particles.CRIT, 10);
    }

    @Override
    public void onProjectileDamage(Player shooter, Projectile projectile, LivingEntity victim, EntityDamageByEntityEvent event) {
        long now = System.currentTimeMillis();
        long window = (long) (settingDouble("combo-seconds") * 1000L);
        UUID key = victim.getUniqueId();
        int current = (now - lastHit.getOrDefault(key, 0L) <= window) ? combo.getOrDefault(key, 0) + 1 : 1;
        current = Math.min(current, Math.max(1, settingInt("max-combo")));
        combo.put(key, current);
        lastHit.put(key, now);

        event.setDamage(event.getDamage() + current * settingDouble("damage-per-combo"));
        double juggle = settingDouble("juggle-power");
        Tasks.later(() -> {
            if (!victim.isDead() && victim.isValid()) {
                victim.setVelocity(new Vector(victim.getVelocity().getX() * 0.2D, juggle, victim.getVelocity().getZ() * 0.2D));
                victim.setNoDamageTicks(0);
            }
        }, 1L);
        Particles.spawn(victim.getLocation().add(0, 1.0D, 0), Particles.ENCHANTED_HIT, 12);
        Sounds.play(victim.getLocation(), Sounds.ARROW_HIT, 1.0F, 0.8F + (current * 0.12F));
        if (shooter != null) {
            Text.actionBar(shooter, "&c&lCOMBO &7x&c" + current);
        }
    }
}
