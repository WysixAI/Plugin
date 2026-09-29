package gg.anarchia.itemy.item;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.item.items.*;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Rejestr wszystkich eventowek + cykliczne efekty. */
public class ItemManager {

    private final AnarchiaItemy plugin;
    private final Map<String, CustomItem> items = new LinkedHashMap<>();
    private final Map<String, List<String>> groups = new LinkedHashMap<>();
    private final Map<String, String> aliases = new HashMap<>();
    private BukkitTask task;

    public ItemManager(AnarchiaItemy plugin) {
        this.plugin = plugin;
        registerDefaults();
    }

    private void registerDefaults() {
        // --- zbroje ---
        register(new ZbrojaItem("anarchicznazbroja_helmet", Material.NETHERITE_HELMET, "&c&lAnarchiczny Hełm", 10060, false, "anarchicznazbroja"));
        register(new ZbrojaItem("anarchicznazbroja_chestplate", Material.NETHERITE_CHESTPLATE, "&c&lAnarchiczny Napierśnik", 10061, false, "anarchicznazbroja"));
        register(new ZbrojaItem("anarchicznazbroja_leggings", Material.NETHERITE_LEGGINGS, "&c&lAnarchiczne Spodnie", 10062, false, "anarchicznazbroja"));
        register(new ZbrojaItem("anarchicznazbroja_boots", Material.NETHERITE_BOOTS, "&c&lAnarchiczne Buty", 10063, false, "anarchicznazbroja"));
        register(new ZbrojaItem("anarchicznazbroja2_helmet", Material.NETHERITE_HELMET, "&4&lAnarchiczny Hełm II", 10064, true, "anarchicznazbroja2"));
        register(new ZbrojaItem("anarchicznazbroja2_chestplate", Material.NETHERITE_CHESTPLATE, "&4&lAnarchiczny Napierśnik II", 10065, true, "anarchicznazbroja2"));
        register(new ZbrojaItem("anarchicznazbroja2_leggings", Material.NETHERITE_LEGGINGS, "&4&lAnarchiczne Spodnie II", 10066, true, "anarchicznazbroja2"));
        register(new ZbrojaItem("anarchicznazbroja2_boots", Material.NETHERITE_BOOTS, "&4&lAnarchiczne Buty II", 10067, true, "anarchicznazbroja2"));
        group("anarchicznazbroja", "anarchicznazbroja_helmet", "anarchicznazbroja_chestplate", "anarchicznazbroja_leggings", "anarchicznazbroja_boots");
        group("anarchicznazbroja2", "anarchicznazbroja2_helmet", "anarchicznazbroja2_chestplate", "anarchicznazbroja2_leggings", "anarchicznazbroja2_boots");

        // --- reszta eventowek (alfabetycznie) ---
        register(new AnarchicznyKilof());
        register(new AnarchicznyLuk());
        register(new AnarchicznyMiecz());
        register(new AnarchicznyTrojzab());
        register(new AntyCobweb());
        register(new ArcusMagnus());
        register(new BombardaMaxima());
        register(new BoskiTopor());
        register(new CiepleMleko());
        register(new Dynamit());
        register(new Excalibur());
        register(new KostkaRubika());
        register(new KoronaAnarchii());
        register(new Kosa());
        register(new KrewWampira());
        register(new KupaAnarchii());
        register(new LeweJajko());
        register(new Lizak());
        register(new LopataGrincha());
        register(new LukKupidyna());
        register(new MaceItem());
        register(new MagicznyCukierek());
        register(new MarchewkowaKusza());
        register(new MarchewkowyMiecz());
        register(new NieskonczonaFajerwerka());
        register(new Parawan());
        register(new PiekielnaTarcza());
        register(new Piernik());
        register(new PlecakDrakuli());
        register(new RozaKupidyna());
        register(new Rozga());
        register(new RozdzkaIluzjonisty());
        register(new SakiewkaDropu());
        register(new SiekieraGrincha());
        register(new SmoczyMiecz());
        register(new Sniezka());
        register(new Sniezynka());
        register(new SplesnialaKanapka());
        register(new TotemUlaskawienia());
        register(new TrojzabPosejdona());
        register(new TurboDomek());
        register(new TurboTrap());
        register(new WampirzeJablko());
        register(new WataCukrowa());
        register(new WedkaGuardiania());
        register(new WedkaNielota());
        register(new WedkaSurferka());
        register(new WyrzutniaHydroKlatki());
        register(new WzmocnionaElytra());
        register(new Zaczarowanie());
        register(new ZajeczyMiecz());
        register(new ZatrutyOlowek());
        register(new ZlamaneSerce());
        register(new ZmutowanyCreeper());

        // --- aliasy (wygodne skroty / polskie znaki) ---
        alias("różdżkailuzjonisty", "rozdzkailuzjonisty");
        alias("rozdzka", "rozdzkailuzjonisty");
        alias("zbroja", "anarchicznazbroja");
        alias("zbroja2", "anarchicznazbroja2");
        alias("korona", "koronaanarchi");
        alias("koronaanarchii", "koronaanarchi");
        alias("kupaanarchii", "kupaanarchi");
        alias("trojzab", "anarchicznytrojzab");
    }

