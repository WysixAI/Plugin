package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.Keys;
import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Enchants;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/** Excalibur - z każdym zabójstwem zyskuje wyższy poziom Sharpness. */
public class Excalibur extends CustomItem implements Handlers.Kill {

    public Excalibur() {
        super("excalibur", Material.GOLDEN_SWORD, "&e&lExcalibur");
        category("Bronie");
        model(10011);
        lore("&7Legendarny miecz, który rośnie w siłę",
                "&7z każdym zabitym graczem.",
                "",
                "&7Zabójstwa: &c%kills%",
                "&7Sharpness: &c%level%",
                "",
                "&8» &cAnarchia.GG");
        unbreakable();
        glow();
        setting("base-level", 1);
        setting("max-level", 10);
        setting("kills-per-level", 1);
    }

    public int levelFor(int kills) {
        int base = Math.max(1, settingInt("base-level"));
        int perLevel = Math.max(1, settingInt("kills-per-level"));
        int max = Math.max(base, settingInt("max-level"));
        return Math.min(max, base + kills / perLevel);
    }

    public int getKills(ItemStack stack) {
        return ItemUtil.getInt(stack, Keys.KILLS, 0);
    }

    /** Ustawia licznik zabójstw i przelicza zaklęcie oraz opis. */
    public void applyKills(ItemStack stack, int kills) {
        if (ItemUtil.isEmpty(stack)) {
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return;
        }
        int level = levelFor(kills);
        meta.getPersistentDataContainer().set(Keys.KILLS, PersistentDataType.INTEGER, kills);
        meta.addEnchant(Enchantment.SHARPNESS, level, true);
        List<String> lore = new ArrayList<>();
        for (String line : getLore()) {
            lore.add(Text.replace(line, "%kills%", String.valueOf(kills), "%level%", Enchants.roman(level)));
        }
        meta.lore(Text.parse(lore));
        stack.setItemMeta(meta);
    }

    @Override
    protected void finish(ItemStack stack) {
        applyKills(stack, ItemUtil.getInt(stack, Keys.KILLS, 0));
    }

    @Override
    public void onKill(Player killer, Player victim, ItemStack item, PlayerDeathEvent event) {
        if (!matches(killer.getInventory().getItemInMainHand())) {
            return;
        }
        int kills = getKills(item) + 1;
        int before = levelFor(kills - 1);
        applyKills(item, kills);
        int after = levelFor(kills);
        Particles.spawn(killer.getLocation().add(0, 1.0D, 0), Particles.ENCHANTED_HIT, 30);
        if (after > before) {
            Sounds.play(killer, Sounds.LEVEL_UP, 1.0F, 1.2F);
            plugin.messages().send(killer, "excalibur-levelup", "%level%", Enchants.roman(after), "%kills%", String.valueOf(kills));
        } else {
            Sounds.play(killer, Sounds.ORB, 1.0F, 1.4F);
        }
    }
}
