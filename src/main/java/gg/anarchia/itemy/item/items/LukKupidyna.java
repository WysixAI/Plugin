package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

/** Łuk Kupidyna - szansa na oślepienie trafionego gracza. */
public class LukKupidyna extends CustomItem implements Handlers.ProjectileDamage {

    public LukKupidyna() {
        super("lukkupidyna", Material.BOW, "&d&lŁuk Kupidyna");
        category("Bronie");
        model(10020);
        lore("&7Masz &c%chance%% &7szansy, że trafiony",
                "&7gracz zostanie &coślepiony&7.",
                "",
                "&8» &cAnarchia.GG");
        enchant("power", 3);
        enchant("punch", 1);
        unbreakable();
        setting("chance", 35.0D);
        setting("blindness-seconds", 5.0D);
    }

    @Override
    public void onProjectileDamage(Player shooter, Projectile projectile, LivingEntity victim, EntityDamageByEntityEvent event) {
        Particles.spawn(victim.getLocation().add(0, 1.5D, 0), Particles.HEART, 10);
        if (ThreadLocalRandom.current().nextDouble(100.0D) > settingDouble("chance")) {
            return;
        }
        Effects.apply(victim, Effects.BLINDNESS, (int) (settingDouble("blindness-seconds") * 20), 0);
        Sounds.play(victim.getLocation(), Sounds.PLING, 1.0F, 1.8F);
        if (shooter != null) {
            Text.actionBar(shooter, "&d&lTRAFIONY! &7Przeciwnik jest oślepiony");
        }
    }
}
