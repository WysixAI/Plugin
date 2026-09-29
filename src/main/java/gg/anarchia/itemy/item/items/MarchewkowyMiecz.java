package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/** Marchewkowy miecz - zamraża przeciwnika na sekundę po uderzeniu. */
public class MarchewkowyMiecz extends CustomItem implements Handlers.Attack {

    public MarchewkowyMiecz() {
        super("marchewkowymiecz", Material.CARROT_ON_A_STICK, "&6&lMarchewkowy Miecz");
        category("Bronie");
        model(10024);
        lore("&7Po uderzeniu zamraża przeciwnika",
                "&7na &c%seconds% sekundę&7.",
                "",
                "&8» &cAnarchia.GG");
        enchant("sharpness", 3);
        unbreakable();
        setting("freeze-seconds", 1.0D);
        cooldown(4.0D);
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, ItemStack item, EntityDamageByEntityEvent event) {
        if (!checkCooldown(attacker)) {
            return;
        }
        int ticks = (int) (settingDouble("freeze-seconds") * 20);
        Effects.apply(victim, Effects.SLOWNESS, ticks, 254);
        Effects.blockJump(victim, ticks);
        victim.setFreezeTicks(Math.max(victim.getFreezeTicks(), ticks + 40));
        Particles.spawn(victim.getLocation().add(0, 1.0D, 0), Particles.SNOWFLAKE, 25);
        Sounds.play(victim.getLocation(), Sounds.SNOW_BREAK, 1.0F, 1.4F);
    }
}
