package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Dynamit - PPM w skałę macierzystą niszczy ją. */
public class Dynamit extends CustomItem implements Handlers.RightClick {

    public Dynamit() {
        super("dynamit", Material.TNT, "&c&lDynamit");
        category("Narzędzia");
        model(10010);
        lore("&7Kliknij PPM w blok, aby go zniszczyć.",
                "&7Działa nawet na &cskale macierzystej&7!",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("consume", true);
        setting("only-bedrock", false);
        setting("radius", 0);
        cooldown(1.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        Block clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }
        if (settingBool("only-bedrock") && clicked.getType() != Material.BEDROCK) {
            return;
        }
        if (!canBuild(player, clicked.getLocation())) {
            plugin.messages().send(player, "region-deny-build");
            return;
        }
        if (!checkCooldown(player)) {
            return;
        }
        int radius = Math.max(0, settingInt("radius"));
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = clicked.getRelative(x, y, z);
                    if (block.getType().isAir() || !canBuild(player, block.getLocation())) {
                        continue;
                    }
                    block.setType(Material.AIR, false);
                }
            }
        }
        Particles.spawn(clicked.getLocation().add(0.5D, 0.5D, 0.5D), Particles.EXPLOSION, 3, 0.3D, 0.3D, 0.3D, 0.0D);
        Sounds.play(clicked.getLocation(), Sounds.EXPLODE, 1.0F, 1.4F);
        if (settingBool("consume")) {
            ItemUtil.consumeOne(player, event.getHand());
        }
    }
}
