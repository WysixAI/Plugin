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

/** Kosa - po uderzeniu przeciwnika nadaje mu efekt ślepoty. */
public class Kosa extends CustomItem implements Handlers.Attack {

    public Kosa() {
        super("kosa", Material.NETHERITE_HOE, "&8&lKosa");
        category("Bronie");
        model(10013);
        lore("&7Po uderzeniu przeciwnika",
                "&7nakłada na niego &cŚlepotę&7.",
                "",
                "&8» &cAnarchia.GG");
        enchant("sharpness", 3);
        unbreakable();
        setting("blindness-seconds", 4.0D);
        setting("blindness-level", 1);
        cooldown(3.0D);
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, ItemStack item, EntityDamageByEntityEvent event) {
        if (!checkCooldown(attacker)) {
            return;
        }
        Effects.apply(victim, Effects.BLINDNESS, (int) (settingDouble("blindness-seconds") * 20), Math.max(0, settingInt("blindness-level") - 1));
        Particles.spawn(victim.getLocation().add(0, 1.5D, 0), Particles.SCULK_SOUL, 15);
        Sounds.play(victim.getLocation(), Sounds.WITHER_SHOOT, 0.8F, 1.6F);
    }
}
