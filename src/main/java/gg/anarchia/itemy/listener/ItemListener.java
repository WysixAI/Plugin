package gg.anarchia.itemy.listener;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.item.items.WedkaNielota;
import gg.anarchia.itemy.util.Cooldowns;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Glowny listener rozdzielajacy zdarzenia do przedmiotow. */
public class ItemListener implements Listener {

    private final AnarchiaItemy plugin;
    private final Map<UUID, String[]> tridentIntent = new HashMap<>();

    public ItemListener(AnarchiaItemy plugin) {
        this.plugin = plugin;
    }

    // ------------------------------------------------------------------
    //  Pomocnicze
    // ------------------------------------------------------------------

    private boolean allowed(Player player, CustomItem item, boolean notify) {
        if (item == null) {
            return false;
        }
        if (!item.isEnabled()) {
            if (notify) {
                plugin.messages().send(player, "item-disabled");
            }
            return false;
        }
        if (!plugin.isWorldEnabled(player.getWorld())) {
            if (notify) {
                plugin.messages().send(player, "world-disabled");
            }
            return false;
        }
        if (plugin.getConfig().getBoolean("settings.require-use-permission", false)
                && !player.hasPermission("iAnarchiaitemy.use")) {
            if (notify) {
                plugin.messages().send(player, "no-permission");
            }
            return false;
        }
        if (!plugin.regions().canUseItems(player, player.getLocation())) {
            if (notify) {
                plugin.messages().send(player, "region-deny-items");
            }
            return false;
        }
        return true;
    }

