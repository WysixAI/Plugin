package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.PlayerUtil;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Wędka Guardiania - przyzywa Strażnika nakładającego zmęczenie kopania. */
public class WedkaGuardiania extends CustomItem implements Handlers.Fish, Handlers.Cleanup {

    private final Map<UUID, LivingEntity> guardians = new HashMap<>();

    public WedkaGuardiania() {
        super("wedkaguardiania", Material.FISHING_ROD, "&3&lWędka Guardiania");
        category("Wędki");
        model(10045);
        lore("&7Zarzuć wędkę, aby przyzwać &3Strażnika Głębin&7,",
                "&7który w promieniu &c%radius% &7kratek nakłada",
                "&7&cZmęczenie kopania I &7na wrogów.",
                "&7Musisz trzymać wędkę - inaczej strażnik znika.",
                "&7Po zabiciu efekt działa jeszcze &c3s&7.",
                "",
                "&8» &cAnarchia.GG");
        unbreakable();
        glow();
        setting("entity", "GUARDIAN");
        setting("radius", 6.0D);
        setting("fatigue-level", 1);
        setting("linger-seconds", 3.0D);
        setting("health", 20.0D);
        cooldown(5.0D);
    }

    @Override
    public void onFish(Player player, ItemStack item, PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.FISHING) {
            return;
        }
        if (guardians.containsKey(player.getUniqueId())) {
            return;
        }
        if (!checkCooldown(player)) {
            return;
        }
        Location location = event.getHook().getLocation();
        if (location.getWorld() == null) {
            return;
        }
        EntityType type;
        try {
            type = EntityType.valueOf(settingString("entity").toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            type = EntityType.GUARDIAN;
        }
        Entity spawned = location.getWorld().spawnEntity(location, type);
        if (!(spawned instanceof LivingEntity guardian)) {
            spawned.remove();
            return;
        }
        guardian.setRemoveWhenFarAway(true);
        guardian.customName(Text.parse("&3&lSTRAŻNIK GŁĘBIN &8| &c" + player.getName()));
        guardian.setCustomNameVisible(true);
        ItemUtil.tag(guardian, getId());
        double health = settingDouble("health");
        if (health > 0) {
            org.bukkit.attribute.AttributeInstance attribute = guardian.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
            if (attribute != null) {
                attribute.setBaseValue(health);
            }
            guardian.setHealth(Math.min(health, PlayerUtil.getMaxHealth(guardian)));
        }
        guardians.put(player.getUniqueId(), guardian);
        Particles.spawn(location, Particles.SPLASH, 40);
        Sounds.play(location, Sounds.GUARDIAN_AMBIENT, 1.0F, 1.0F);

        final UUID uuid = player.getUniqueId();
        new BukkitRunnable() {
            private int lingerTicks = -1;

            @Override
            public void run() {
                LivingEntity current = guardians.get(uuid);
                Player owner = org.bukkit.Bukkit.getPlayer(uuid);
                if (owner == null || !owner.isOnline() || current == null) {
                    remove(current);
                    cancel();
                    return;
                }
                boolean holding = ItemUtil.holds(owner, getId());
                boolean alive = current.isValid() && !current.isDead();
                if (!holding) {
                    remove(current);
                    Text.actionBar(owner, "&3&lSTRAŻNIK &7zniknął - nie trzymasz wędki!");
                    cancel();
                    return;
                }
                if (!alive) {
                    if (lingerTicks < 0) {
                        lingerTicks = (int) (settingDouble("linger-seconds") * 20);
                    }
                    lingerTicks -= 10;
                    if (lingerTicks <= 0) {
                        remove(current);
                        cancel();
                        return;
                    }
                }
                Location center = alive ? current.getLocation() : owner.getLocation();
                double radius = settingDouble("radius");
                int level = Math.max(0, settingInt("fatigue-level") - 1);
                for (LivingEntity target : PlayerUtil.nearbyLiving(center, radius, owner)) {
                    if (target.getUniqueId().equals(uuid) || (current != null && target.getUniqueId().equals(current.getUniqueId()))) {
                        continue;
                    }
                    Effects.apply(target, Effects.MINING_FATIGUE, 40, level);
                }
                if (alive) {
                    Particles.circle(center, Particles.BUBBLE, radius, 24);
                }
            }

            private void remove(LivingEntity entity) {
                guardians.remove(uuid);
                if (entity != null && entity.isValid()) {
                    Particles.spawn(entity.getLocation(), Particles.SPLASH, 30);
                    entity.remove();
                }
            }
        }.runTaskTimer(plugin, 10L, 10L);
    }

    @Override
    public void cleanup(Player player) {
        LivingEntity guardian = guardians.remove(player.getUniqueId());
        if (guardian != null && guardian.isValid()) {
            guardian.remove();
        }
    }

    public void removeAll() {
        for (LivingEntity guardian : guardians.values()) {
            if (guardian != null && guardian.isValid()) {
                guardian.remove();
            }
        }
        guardians.clear();
    }
}
