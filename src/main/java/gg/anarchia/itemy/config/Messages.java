package gg.anarchia.itemy.config;

import gg.anarchia.itemy.AnarchiaItemy;
import gg.anarchia.itemy.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Wszystkie teksty pluginu (messages.yml). Brakujace klucze sa dopisywane automatycznie.
 */
public class Messages {

    private static final Map<String, String> DEFAULTS = new LinkedHashMap<>();

    static {
        DEFAULTS.put("prefix", "&8[&c&lAnarchiaItemy&8] &r");
        DEFAULTS.put("no-permission", "%prefix%&cNie masz uprawnien do tej komendy!");
        DEFAULTS.put("no-permission-books", "%prefix%&cNie masz uprawnien do uzywania zaczarowanych ksiazek!");
        DEFAULTS.put("player-only", "%prefix%&cTa komenda jest dostepna tylko dla graczy!");
        DEFAULTS.put("unknown-command", "%prefix%&cNieznana komenda. Uzyj &f/anarchiaitemy help&c.");
        DEFAULTS.put("reloaded", "%prefix%&aPrzeladowano konfiguracje! &7(%items% przedmiotow, %regions% regionow)");
        DEFAULTS.put("item-not-found", "%prefix%&cNie znaleziono przedmiotu o id &f%item%&c!");
        DEFAULTS.put("player-not-found", "%prefix%&cGracz &f%player% &cjest offline!");
        DEFAULTS.put("item-given", "%prefix%&aOtrzymales przedmiot &f%item%&a!");
        DEFAULTS.put("item-given-other", "%prefix%&aPrzekazano &f%item% &agraczowi &f%player%&a!");
        DEFAULTS.put("inventory-full", "%prefix%&cTwoj ekwipunek jest pelny - przedmioty wypadly na ziemie!");
        DEFAULTS.put("cooldown", "&c&lPOCZEKAJ! &7Przedmiot bedzie gotowy za &c%time%s");
        DEFAULTS.put("item-disabled", "%prefix%&cTen przedmiot jest wylaczony w konfiguracji!");
        DEFAULTS.put("no-item-in-hand", "%prefix%&cWez jakis przedmiot do reki!");
        DEFAULTS.put("world-disabled", "%prefix%&cPrzedmioty eventowe sa wylaczone w tym swiecie!");
        DEFAULTS.put("kills-set", "%prefix%&aUstawiono &f%kills% &azabojstw na Excaliburze &7(Sharpness %level%)");
        DEFAULTS.put("kills-usage", "%prefix%&cUzycie: &f/anarchiaitemy setkills <ilosc> [gracz]");
        DEFAULTS.put("kills-not-excalibur", "%prefix%&cMusisz trzymac Excalibur w rece!");
        DEFAULTS.put("excalibur-levelup", "&c&lEXCALIBUR &7» &fPoziom mocy &c%level% &7(&c%kills% &7zabojstw)");
        DEFAULTS.put("enchant-usage", "%prefix%&cUzycie: &f/anarchiaitemy enchant [menu|remove|<zaklecie> <poziom>]");
        DEFAULTS.put("enchant-applied", "%prefix%&aZaczarowano przedmiot: &f%enchant% %level%");
        DEFAULTS.put("enchant-removed", "%prefix%&aUsunieto wszystkie zaklecia z przedmiotu!");
        DEFAULTS.put("enchant-unknown", "%prefix%&cNieznane zaklecie: &f%enchant%");
        DEFAULTS.put("enchant-max", "%prefix%&cMaksymalny poziom tego zaklecia to &f%level%&c!");
        DEFAULTS.put("enchant-cost", "%prefix%&cPotrzebujesz &f%levels% &cpoziomow doswiadczenia!");
        DEFAULTS.put("book-applied", "%prefix%&aKsiazka zostala uzyta: &f%enchant% %level%");
        DEFAULTS.put("book-failed", "%prefix%&cTej ksiazki nie mozna uzyc na tym przedmiocie!");
        DEFAULTS.put("region-usage", "%prefix%&cUzycie: &f/anarchiaitemy region <create|delete|list|info|pos1|pos2|wand|flag|tp>");
        DEFAULTS.put("region-created", "%prefix%&aUtworzono region &f%region%&a!");
        DEFAULTS.put("region-deleted", "%prefix%&aUsunieto region &f%region%&a!");
        DEFAULTS.put("region-exists", "%prefix%&cRegion &f%region% &cjuz istnieje!");
        DEFAULTS.put("region-not-found", "%prefix%&cNie znaleziono regionu &f%region%&c!");
        DEFAULTS.put("region-pos", "%prefix%&aUstawiono pozycje &f%pos% &ana &f%x%, %y%, %z%");
        DEFAULTS.put("region-need-selection", "%prefix%&cNajpierw zaznacz oba rogi regionu (&f/anarchiaitemy region pos1&c i &fpos2&c)!");
        DEFAULTS.put("region-flag-set", "%prefix%&aFlaga &f%flag% &aw regionie &f%region% &austawiona na &f%value%");
        DEFAULTS.put("region-flag-unknown", "%prefix%&cNieznana flaga! Dostepne: &f%flags%");
        DEFAULTS.put("region-wand-given", "%prefix%&aOtrzymales rozdzke regionow! &7(LPM = poz. 1, PPM = poz. 2)");
        DEFAULTS.put("region-enter", "&c&l%region%&r");
        DEFAULTS.put("region-deny-build", "%prefix%&cNie mozesz budowac na tym terenie!");
        DEFAULTS.put("region-deny-pvp", "%prefix%&cPvP jest wylaczone na tym terenie!");
        DEFAULTS.put("region-deny-items", "%prefix%&cPrzedmioty eventowe sa wylaczone na tym terenie!");
        DEFAULTS.put("event-usage", "%prefix%&cUzycie: &f/anarchiaitemy event <start|stop|list> [nazwa]");
        DEFAULTS.put("event-unknown", "%prefix%&cNieznany event! Dostepne: &f%events%");
        DEFAULTS.put("event-already-running", "%prefix%&cEvent &f%event% &cjuz trwa!");
        DEFAULTS.put("event-not-running", "%prefix%&cZaden event nie jest aktywny!");
        DEFAULTS.put("event-started", "%prefix%&aWystartowano event &f%event%&a!");
        DEFAULTS.put("event-stopped", "%prefix%&aZatrzymano event &f%event%&a!");
        DEFAULTS.put("event-broadcast-start", "&8&m--------------------------------\n&c&lEVENT &8» &f%event%\n&7%description%\n&8&m--------------------------------");
        DEFAULTS.put("event-broadcast-stop", "&8» &c&lEVENT &8» &fEvent &c%event% &fzostal zakonczony!");
        DEFAULTS.put("event-dropparty-start", "&8» &c&lDROP PARTY &8» &fZa chwile z nieba spadna eventowki!");
        DEFAULTS.put("event-koth-capture", "&8» &c&lKOTH &8» &fGracz &c%player% &fprzejmuje punkt! &7(%time%s)");
        DEFAULTS.put("event-koth-win", "&8» &c&lKOTH &8» &fGracz &c%player% &fwygrywa event!");
        DEFAULTS.put("event-koth-need-region", "%prefix%&cNajpierw utworz region i podaj jego nazwe: &f/anarchiaitemy event start koth <region>");
        DEFAULTS.put("event-losowanie", "&8» &c&lLOSOWANIE &8» &fOtrzymales &c%item%&f!");
        DEFAULTS.put("event-tntrain", "&8» &c&lTNT RAIN &8» &fUwaga! Z nieba leci TNT!");
        DEFAULTS.put("backpack-blocked", "%prefix%&cNie mozesz tego zrobic podczas otwartego plecaka!");
        DEFAULTS.put("totem-saved", "&5&lTOTEM UlASKAWIENIA &7» &fTwoje przedmioty zostaly uratowane!");
        DEFAULTS.put("sakiewka-collected", "&6&lSAKIEWKA &7» &fZebrano przedmioty gracza &e%player%&f!");
        DEFAULTS.put("gui-back", "&c« Powrot");
        DEFAULTS.put("gui-next", "&aNastepna strona »");
        DEFAULTS.put("gui-prev", "&a« Poprzednia strona");
        DEFAULTS.put("gui-close", "&cZamknij");
    }

