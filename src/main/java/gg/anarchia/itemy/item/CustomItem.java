package gg.anarchia.itemy.item;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.Keys;
import gg.anarchia.itemy.util.Cooldowns;
import gg.anarchia.itemy.util.Enchants;
import gg.anarchia.itemy.util.Flags;
import gg.anarchia.itemy.util.ItemUtil;
import gg.anarchia.itemy.util.Sounds;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Bazowa klasa kazdej eventowki. Wszystkie wartosci (material, nazwa, lore,
 * custom-model-data, zaklecia, flagi, ustawienia) sa konfigurowalne w items.yml.
 */
public abstract class CustomItem {

    private final String id;
    private final Material defaultMaterial;
    private final String defaultName;
    private final List<String> defaultLore = new ArrayList<>();
    private final Map<String, Integer> defaultEnchantments = new LinkedHashMap<>();
    private final List<String> defaultFlags = new ArrayList<>();
    private final Map<String, Object> defaultSettings = new LinkedHashMap<>();
    private int defaultModelData;
    private boolean defaultUnbreakable;
    private boolean defaultGlow;
    private String category = "Eventowki";

    protected AnarchiaItemy plugin;
    private ConfigurationSection section;

    protected CustomItem(String id, Material material, String name) {
        this.id = id.toLowerCase(Locale.ROOT);
        this.defaultMaterial = material;
        this.defaultName = name;
    }

    // ------------------------------------------------------------------
    //  DSL uzywane w konstruktorach przedmiotow
    // ------------------------------------------------------------------

    protected CustomItem lore(String... lines) {
        Collections.addAll(defaultLore, lines);
        return this;
    }

    protected CustomItem enchant(String enchantment, int level) {
        defaultEnchantments.put(enchantment.toLowerCase(Locale.ROOT), level);
        return this;
    }

    protected CustomItem flags(String... names) {
        Collections.addAll(defaultFlags, names);
        return this;
    }

    protected CustomItem model(int data) {
        this.defaultModelData = data;
        return this;
    }

    protected CustomItem unbreakable() {
        this.defaultUnbreakable = true;
        return this;
    }

    protected CustomItem glow() {
        this.defaultGlow = true;
        return this;
    }

    protected CustomItem category(String name) {
        this.category = name;
        return this;
    }

    protected CustomItem setting(String path, Object value) {
        defaultSettings.put(path, value);
        return this;
    }

    /** Skrot: rejestruje ustawienie "cooldown" (w sekundach). */
    protected CustomItem cooldown(double seconds) {
        return setting("cooldown", seconds);
    }

    // ------------------------------------------------------------------
    //  Konfiguracja
    // ------------------------------------------------------------------

    public void init(AnarchiaItemy instance, ConfigurationSection configuration) {
        this.plugin = instance;
        this.section = configuration;
        onInit();
    }

    protected void onInit() {
    }

