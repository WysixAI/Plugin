package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Structures;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Turbo-domek - po wystrzeleniu buduje prostą pułapkę/domek w miejscu eksplozji. */
public class TurboDomek extends CustomItem implements Handlers.RightClick, Handlers.Hit {

    public TurboDomek() {
        super("turbodomek", Material.OAK_DOOR, "&6&lTurbo-Domek");
        category("Sabotaż");
        model(10041);
        lore("&7Podczas wystrzelenia budujesz",
                "&7prostą &cpułapkę &7w miejscu eksplozji!",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("speed", 1.6D);
        setting("radius", 2);
        setting("height", 4);
        setting("wall-material", "OBSIDIAN");
        setting("roof-material", "OBSIDIAN");
        setting("floor-material", "OBSIDIAN");
        setting("duration-seconds", 45.0D);
        setting("consume", true);
        cooldown(30.0D);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(player.getLocation().getDirection().multiply(settingDouble("speed")));
        ItemUtil.tag(projectile, getId());
        Sounds.play(player.getLocation(), "entity.snowball.throw", 1.0F, 0.8F);
        if (settingBool("consume")) {
            ItemUtil.consumeOne(player, event.getHand());
        }
    }

    @Override
    public void onProjectileHit(Player shooter, Projectile projectile, ProjectileHitEvent event) {
        Location center = event.getHitEntity() != null
                ? event.getHitEntity().getLocation()
                : projectile.getLocation();
        projectile.remove();
        Material wall = material("wall-material", Material.OBSIDIAN);
        Material roof = material("roof-material", Material.OBSIDIAN);
        Material floor = material("floor-material", Material.OBSIDIAN);
        long ticks = (long) (settingDouble("duration-seconds") * 20);
        Structures.box(plugin, shooter, center, Math.max(1, settingInt("radius")), Math.max(2, settingInt("height")),
                wall, roof, floor, ticks);
        Particles.spawn(center, Particles.EXPLOSION, 5, 1.0D, 1.0D, 1.0D, 0.0D);
        Sounds.play(center, Sounds.ANVIL_LAND, 1.0F, 0.8F);
    }

    private Material material(String path, Material fallback) {
        Material material = Material.matchMaterial(settingString(path));
        return material == null ? fallback : material;
    }
}
