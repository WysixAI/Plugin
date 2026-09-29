package gg.anarchia.itemy.listener;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.Keys;
import gg.anarchia.itemy.region.Region;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/** Egzekwowanie flag regionow + rozdzka zaznaczania. */
public class RegionListener implements Listener {

    private final AnarchiaItemy plugin;

    public RegionListener(AnarchiaItemy plugin) {
        this.plugin = plugin;
    }

    private boolean isWand(ItemStack stack) {
        if (stack == null || stack.getItemMeta() == null) {
            return false;
        }
        return stack.getItemMeta().getPersistentDataContainer().has(Keys.REGION_WAND, PersistentDataType.BYTE);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWand(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (!isWand(item) || event.getClickedBlock() == null) {
            return;
        }
        if (!player.hasPermission("Anarchiaitemy.region") && !player.hasPermission("iAnarchiaitemy.admin")) {
            return;
        }
        event.setCancelled(true);
        Location location = event.getClickedBlock().getLocation();
        Action action = event.getAction();
        int index = action == Action.LEFT_CLICK_BLOCK ? 0 : 1;
        plugin.regions().setSelection(player, index, location);
        plugin.messages().send(player, "region-pos",
                "%pos%", String.valueOf(index + 1),
                "%x%", String.valueOf(location.getBlockX()),
                "%y%", String.valueOf(location.getBlockY()),
                "%z%", String.valueOf(location.getBlockZ()));
        Sounds.play(player, Sounds.PLING, 0.7F, index == 0 ? 1.0F : 1.5F);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (!plugin.regions().canBuild(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
            plugin.messages().send(event.getPlayer(), "region-deny-build");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (!plugin.regions().canBuild(event.getPlayer(), event.getBlock().getLocation())) {
            event.setCancelled(true);
            plugin.messages().send(event.getPlayer(), "region-deny-build");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!plugin.regions().canInteract(event.getPlayer(), event.getClickedBlock().getLocation())) {
            event.setCancelled(true);
            plugin.messages().send(event.getPlayer(), "region-deny-build");
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPvp(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        Player attacker = null;
        if (event.getDamager() instanceof Player player) {
            attacker = player;
        } else if (event.getDamager() instanceof org.bukkit.entity.Projectile projectile
                && projectile.getShooter() instanceof Player player) {
            attacker = player;
        }
        if (attacker == null) {
            return;
        }
        if (!plugin.regions().canPvp(attacker, victim.getLocation()) || !plugin.regions().canPvp(attacker, attacker.getLocation())) {
            event.setCancelled(true);
            plugin.messages().send(attacker, "region-deny-pvp");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        if (!plugin.regions().allowsExplosions(event.getLocation())) {
            event.blockList().clear();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!plugin.regions().allowsExplosions(event.getBlock().getLocation())) {
            event.blockList().clear();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.regions().enabled()) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())) {
            return;
        }
        Player player = event.getPlayer();
        Region region = plugin.regions().at(to);
        String current = region == null ? null : region.getName();
        String last = plugin.regions().getLastRegion(player);
        if (region != null && !region.getFlag("enter", true)
                && !player.hasPermission("iAnarchiaitemy.bypass.region")) {
            event.setCancelled(true);
            Text.actionBar(player, plugin.messages().raw("region-deny-items"));
            return;
        }
        if (current == null ? last == null : current.equals(last)) {
            return;
        }
        plugin.regions().setLastRegion(player, current);
        if (current != null) {
            Text.title(player, plugin.messages().raw("region-enter", "%region%", current), "", 5, 30, 5);
        }
    }
}
