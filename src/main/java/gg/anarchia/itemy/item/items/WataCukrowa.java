package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/** Wata cukrowa - naprawia jeden element zbroi do pełnej trwałości. */
public class WataCukrowa extends CustomItem implements Handlers.RightClick {

    public WataCukrowa() {
        super("watacukrowa", Material.PINK_DYE, "&d&lWata Cukrowa");
        category("Wsparcie");
        model(10044);
        lore("&7PPM: rozpoczyna naprawę jednego",
                "&7elementu twojej zbroi do pełna.",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("repair-seconds", 2.0D);
        setting("consume", true);
        cooldown(3.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        ItemStack[] armor = player.getInventory().getArmorContents();
        int index = -1;
        int worst = 0;
        for (int i = 0; i < armor.length; i++) {
            ItemStack piece = armor[i];
            if (ItemUtil.isEmpty(piece)) {
                continue;
            }
            ItemMeta meta = piece.getItemMeta();
            if (!(meta instanceof Damageable damageable)) {
                continue;
            }
            if (damageable.getDamage() > worst) {
                worst = damageable.getDamage();
                index = i;
            }
        }
        if (index < 0) {
            Text.send(player, plugin.messages().prefix() + "&cCała twoja zbroja jest już naprawiona!");
            return;
        }
        if (!checkCooldown(player)) {
            return;
        }
        if (settingBool("consume")) {
            ItemUtil.consumeOne(player, event.getHand());
        }
        final int slot = index;
        final int startDamage = worst;
        final int steps = Math.max(1, (int) (settingDouble("repair-seconds") * 4));
        new BukkitRunnable() {
            private int step = 0;

            @Override
            public void run() {
                step++;
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                ItemStack[] current = player.getInventory().getArmorContents();
                ItemStack piece = current[slot];
                if (ItemUtil.isEmpty(piece)) {
                    cancel();
                    return;
                }
                ItemMeta meta = piece.getItemMeta();
                if (!(meta instanceof Damageable damageable)) {
                    cancel();
                    return;
                }
                int newDamage = (int) (startDamage * (1.0D - ((double) step / steps)));
                damageable.setDamage(Math.max(0, newDamage));
                piece.setItemMeta(meta);
                current[slot] = piece;
                player.getInventory().setArmorContents(current);
                Particles.spawn(player.getLocation().add(0, 1.0D, 0), Particles.HAPPY_VILLAGER, 6);
                Sounds.play(player, "block.anvil.use", 0.4F, 1.8F);
                if (step >= steps) {
                    Sounds.play(player, Sounds.LEVEL_UP, 0.8F, 1.6F);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 5L, 5L);
    }
}
