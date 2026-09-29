package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.Effects;
import gg.anarchia.itemy.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Anarchiczna zbroja / Anarchiczna zbroja 2.
 * Drugi set posiada dodatkowo bonus za pelny komplet.
 */
public class ZbrojaItem extends CustomItem implements Handlers.Wear {

    private final boolean secondSet;
    private final String setId;

    public ZbrojaItem(String id, Material material, String name, int model, boolean secondSet, String setId) {
        super(id, material, name);
        this.secondSet = secondSet;
        this.setId = setId;
        category("Zbroje");
        model(model);
        unbreakable();
        glow();
        if (secondSet) {
            lore("&7Ulepszona zbroja z serwera &cAnarchia.GG&7.",
                    "&7Za pełny komplet: &cOdporność I &7oraz &cSzybkość I&7.",
                    "",
                    "&8» &cAnarchia.GG");
            enchant("protection", 6);
            enchant("unbreaking", 10);
            enchant("fire_protection", 4);
            enchant("feather_falling", 4);
            enchant("thorns", 3);
            setting("set-bonus", true);
        } else {
            lore("&7Zbroja prosto z serwera &cAnarchia.GG&7.",
                    "&7Wytrzymała na każdą bitwę eventową.",
                    "",
                    "&8» &cAnarchia.GG");
            enchant("protection", 4);
            enchant("unbreaking", 5);
            enchant("feather_falling", 2);
        }
        flags("HIDE_ENCHANTS", "HIDE_ATTRIBUTES", "HIDE_UNBREAKABLE");
    }

    public String getSetId() {
        return setId;
    }

    @Override
    public void onWear(Player player, ItemStack item) {
        if (!secondSet || !settingBool("set-bonus")) {
            return;
        }
        if (!hasFullSet(player)) {
            return;
        }
        Effects.refresh(player, Effects.RESISTANCE, 100, 0);
        Effects.refresh(player, Effects.SPEED, 100, 0);
    }

    private boolean hasFullSet(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        int found = 0;
        for (ItemStack piece : armor) {
            String id = ItemUtil.getId(piece);
            if (id != null && id.startsWith(setId)) {
                found++;
            }
        }
        return found >= 4;
    }
}
