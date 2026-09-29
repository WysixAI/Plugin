package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Wędka nielota - złapany gracz nie może lecieć elytrą. */
public class WedkaNielota extends CustomItem implements Handlers.Fish {

    private final Map<UUID, Long> blocked = new HashMap<>();

    public WedkaNielota() {
        super("wedkanielota", Material.FISHING_ROD, "&e&lWędka Nielota");
        category("Wędki");
        model(10046);
        lore("&7Po złapaniu gracza na haczyk",
                "&7nie może on odlecieć &celytrą &7przez &c%seconds%s&7.",
                "",
                "&8» &cAnarchia.GG");
        unbreakable();
        glow();
        setting("seconds", 10.0D);
        cooldown(3.0D);
    }

    @Override
    public void onFish(Player player, ItemStack item, PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) {
            return;
        }
        Entity caught = event.getCaught();
        if (!(caught instanceof Player target)) {
            return;
        }
        if (!checkCooldown(player)) {
            return;
        }
        long millis = (long) (settingDouble("seconds") * 1000L);
        blocked.put(target.getUniqueId(), System.currentTimeMillis() + millis);
        target.setGliding(false);
        Particles.spawn(target.getLocation().add(0, 1.0D, 0), Particles.LARGE_SMOKE, 25);
        Sounds.play(target.getLocation(), Sounds.CHAIN, 1.0F, 0.8F);
        Text.actionBar(target, "&e&lNIELOT! &7Nie możesz latać elytrą!");
        Text.actionBar(player, "&e&lZŁAPANY! &7" + target.getName() + " nie odleci elytrą");
    }

    public boolean isBlocked(Player player) {
        Long until = blocked.get(player.getUniqueId());
        if (until == null) {
            return false;
        }
        if (until < System.currentTimeMillis()) {
            blocked.remove(player.getUniqueId());
            return false;
        }
        return true;
    }
}
