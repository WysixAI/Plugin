package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/** Nieskończona fajerwerka - pozwala na nieskończone latanie na elytrze. */
public class NieskonczonaFajerwerka extends CustomItem implements Handlers.RightClick {

    public NieskonczonaFajerwerka() {
        super("nieskonczonafajerwerka", Material.FIREWORK_ROCKET, "&e&lNieskończona Fajerwerka");
        category("Wsparcie");
        model(10025);
        lore("&7Pozwala na &cnieskończone &7latanie na elytrze.",
                "&7Nigdy się nie zużywa!",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("power", 1.15D);
        setting("max-speed", 3.2D);
        cooldown(0.4D);
        flags("HIDE_POTION_EFFECTS", "HIDE_ENCHANTS");
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!player.isGliding()) {
            Text.actionBar(player, "&c&lELYTRA! &7Najpierw wznieś się i rozłóż elytrę");
            return;
        }
        if (!checkCooldown(player)) {
            return;
        }
        Vector direction = player.getLocation().getDirection().normalize().multiply(settingDouble("power"));
        Vector velocity = player.getVelocity().add(direction);
        double max = settingDouble("max-speed");
        if (velocity.length() > max) {
            velocity = velocity.normalize().multiply(max);
        }
        player.setVelocity(velocity);
        Particles.spawn(player.getLocation(), Particles.FIREWORK, 20, 0.3D, 0.3D, 0.3D, 0.05D);
        Sounds.play(player.getLocation(), Sounds.FIREWORK, 0.7F, 1.2F);
    }
}
