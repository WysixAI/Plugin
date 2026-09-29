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

/** Spleśniała kanapka - po uderzeniu zaraża przeciwnika chorobą. */
public class SplesnialaKanapka extends CustomItem implements Handlers.Attack {

    public SplesnialaKanapka() {
        super("splesnialakanapka", Material.BREAD, "&2&lSpleśniała Kanapka");
        category("Bronie");
        model(10038);
        lore("&7Po uderzeniu zarażasz przeciwnika chorobą:",
                "&8• &cZatrucie&7, &cGłód",
                "&8• &cSłabość&7, &cNudności",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("disease-seconds", 8.0D);
        cooldown(6.0D);
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, ItemStack item, EntityDamageByEntityEvent event) {
        if (!checkCooldown(attacker)) {
            return;
        }
        int ticks = (int) (settingDouble("disease-seconds") * 20);
        Effects.apply(victim, Effects.POISON, ticks, 0);
        Effects.apply(victim, Effects.HUNGER, ticks, 1);
        Effects.apply(victim, Effects.WEAKNESS, ticks, 0);
        Effects.apply(victim, Effects.NAUSEA, ticks, 0);
        Particles.spawn(victim.getLocation().add(0, 1.2D, 0), Particles.ANGRY_VILLAGER, 10);
        Sounds.play(victim.getLocation(), Sounds.BURP, 1.0F, 0.7F);
    }
}
