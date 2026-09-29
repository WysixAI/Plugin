package gg.anarchia.itemy.listener;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.item.CustomItem;
import gg.anarchia.itemy.item.items.ZmutowanyCreeper;
import gg.anarchia.itemy.util.ItemUtil;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

/** Obsluga mobow pluginu (zmutowany creeper, TNT z eventu). */
public class MobListener implements Listener {

    private final AnarchiaItemy plugin;

    public MobListener(AnarchiaItemy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        String id = ItemUtil.getId(entity);

        if (!plugin.regions().allowsExplosions(event.getLocation())) {
            event.blockList().clear();
        }
        if (id == null) {
            return;
        }
        if ("tntrain".equals(id)) {
            if (!plugin.getConfig().getBoolean("events.tntrain.destroy-blocks", false)) {
                event.blockList().clear();
            }
            return;
        }
        CustomItem custom = plugin.items().byId(id);
        if (custom instanceof ZmutowanyCreeper creeper) {
            creeper.handleExplosion(entity);
            if (!custom.settingBool("destroy-blocks")) {
                event.blockList().clear();
            }
        }
    }
}