    public void register(CustomItem item) {
        items.put(item.getId().toLowerCase(Locale.ROOT), item);
    }

    private void group(String id, String... members) {
        groups.put(id.toLowerCase(Locale.ROOT), Arrays.asList(members));
    }

    private void alias(String from, String to) {
        aliases.put(from.toLowerCase(Locale.ROOT), to.toLowerCase(Locale.ROOT));
    }

    /** Wczytuje items.yml, dopisuje brakujace wartosci domyslne. */
    public void load() {
        FileConfiguration configuration = plugin.configs().items();
        if (!configuration.isConfigurationSection("items")) {
            configuration.createSection("items");
        }
        for (CustomItem item : items.values()) {
            ConfigurationSection section = configuration.getConfigurationSection("items." + item.getId());
            if (section == null) {
                section = configuration.createSection("items." + item.getId());
            }
            item.writeDefaults(section);
            item.init(plugin, section);
        }
        plugin.configs().saveItems();
    }

    public Collection<CustomItem> all() {
        return new ArrayList<>(items.values());
    }

    public List<CustomItem> enabled() {
        List<CustomItem> list = new ArrayList<>();
        for (CustomItem item : items.values()) {
            if (item.isEnabled()) {
                list.add(item);
            }
        }
        return list;
    }

    public List<String> ids() {
        List<String> list = new ArrayList<>(items.keySet());
        list.addAll(groups.keySet());
        return list;
    }

    public String resolve(String id) {
        if (id == null) {
            return null;
        }
        String key = id.toLowerCase(Locale.ROOT);
        return aliases.getOrDefault(key, key);
    }

    public CustomItem byId(String id) {
        return items.get(resolve(id));
    }

    public boolean isGroup(String id) {
        return groups.containsKey(resolve(id));
    }

    public List<CustomItem> groupItems(String id) {
        List<CustomItem> list = new ArrayList<>();
        List<String> members = groups.get(resolve(id));
        if (members == null) {
            return list;
        }
        for (String member : members) {
            CustomItem item = items.get(member);
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    public CustomItem get(ItemStack stack) {
        String id = ItemUtil.getId(stack);
        return id == null ? null : items.get(id.toLowerCase(Locale.ROOT));
    }

    public CustomItem fromEntity(Entity entity) {
        String id = ItemUtil.getId(entity);
        return id == null ? null : items.get(id.toLowerCase(Locale.ROOT));
    }

    public ItemStack create(String id, int amount) {
        CustomItem item = byId(id);
        return item == null ? null : item.build(amount);
    }

    // ------------------------------------------------------------------
    //  Cykliczne efekty (trzymane / zalozone przedmioty)
    // ------------------------------------------------------------------

    public void startTask() {
        stopTask();
        long period = Math.max(1L, plugin.getConfig().getLong("settings.effect-task-period", 10L));
        task = Tasks.timer(this::tick, period, period);
    }

    public void stopTask() {
        Tasks.cancel(task);
        task = null;
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!plugin.isWorldEnabled(player.getWorld())) {
                continue;
            }
            if (!plugin.regions().canUseItems(player, player.getLocation())) {
                continue;
            }
            hold(player, player.getInventory().getItemInMainHand());
            hold(player, player.getInventory().getItemInOffHand());
            for (ItemStack armor : player.getInventory().getArmorContents()) {
                wear(player, armor);
            }
        }
    }

    private void hold(Player player, ItemStack stack) {
        CustomItem item = get(stack);
        if (item == null || !item.isEnabled()) {
            return;
        }
        if (item instanceof Handlers.Hold handler) {
            try {
                handler.onHold(player, stack);
            } catch (Exception exception) {
                error(item, exception);
            }
        }
    }

    private void wear(Player player, ItemStack stack) {
        CustomItem item = get(stack);
        if (item == null || !item.isEnabled()) {
            return;
        }
        if (item instanceof Handlers.Wear handler) {
            try {
                handler.onWear(player, stack);
            } catch (Exception exception) {
                error(item, exception);
            }
        }
    }

    public void cleanup(Player player) {
        for (CustomItem item : items.values()) {
            if (item instanceof Handlers.Cleanup handler) {
                try {
                    handler.cleanup(player);
                } catch (Exception exception) {
                    error(item, exception);
                }
            }
        }
    }

    public void shutdown() {
        stopTask();
        for (Player player : Bukkit.getOnlinePlayers()) {
            cleanup(player);
        }
    }

    private void error(CustomItem item, Exception exception) {
        plugin.getLogger().warning("Blad w przedmiocie " + item.getId() + ": " + exception);
    }
}