    public void writeDefaults(ConfigurationSection target) {
        setIfAbsent(target, "enabled", true);
        setIfAbsent(target, "category", category);
        setIfAbsent(target, "material", defaultMaterial.name());
        setIfAbsent(target, "name", defaultName);
        setIfAbsent(target, "lore", new ArrayList<>(defaultLore));
        setIfAbsent(target, "custom-model-data", defaultModelData);
        setIfAbsent(target, "unbreakable", defaultUnbreakable);
        setIfAbsent(target, "glow", defaultGlow);
        setIfAbsent(target, "flags", new ArrayList<>(defaultFlags));
        for (Map.Entry<String, Integer> entry : defaultEnchantments.entrySet()) {
            setIfAbsent(target, "enchantments." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Object> entry : defaultSettings.entrySet()) {
            setIfAbsent(target, "settings." + entry.getKey(), entry.getValue());
        }
    }

    private void setIfAbsent(ConfigurationSection target, String path, Object value) {
        if (!target.isSet(path)) {
            target.set(path, value);
        }
    }

    public ConfigurationSection section() {
        return section;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return section == null ? category : section.getString("category", category);
    }

    public boolean isEnabled() {
        return section == null || section.getBoolean("enabled", true);
    }

    public Material getMaterial() {
        Material material = Material.matchMaterial(string("material", defaultMaterial.name()));
        return material == null ? defaultMaterial : material;
    }

    public String getDisplayName() {
        return string("name", defaultName);
    }

    public String getPlainName() {
        return Text.strip(getDisplayName());
    }

    public List<String> getLore() {
        if (section != null && section.isList("lore")) {
            return section.getStringList("lore");
        }
        return new ArrayList<>(defaultLore);
    }

    protected String string(String path, String def) {
        return section == null ? def : section.getString(path, def);
    }

    protected int integer(String path, int def) {
        return section == null ? def : section.getInt(path, def);
    }

    protected boolean bool(String path, boolean def) {
        return section == null ? def : section.getBoolean(path, def);
    }

    public int settingInt(String path) {
        Object def = defaultSettings.get(path);
        int fallback = def instanceof Number number ? number.intValue() : 0;
        return section == null ? fallback : section.getInt("settings." + path, fallback);
    }

    public double settingDouble(String path) {
        Object def = defaultSettings.get(path);
        double fallback = def instanceof Number number ? number.doubleValue() : 0.0D;
        return section == null ? fallback : section.getDouble("settings." + path, fallback);
    }

    public boolean settingBool(String path) {
        Object def = defaultSettings.get(path);
        boolean fallback = def instanceof Boolean value && value;
        return section == null ? fallback : section.getBoolean("settings." + path, fallback);
    }

    public String settingString(String path) {
        Object def = defaultSettings.get(path);
        String fallback = def == null ? "" : String.valueOf(def);
        return section == null ? fallback : section.getString("settings." + path, fallback);
    }

    @SuppressWarnings("unchecked")
    public List<String> settingList(String path) {
        if (section != null && section.isList("settings." + path)) {
            return section.getStringList("settings." + path);
        }
        Object def = defaultSettings.get(path);
        if (def instanceof List<?> list) {
            return (List<String>) list;
        }
        return new ArrayList<>();
    }

    // ------------------------------------------------------------------
    //  Tworzenie przedmiotu
    // ------------------------------------------------------------------

    public ItemStack build() {
        return build(1);
    }

    public ItemStack build(int amount) {
        ItemStack stack = new ItemStack(getMaterial(), Math.max(1, amount));
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }
        meta.displayName(Text.parse(getDisplayName()));
        List<String> lore = getLore();
        if (!lore.isEmpty()) {
            meta.lore(Text.parse(lore));
        }
        int modelData = integer("custom-model-data", defaultModelData);
        if (modelData > 0) {
            meta.setCustomModelData(modelData);
        }
        if (bool("unbreakable", defaultUnbreakable)) {
            meta.setUnbreakable(true);
        }
        for (Map.Entry<String, Integer> entry : enchantments().entrySet()) {
            Enchantment enchantment = Enchants.byName(entry.getKey());
            if (enchantment != null && entry.getValue() != null && entry.getValue() > 0) {
                meta.addEnchant(enchantment, entry.getValue(), true);
            }
        }
        boolean glow = bool("glow", defaultGlow);
        if (glow && !meta.hasEnchants()) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        }
        List<String> flagNames = new ArrayList<>();
        if (plugin != null) {
            flagNames.addAll(plugin.getConfig().getStringList("default-flags"));
        }
        if (section != null && section.isList("flags")) {
            flagNames.addAll(section.getStringList("flags"));
        } else {
            flagNames.addAll(defaultFlags);
        }
        if (glow) {
            flagNames.add("HIDE_ENCHANTS");
        }
        for (ItemFlag flag : Flags.parse(flagNames)) {
            meta.addItemFlags(flag);
        }
        meta.getPersistentDataContainer().set(Keys.ITEM_ID, PersistentDataType.STRING, id);
        decorate(meta);
        stack.setItemMeta(meta);
        finish(stack);
        return stack;
    }

    public Map<String, Integer> enchantments() {
        Map<String, Integer> map = new LinkedHashMap<>(defaultEnchantments);
        if (section != null && section.isConfigurationSection("enchantments")) {
            ConfigurationSection enchantSection = section.getConfigurationSection("enchantments");
            if (enchantSection != null) {
                for (String key : enchantSection.getKeys(false)) {
                    map.put(key.toLowerCase(Locale.ROOT), enchantSection.getInt(key));
                }
            }
        }
        return map;
    }

    /** Dodatkowe zmiany meta (np. glowa gracza, kolor skory). */
    protected void decorate(ItemMeta meta) {
    }

    /** Dodatkowe zmiany na gotowym przedmiocie. */
    protected void finish(ItemStack stack) {
    }

    public boolean matches(ItemStack stack) {
        return ItemUtil.hasId(stack, id);
    }

    // ------------------------------------------------------------------
    //  Pomocnicze
    // ------------------------------------------------------------------

    /** Sprawdza i ustawia cooldown z ustawienia "cooldown". */
    protected boolean checkCooldown(Player player) {
        return checkCooldown(player, settingDouble("cooldown"));
    }

    protected boolean checkCooldown(Player player, double seconds) {
        if (seconds <= 0) {
            return true;
        }
        if (Cooldowns.isReady(player, id)) {
            Cooldowns.set(player, id, seconds);
            return true;
        }
        if (plugin != null && plugin.getConfig().getBoolean("settings.cooldown-actionbar", true)) {
            plugin.messages().actionBar(player, "cooldown", "%time%", Cooldowns.format(Cooldowns.remaining(player, id)));
        }
        Sounds.play(player, Sounds.GLASS, 0.6F, 1.6F);
        return false;
    }

    protected void message(Player player, String text) {
        Text.send(player, text);
    }

    /** Czy gracz moze zmieniac bloki w danym miejscu (system regionow). */
    protected boolean canBuild(Player player, org.bukkit.Location location) {
        return plugin == null || plugin.regions().canBuild(player, location);
    }

    /** Czy w danym miejscu mozna uzywac eventowek. */
    protected boolean canUseItems(Player player, org.bukkit.Location location) {
        return plugin == null || plugin.regions().canUseItems(player, location);
    }

    @Override
    public String toString() {
        return "CustomItem{" + id + "}";
    }
}
