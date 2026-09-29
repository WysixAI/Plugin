package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Anty cobweb - usuwa pajęczyny w promieniu 3 kratek od gracza. */
public class AntyCobweb extends CustomItem implements Handlers.RightClick {

    public AntyCobweb() {
        super("antycobweb", Material.SHEARS, "&f&lAnty Cobweb");
        category("Narzędzia");
        model(10005);
        lore("&7Kliknij PPM, aby usunąć wszystkie",
                "&7pajęczyny w promieniu &c%radius% &7kratek.",
                "",
                "&8» &cAnarchia.GG");
        unbreakable();
        glow();
        setting("radius", 3);
        setting("consume", false);
        cooldown(1.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        int radius = Math.max(1, settingInt("radius"));
        Location center = player.getLocation();
        int removed = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = center.clone().add(x, y, z).getBlock();
                    if (block.getType() != Material.COBWEB) {
                        continue;
                    }
                    if (!canBuild(player, block.getLocation())) {
                        continue;
                    }
                    block.setType(Material.AIR, false);
                    Particles.spawn(block.getLocation().add(0.5D, 0.5D, 0.5D), Particles.CLOUD, 3, 0.2D, 0.2D, 0.2D, 0.01D);
                    removed++;
                }
            }
        }
        if (removed > 0) {
            Sounds.play(player, "block.wool.break", 1.0F, 1.4F);
            if (settingBool("consume")) {
                gg.anarchia.itemy.util.ItemUtil.consumeOne(player, event.getHand());
            }
        }
    }
}
