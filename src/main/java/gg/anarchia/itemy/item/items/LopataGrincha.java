package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

/** Łopata Grincha - obraca głowę przeciwnika i zamraża go na 3 sekundy. */
public class LopataGrincha extends CustomItem implements Handlers.Attack {

    public LopataGrincha() {
        super("lopatagrincha", Material.IRON_SHOVEL, "&2&lŁopata Grincha");
        category("Bronie");
        model(10019);
        lore("&7Po uderzeniu obraca głowę przeciwnika",
                "&7w losową stronę i zamraża go na &c%seconds%s&7.",
                "",
                "&8» &cAnarchia.GG");
        enchant("knockback", 1);
        unbreakable();
        setting("freeze-seconds", 3.0D);
        cooldown(10.0D);
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, ItemStack item, EntityDamageByEntityEvent event) {
        if (!(victim instanceof Player target)) {
            return;
        }
        if (!checkCooldown(attacker)) {
            return;
        }
        int ticks = (int) (settingDouble("freeze-seconds") * 20);
        Location location = target.getLocation().clone();
        location.setYaw(ThreadLocalRandom.current().nextFloat() * 360.0F - 180.0F);
        location.setPitch(ThreadLocalRandom.current().nextFloat() * 180.0F - 90.0F);
        target.teleport(location);
        Effects.apply(target, Effects.SLOWNESS, ticks, 254);
        Effects.blockJump(target, ticks);
        target.setFreezeTicks(Math.max(target.getFreezeTicks(), ticks + 60));
        Particles.spawn(target.getLocation().add(0, 1.8D, 0), Particles.SNOWFLAKE, 30);
        Sounds.play(target.getLocation(), Sounds.ANVIL_LAND, 0.7F, 1.4F);
    }
}
