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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/** Zajęczy miecz - po uderzeniu przeciwnik nie może skakać przez 4 sekundy. */
public class ZajeczyMiecz extends CustomItem implements Handlers.Attack {

    public ZajeczyMiecz() {
        super("zajeczymiecz", Material.GOLDEN_SWORD, "&e&lZajęczy Miecz");
        category("Bronie");
        model(10051);
        lore("&7Po uderzeniu przeciwnik",
                "&7nie może skakać przez &c%seconds%s&7.",
                "",
                "&8» &cAnarchia.GG");
        enchant("sharpness", 4);
        unbreakable();
        setting("no-jump-seconds", 4.0D);
        cooldown(5.0D);
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, ItemStack item, EntityDamageByEntityEvent event) {
        if (!checkCooldown(attacker)) {
            return;
        }
        int ticks = (int) (settingDouble("no-jump-seconds") * 20);
        Effects.blockJump(victim, ticks);
        Particles.spawn(victim.getLocation(), Particles.CLOUD, 15);
        Sounds.play(victim.getLocation(), "entity.rabbit.jump", 1.0F, 0.8F);
        if (victim instanceof Player target) {
            Text.actionBar(target, "&e&lZAJĄC! &7Nie możesz skakać przez &e" + (int) settingDouble("no-jump-seconds") + "s");
        }
    }
}
