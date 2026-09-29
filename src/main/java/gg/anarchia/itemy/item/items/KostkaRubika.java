package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Kostka Rubika - uderz gracza, aby przemieszać jego ekwipunek. */
public class KostkaRubika extends CustomItem implements Handlers.Attack {

    public KostkaRubika() {
        super("kostkarubika", Material.SLIME_BLOCK, "&a&lKostka Rubika");
        category("Sabotaż");
        model(10014);
        lore("&7Uderz gracza, aby jego ekwipunek",
                "&7został całkowicie przemieszany!",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("shuffle-hotbar", true);
        cooldown(15.0D);
    }

    @Override
    public void onAttack(Player attacker, LivingEntity victim, ItemStack item, EntityDamageByEntityEvent event) {
        if (!(victim instanceof Player target)) {
            return;
        }
        if (!checkCooldown(attacker)) {
            return;
        }
        ItemStack[] storage = target.getInventory().getStorageContents();
        int from = settingBool("shuffle-hotbar") ? 0 : 9;
        List<ItemStack> shuffled = new ArrayList<>(Arrays.asList(storage).subList(from, storage.length));
        Collections.shuffle(shuffled);
        for (int i = from; i < storage.length; i++) {
            storage[i] = shuffled.get(i - from);
        }
        target.getInventory().setStorageContents(storage);
        target.updateInventory();
        Particles.spawn(target.getLocation().add(0, 1.0D, 0), Particles.HAPPY_VILLAGER, 25);
        Sounds.play(target.getLocation(), Sounds.PLING, 1.0F, 1.8F);
    }
}
