package gg.anarchia.itemy.events;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Rejestr eventow serwerowych. */
public class EventManager {

    private final AnarchiaItemy plugin;
    private final Map<String, GameEvent> events = new LinkedHashMap<>();
    private GameEvent active;

    public EventManager(AnarchiaItemy plugin) {
        this.plugin = plugin;
        register(new DropPartyEvent(plugin));
        register(new LosowanieEvent(plugin));
        register(new KothEvent(plugin));
        register(new TntRainEvent(plugin));
    }

    public void register(GameEvent event) {
        events.put(event.getId().toLowerCase(Locale.ROOT), event);
    }

    public Collection<GameEvent> all() {
        return new ArrayList<>(events.values());
    }

    public List<String> ids() {
        return new ArrayList<>(events.keySet());
    }

    public GameEvent get(String id) {
        return id == null ? null : events.get(id.toLowerCase(Locale.ROOT));
    }

    public GameEvent active() {
        return active != null && active.isRunning() ? active : null;
    }

    public boolean isRunning() {
        return active() != null;
    }

    public boolean start(CommandSender sender, String id, String[] args) {
        GameEvent event = get(id);
        if (event == null) {
            plugin.messages().send(sender, "event-unknown", "%events%", String.join(", ", ids()));
            return false;
        }
        if (!event.isEnabled()) {
            plugin.messages().send(sender, "item-disabled");
            return false;
        }
        if (active() != null) {
            plugin.messages().send(sender, "event-already-running", "%event%", active().getName());
            return false;
        }
        if (!event.start(sender, args)) {
            return false;
        }
        active = event;
        plugin.messages().send(sender, "event-started", "%event%", event.getName());
        plugin.messages().broadcast("event-broadcast-start", "%event%", event.getName(), "%description%", event.getDescription());
        return true;
    }

    public void stop() {
        GameEvent event = active();
        if (event == null) {
            return;
        }
        event.stop();
        plugin.messages().broadcast("event-broadcast-stop", "%event%", event.getName());
        active = null;
    }

    public void shutdown() {
        for (GameEvent event : events.values()) {
            if (event.isRunning()) {
                event.stop();
            }
        }
        active = null;
    }

    /** Zamienia wpis z configu (id przedmiotu albo MATERIAL:ILOSC) na przedmiot. */
    public ItemStack parseReward(String raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        String[] parts = raw.split(":");
        String key = parts[0].trim();
        int amount = 1;
        if (parts.length > 1) {
            try {
                amount = Math.max(1, Integer.parseInt(parts[1].trim()));
            } catch (NumberFormatException ignored) {
                amount = 1;
            }
        }
        CustomItem item = plugin.items().byId(key);
        if (item != null) {
            return item.build(amount);
        }
        Material material = Material.matchMaterial(key.toUpperCase(Locale.ROOT));
        return material == null ? null : new ItemStack(material, amount);
    }

    public ItemStack randomReward(List<String> rewards) {
        List<ItemStack> parsed = new ArrayList<>();
        for (String raw : rewards) {
            ItemStack stack = parseReward(raw);
            if (stack != null) {
                parsed.add(stack);
            }
        }
        if (parsed.isEmpty()) {
            return null;
        }
        return parsed.get(ThreadLocalRandom.current().nextInt(parsed.size()));
    }
}