    private void safe(CustomItem item, Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception exception) {
            plugin.getLogger().warning("Blad w przedmiocie " + (item == null ? "?" : item.getId()) + ": " + exception);
            exception.printStackTrace();
        }
    }

    private List<ItemStack> equipment(Player player) {
        List<ItemStack> list = new ArrayList<>();
        list.add(player.getInventory().getItemInMainHand());
        list.add(player.getInventory().getItemInOffHand());
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (!ItemUtil.isEmpty(armor)) {
                list.add(armor);
            }
        }
        return list;
    }

    // ------------------------------------------------------------------
    //  Interakcje
    // ------------------------------------------------------------------

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == null) {
            return;
        }
        ItemStack item = event.getItem();
        if (ItemUtil.isEmpty(item)) {
            return;
        }
        Player player = event.getPlayer();
        CustomItem custom = plugin.items().get(item);
        if (custom == null) {
            return;
        }
        if (!allowed(player, custom, true)) {
            event.setCancelled(true);
            return;
        }
        if (item.getType() == Material.TRIDENT) {
            tridentIntent.put(player.getUniqueId(), new String[]{custom.getId(), String.valueOf(System.currentTimeMillis())});
        }
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (custom instanceof Handlers.RightClick handler) {
                safe(custom, () -> handler.onRightClick(player, item, event));
            }
        } else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            if (custom instanceof Handlers.LeftClick handler) {
                safe(custom, () -> handler.onLeftClick(player, item, event));
            }
        }
    }

    /** Eventowki nie sa blokami - nie pozwalamy ich postawic. */
    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        CustomItem custom = plugin.items().get(event.getItemInHand());
        if (custom != null && !custom.settingBool("placeable")) {
            event.setCancelled(true);
        }
    }

    // ------------------------------------------------------------------
    //  Walka
    // ------------------------------------------------------------------

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victimEntity = event.getEntity();

        if (damager instanceof Player attacker && victimEntity instanceof LivingEntity victim) {
            ItemStack weapon = attacker.getInventory().getItemInMainHand();
            CustomItem custom = plugin.items().get(weapon);
            if (custom instanceof Handlers.Attack handler && allowed(attacker, custom, false)) {
                safe(custom, () -> handler.onAttack(attacker, victim, weapon, event));
            }
        }

        if (damager instanceof Projectile projectile && victimEntity instanceof LivingEntity hitTarget) {
            CustomItem custom = plugin.items().fromEntity(projectile);
            if (custom instanceof Handlers.ProjectileDamage handler && custom.isEnabled()) {
                Player shooter = projectile.getShooter() instanceof Player player ? player : null;
                safe(custom, () -> handler.onProjectileDamage(shooter, projectile, hitTarget, event));
            }
        }

        if (victimEntity instanceof Player defender) {
            for (ItemStack stack : equipment(defender)) {
                CustomItem custom = plugin.items().get(stack);
                if (custom instanceof Handlers.Defend handler && allowed(defender, custom, false)) {
                    safe(custom, () -> handler.onDefend(defender, damager, stack, event));
                }
                if (event.isCancelled()) {
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        for (ItemStack stack : equipment(player)) {
            CustomItem custom = plugin.items().get(stack);
            if (custom instanceof Handlers.Damaged handler && custom.isEnabled()) {
                safe(custom, () -> handler.onDamaged(player, stack, event));
            }
        }
    }

    // ------------------------------------------------------------------
    //  Pociski
    // ------------------------------------------------------------------

    @EventHandler(ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack bow = event.getBow();
        CustomItem custom = plugin.items().get(bow);
        if (custom == null) {
            return;
        }
        if (!allowed(player, custom, true)) {
            event.setCancelled(true);
            return;
        }
        ItemUtil.tag(event.getProjectile(), custom.getId());
        if (custom instanceof Handlers.Shoot handler) {
            safe(custom, () -> handler.onShoot(player, bow, event));
        }
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (ItemUtil.getId(projectile) != null) {
            return;
        }
        if (!(projectile.getShooter() instanceof Player player)) {
            return;
        }
        if (!(projectile instanceof Trident)) {
            return;
        }
        CustomItem custom = plugin.items().get(player.getInventory().getItemInMainHand());
        if (custom == null) {
            custom = plugin.items().get(player.getInventory().getItemInOffHand());
        }
        if (custom == null) {
            String[] intent = tridentIntent.get(player.getUniqueId());
            if (intent != null && System.currentTimeMillis() - Long.parseLong(intent[1]) < 5000L) {
                custom = plugin.items().byId(intent[0]);
            }
        }
        if (custom == null || !custom.isEnabled()) {
            return;
        }
        if (custom.getMaterial() != Material.TRIDENT) {
            return;
        }
        ItemUtil.tag(projectile, custom.getId());
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        CustomItem custom = plugin.items().fromEntity(projectile);
        if (!(custom instanceof Handlers.Hit handler) || !custom.isEnabled()) {
            return;
        }
        Player shooter = projectile.getShooter() instanceof Player player ? player : null;
        safe(custom, () -> handler.onProjectileHit(shooter, projectile, event));
    }

    // ------------------------------------------------------------------
    //  Wedki, jedzenie, latanie
    // ------------------------------------------------------------------

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack rod = player.getInventory().getItemInMainHand();
        CustomItem custom = plugin.items().get(rod);
        if (custom == null) {
            rod = player.getInventory().getItemInOffHand();
            custom = plugin.items().get(rod);
        }
        if (!(custom instanceof Handlers.Fish handler)) {
            return;
        }
        if (!allowed(player, custom, true)) {
            return;
        }
        final ItemStack used = rod;
        safe(custom, () -> handler.onFish(player, used, event));
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        CustomItem custom = plugin.items().get(item);
        if (!(custom instanceof Handlers.Consume handler)) {
            return;
        }
        if (!allowed(player, custom, true)) {
            event.setCancelled(true);
            return;
        }
        safe(custom, () -> handler.onConsume(player, item, event));
    }

    @EventHandler(ignoreCancelled = true)
    public void onGlide(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player) || !event.isGliding()) {
            return;
        }
        CustomItem custom = plugin.items().byId("wedkanielota");
        if (custom instanceof WedkaNielota wedka && wedka.isBlocked(player)) {
            event.setCancelled(true);
            player.setGliding(false);
            Text.actionBar(player, "&e&lNIELOT! &7Nie możesz teraz latać elytrą!");
        }
    }

    // ------------------------------------------------------------------
    //  Smierc / zabojstwa
    // ------------------------------------------------------------------

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        ItemStack[] contents = victim.getInventory().getContents();
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack stack = contents[slot];
            CustomItem custom = plugin.items().get(stack);
            if (!(custom instanceof Handlers.Death handler) || !custom.isEnabled()) {
                continue;
            }
            final int index = slot;
            final ItemStack used = stack;
            safe(custom, () -> {
                if (handler.onDeath(victim, used, event)) {
                    if (used.getAmount() > 1) {
                        used.setAmount(used.getAmount() - 1);
                        victim.getInventory().setItem(index, used);
                    } else {
                        victim.getInventory().setItem(index, null);
                    }
                }
            });
            break;
        }

        Player killer = victim.getKiller();
        if (killer == null || killer.getUniqueId().equals(victim.getUniqueId())) {
            return;
        }
        ItemStack[] killerContents = killer.getInventory().getContents();
        for (int slot = 0; slot < killerContents.length; slot++) {
            ItemStack stack = killerContents[slot];
            CustomItem custom = plugin.items().get(stack);
            if (!(custom instanceof Handlers.Kill handler) || !custom.isEnabled()) {
                continue;
            }
            final int index = slot;
            final ItemStack used = stack;
            safe(custom, () -> {
                handler.onKill(killer, victim, used, event);
                killer.getInventory().setItem(index, used);
            });
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.items().cleanup(event.getPlayer());
        Cooldowns.clear(event.getPlayer().getUniqueId());
        tridentIntent.remove(event.getPlayer().getUniqueId());
    }
}
