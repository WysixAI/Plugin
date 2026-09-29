package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.PlayerUtil;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

/** Krew wampira - PPM przywraca pełne zdrowie. */
public class KrewWampira extends CustomItem implements Handlers.RightClick {

    public KrewWampira() {
        super("krewwampira", Material.POTION, "&4&lKrew Wampira");
        category("Wsparcie");
        model(10015);
        lore("&7PPM: przywraca &cpełne zdrowie&7.",
                "",
                "&8» &cAnarchia.GG");
        setting("consume", true);
        setting("absorption-seconds", 0.0D);
        cooldown(20.0D);
        flags("HIDE_POTION_EFFECTS", "HIDE_ENCHANTS", "HIDE_ATTRIBUTES");
    }

    @Override
    protected void decorate(ItemMeta meta) {
        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(Color.fromRGB(139, 0, 0));
        }
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        event.setCancelled(true);
        if (!checkCooldown(player)) {
            return;
        }
        PlayerUtil.healFull(player);
        double absorption = settingDouble("absorption-seconds");
        if (absorption > 0) {
            gg.anarchia.itemy.util.Effects.apply(player, gg.anarchia.itemy.util.Effects.ABSORPTION, (int) (absorption * 20), 1);
        }
        Particles.spawn(player.getLocation().add(0, 1.0D, 0), Particles.HEART, 20);
        Sounds.play(player, Sounds.DRINK, 1.0F, 0.8F);
        if (settingBool("consume")) {
            ItemUtil.consumeOne(player, event.getHand());
        }
    }
}