    private final AnarchiaItemy plugin;
    private final ConfigManager configs;

    public Messages(AnarchiaItemy plugin, ConfigManager configs) {
        this.plugin = plugin;
        this.configs = configs;
        load();
    }

    public final void load() {
        FileConfiguration configuration = configs.messages();
        boolean changed = false;
        for (Map.Entry<String, String> entry : DEFAULTS.entrySet()) {
            if (!configuration.isSet(entry.getKey())) {
                configuration.set(entry.getKey(), entry.getValue());
                changed = true;
            }
        }
        if (changed) {
            configs.saveMessages();
        }
    }

    public String prefix() {
        return configs.messages().getString("prefix", DEFAULTS.get("prefix"));
    }

    public String raw(String key, String... placeholders) {
        String message = configs.messages().getString(key, DEFAULTS.getOrDefault(key, "&cBrak wiadomosci: " + key));
        message = message.replace("%prefix%", prefix());
        return Text.replace(message, placeholders);
    }

    public void send(CommandSender sender, String key, String... placeholders) {
        if (sender == null) {
            return;
        }
        String message = raw(key, placeholders);
        if (message.isEmpty()) {
            return;
        }
        for (String line : message.split("\n")) {
            Text.send(sender, line);
        }
    }

    public void actionBar(Player player, String key, String... placeholders) {
        Text.actionBar(player, raw(key, placeholders));
    }

    public void broadcast(String key, String... placeholders) {
        String message = raw(key, placeholders);
        for (String line : message.split("\n")) {
            Bukkit.broadcast(Text.parse(line));
        }
    }

    public AnarchiaItemy plugin() {
        return plugin;
    }
}
