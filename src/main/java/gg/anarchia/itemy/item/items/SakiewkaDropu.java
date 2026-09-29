package gg.anarchia.itemy.item.items;

import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.Handlers;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Particles;
import gg.anarchia.itemy.util.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Sakiewka dropu - przedmioty zabitego gracza trafiają do twojego ekwipunku. */
public class SakiewkaDropu extends CustomItem implements Handlers.Kill {

    public SakiewkaDropu() {
        super("sakiewkadropu", Material.BUNDLE, "&6&lSakiewka Dropu");
        category("Wsparcie");
        model(10033);
        lore("&7Po zabiciu gracza jego przedmioty",
                "&7trafiają prosto do &ctwojego ekwipunku&7.",
                "",
                "&8» &cAnarchia.GG");
        glow();
        setting("collect-experience", true);
    }

    @Override
    public void onKill(Player killer, Player victim, ItemStack item, PlayerDeathEvent event) {
        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        if (drops.isEmpty()) {
            return;
        }
        event.getDrops().clear();
        for (ItemStack drop : drops) {
            if (!ItemUtil.isEmpty(drop)) {
                ItemUtil.give(killer, drop);
            }
        }
        if (settingBool("collect-experience") && event.getDroppedExp() > 0) {
            killer.giveExp(event.getDroppedExp());
            event.setDroppedExp(0);
        }
        Particles.spawn(killer.getLocation().add(0, 1.0D, 0), Particles.HAPPY_VILLAGER, 20);
        Sounds.play(killer, Sounds.ORB, 1.0F, 1.2F);
        plugin.messages().send(killer, "sakiewka-collected", "%player%", victim.getName());
    }
}
