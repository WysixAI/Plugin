package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Particles;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/** Mace - pełne obrażenia tylko w Endzie, poza nim prawie nie rani. */
public class MaceItem extends CustomItem implements Handlers.Attack {

    public MaceItem() {
        super("mace", Material.MACE, "&5&lMace");
        category("Bronie");
        model(10021);
        lore("&7Zadaje pełne obrażenia tylko w &5Endzie&7.",
                "&7Poza nim prawie nie rani.",
                "",
                "&8» &cAnarchia.GG");
        enchant("density", 5);
        enchant("unbreaking", 5);
        unbreakable();
        setting("damage-outside-end", 1.0D);
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, ItemStack item, EntityDamageByEntityEvent event) {
        if (attacker.getWorld().getEnvironment() == World.Environment.THE_END) {
            Particles.spawn(victim.getLocation().add(0, 1.0D, 0), Particles.DRAGON_BREATH, 15);
            return;
        }
        event.setDamage(Math.max(0.0D, settingDouble("damage-outside-end")));
        Particles.spawn(victim.getLocation().add(0, 1.0D, 0), Particles.SMOKE, 10);
    }
}
