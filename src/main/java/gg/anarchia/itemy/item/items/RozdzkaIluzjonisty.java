package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Cooldowns;
import gg.anarchia.itemy.util.Effects;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Tasks;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/** Różdżka Iluzjonisty - LPM: szczęki Evokera, PPM: znikasz i tworzysz klona. */
public class RozdzkaIluzjonisty extends CustomItem implements Handlers.LeftClick, Handlers.RightClick {

    public RozdzkaIluzjonisty() {
        super("rozdzkailuzjonisty", Material.BLAZE_ROD, "&5&lRóżdżka Iluzjonisty");
        category("Bronie");
        model(10031);
        lore("&7&lLPM: &7przywołuje &5Szczęki Evokera",
                "&7atakujące przeciwników na wprost.",
                "&7&lPPM: &7znikasz na &c%seconds%s &7i tworzysz",
                "&7klona biegnącego do przodu.",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("fangs-count", 8);
        setting("fangs-cooldown", 8.0D);
        setting("clone-seconds", 4.0D);
        setting("clone-speed", 0.35D);
        setting("clone-cooldown", 25.0D);
    }

    @Override
    public void onLeftClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!Cooldowns.isReady(player, getId() + ":fangs")) {
            Text.actionBar(player, "&c&lPOCZEKAJ! &7" + Cooldowns.format(Cooldowns.remaining(player, getId() + ":fangs")) + "s");
            return;
        }
        Cooldowns.set(player, getId() + ":fangs", settingDouble("fangs-cooldown"));
        int count = Math.max(1, settingInt("fangs-count"));
        Vector direction = player.getLocation().getDirection().setY(0);
        if (direction.lengthSquared() < 0.01D) {
            direction = new Vector(1, 0, 0);
        }
        final Vector step = direction.normalize();
        final Location start = player.getLocation().clone();
        new BukkitRunnable() {
            private int spawned = 0;

            @Override
            public void run() {
                spawned++;
                if (spawned > count || !player.isOnline()) {
                    cancel();
                    return;
                }
                Location target = ground(start.clone().add(step.clone().multiply(spawned * 1.4D)));
                if (target == null || target.getWorld() == null) {
                    return;
                }
                EvokerFangs fangs = target.getWorld().spawn(target, EvokerFangs.class);
                fangs.setOwner(player);
                Particles.spawn(target, Particles.WITCH, 6);
            }
        }.runTaskTimer(plugin, 0L, 2L);
        Sounds.play(player.getLocation(), Sounds.EVOKER_FANGS, 1.0F, 1.0F);
    }

    private Location ground(Location base) {
        for (int offset = 2; offset >= -3; offset--) {
            Location check = base.clone().add(0, offset, 0);
            if (check.getBlock().getType().isSolid() && check.clone().add(0, 1, 0).getBlock().getType().isAir()) {
                return check.add(0, 1, 0);
            }
        }
        return base;
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!Cooldowns.isReady(player, getId() + ":clone")) {
            Text.actionBar(player, "&c&lPOCZEKAJ! &7" + Cooldowns.format(Cooldowns.remaining(player, getId() + ":clone")) + "s");
            return;
        }
        Cooldowns.set(player, getId() + ":clone", settingDouble("clone-cooldown"));
        int ticks = (int) (settingDouble("clone-seconds") * 20);
        Effects.apply(player, Effects.INVISIBILITY, ticks, 0);
        Particles.spawn(player.getLocation().add(0, 1.0D, 0), Particles.LARGE_SMOKE, 40);
        Sounds.play(player.getLocation(), Sounds.ILLUSIONER_CAST, 1.0F, 1.0F);

        Location spawnLocation = player.getLocation().clone();
        if (spawnLocation.getWorld() == null) {
            return;
        }
        ArmorStand clone = spawnLocation.getWorld().spawn(spawnLocation, ArmorStand.class, stand -> {
            stand.setArms(true);
            stand.setBasePlate(false);
            stand.setInvulnerable(true);
            stand.setCanPickupItems(false);
            stand.customName(Text.parse(player.getName()));
            stand.setCustomNameVisible(true);
            EntityEquipment equipment = stand.getEquipment();
            if (equipment != null) {
                equipment.setHelmet(head(player));
                equipment.setChestplate(copy(player.getInventory().getChestplate()));
                equipment.setLeggings(copy(player.getInventory().getLeggings()));
                equipment.setBoots(copy(player.getInventory().getBoots()));
                equipment.setItemInMainHand(copy(player.getInventory().getItemInMainHand()));
            }
        });
        final Vector direction = player.getLocation().getDirection().setY(0).normalize().multiply(settingDouble("clone-speed"));
        new BukkitRunnable() {
            private int elapsed = 0;

            @Override
            public void run() {
                elapsed += 2;
                if (elapsed >= ticks || !clone.isValid()) {
                    if (clone.isValid()) {
                        Particles.spawn(clone.getLocation().add(0, 1.0D, 0), Particles.LARGE_SMOKE, 25);
                        clone.remove();
                    }
                    cancel();
                    return;
                }
                Location next = clone.getLocation().add(direction);
                next.setYaw((float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ())));
                clone.teleport(next);
                Particles.spawn(clone.getLocation(), Particles.WITCH, 3);
            }
        }.runTaskTimer(plugin, 2L, 2L);
    }

    private ItemStack copy(ItemStack stack) {
        return stack == null ? null : stack.clone();
    }

    private ItemStack head(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (head.getItemMeta() instanceof SkullMeta meta) {
            meta.setOwningPlayer(player);
            head.setItemMeta(meta);
        }
        return head;
    }
}
