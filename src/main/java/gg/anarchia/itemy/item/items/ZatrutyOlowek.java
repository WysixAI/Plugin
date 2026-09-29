package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import gg.anarchia.itemy.util.Particles;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/** Zatruty ołówek - po uderzeniu przeciwnik dostaje efekt zatrucia. */
public class ZatrutyOlowek extends CustomItem implements Handlers.Attack {

    public ZatrutyOlowek() {
        super("zatrutyolowek", Material.STICK, "&2&lZatruty Ołówek");
        category("Bronie");
        model(10052);
        lore("&7Po uderzeniu przeciwnika",
                "&7zatruwasz go na &c%seconds%s&7.",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("poison-seconds", 6.0D);
        setting("poison-level", 2);
        cooldown(2.0D);
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, ItemStack item, EntityDamageByEntityEvent event) {
        if (!checkCooldown(attacker)) {
            return;
        }
        Effects.apply(victim, Effects.POISON, (int) (settingDouble("poison-seconds") * 20), Math.max(0, settingInt("poison-level") - 1));
        Particles.spawn(victim.getLocation().add(0, 1.2D, 0), Particles.WITCH, 20);
    }
}
