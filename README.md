# AnarchiaItemy

Plugin **Paper 1.21.4+** dodający kompletny zestaw *eventówek* (przedmiotów eventowych)
w stylu serwera **Anarchia.GG** — 62 przedmioty, system regionów, eventy serwerowe,
menu GUI oraz w pełni konfigurowalne `custom-model-data`.

> ⚠️ **Tekstury.** Plugin nie zawiera tekstur. Modele pochodzą z oryginalnego
> teksturepacka Anarchia.GG — pobierzesz go z ich Discorda:
> <https://discord.gg/yfnDmhhv5Q>. Każdy przedmiot ma własne `custom-model-data`,
> które możesz dowolnie zmienić w `items.yml`, aby dopasować je do swojej paczki.

---

## Spis treści

- [Wymagania](#wymagania)
- [Budowanie](#budowanie)
- [Instalacja](#instalacja)
- [Komendy](#komendy)
- [Uprawnienia](#uprawnienia)
- [Lista przedmiotów](#lista-przedmiotow)
- [Zbroje](#zbroje)
- [System regionów](#system-regionow)
- [Eventy serwerowe](#eventy-serwerowe)
- [Konfiguracja](#konfiguracja)
- [Struktura projektu](#struktura-projektu)

---

## Wymagania

| | |
|---|---|
| Serwer | **Paper / Purpur 1.21.4+** (czysty Spigot nie wystarczy — używamy API Adventure) |
| Java | **21+** |
| Build | Maven 3.9+ |

## Budowanie

```bash
mvn clean package
```

Gotowy plik znajdziesz w `target/anarchiaitemy-1.0.0.jar`.

> W środowisku, w którym powstał ten kod, nie było dostępu do JDK ani repozytoriów
> Maven, więc **jar nie został zbudowany lokalnie** — uruchom `mvn clean package`
> u siebie, aby wygenerować plik wynikowy.

## Instalacja

1. Wrzuć `anarchiaitemy-1.0.0.jar` do katalogu `plugins/`.
2. Uruchom serwer — wygenerują się pliki:
   - `plugins/AnarchiaItemy/config.yml` — ustawienia globalne, regiony, eventy, GUI,
   - `plugins/AnarchiaItemy/items.yml` — **każdy przedmiot**: materiał, nazwa, lore,
     `custom-model-data`, zaklęcia, flagi i ustawienia mechaniki,
   - `plugins/AnarchiaItemy/messages.yml` — wszystkie wiadomości,
   - `plugins/AnarchiaItemy/regions.yml` — zapisane regiony.
3. Zainstaluj teksturepack Anarchia.GG (patrz wyżej).
4. `/anarchiaitemy reload` po każdej zmianie plików.

## Komendy

Główna komenda: `/anarchiaitemy` (aliasy: `/anarchiaiitemy`, `/aitemy`, `/anarchia`,
`/eventowki`, `/ai`).

| Komenda | Opis |
|---|---|
| `/anarchiaitemy giveitem <id> [gracz] [ilość]` | Daje eventówkę sobie lub innemu graczowi (działa też z grupami, np. `zbroja`) |
| `/anarchiaitemy reload` | Przeładowuje `config.yml`, `items.yml`, `messages.yml`, `regions.yml` |
| `/anarchiaitemy panel` | Otwiera główne menu pluginu |
| `/anarchiaitemy menupreview [strona]` | Menu podglądu wszystkich przedmiotów (klik = otrzymujesz przedmiot) |
| `/anarchiaitemy setkills <ilość> [gracz]` | Ustawia licznik zabójstw na Excaliburze |
| `/anarchiaitemy region <...>` | Zarządzanie regionami (patrz niżej) |
| `/anarchiaitemy event <start\|stop\|list\|gui> [nazwa]` | Eventy serwerowe |
| `/anarchiaitemy enchant [menu\|remove\|<zaklęcie> <poziom>]` | Zaczarowanie / odczarowanie przedmiotu w ręce |
| `/anarchiaitemy help` | Lista komend |

Wszystkie komendy mają pełne **tab-completion**.

## Uprawnienia

| Permisja | Domyślnie | Opis |
|---|---|---|
| `iAnarchiaitemy.admin` | op | Dostęp do wszystkich komend (zawiera pozostałe permisje) |
| `iAnarchiaitemy.books` | op | Pozwala używać zaczarowanych książek pluginu |
| `Anarchiaitemy.region` | op | Zarządzanie regionami |
| `iAnarchiaitemy.bypass.region` | op | Ignorowanie flag regionów |
| `iAnarchiaitemy.use` | wszyscy | Używanie eventówek (wymagane tylko gdy `settings.require-use-permission: true`) |

## Lista przedmiotów

| `id` | Nazwa | Materiał | Kategoria | CMD | Działanie |
|---|---|---|---|---|---|
| `anarchicznykilof` | Anarchiczny Kilof | `NETHERITE_PICKAXE` | Narzędzia | 10001 | Netherytowy kilof z EFFICIENCY 10, FORTUNE 5, UNBREAKING 5 |
| `anarchicznyluk` | Anarchiczny Łuk | `BOW` | Bronie | 10002 | FLAME, POWER 6, PUNCH 3, UNBREAKING 5 |
| `anarchicznymiecz` | Anarchiczny Miecz | `NETHERITE_SWORD` | Bronie | 10003 | Netherytowy miecz z SHARPNESS 6 oraz FIRE ASPECT 2 |
| `anarchicznytrojzab` | Anarchiczny Trójząb | `TRIDENT` | Bronie | 10004 | RIPTIDE 4, CHANNELING, IMPALING 5, LOYALTY 4, UNBREAKING 4 + 10s cooldown |
| `antycobweb` | Anty Cobweb | `SHEARS` | Narzędzia | 10005 | Usuwa pajęczyny w promieniu 3 kratek od gracza |
| `arcusmagnus` | Arcus Magnus | `BOW` | Bronie | 10006 | Łuk pozwalający na niszczycielskie combo |
| `bombardamaxima` | Bombarda Maxima | `BOW` | Bronie | 10007 | Po wystrzeleniu wybucha i niszczy każdy blok oprócz skały macierzystej |
| `boskitopor` | Boski Topór | `NETHERITE_AXE` | Bronie | 10008 | Odpycha graczy wokół i daje nieśmiertelność na 3 sekundy |
| `cieplemleko` | Ciepłe Mleko | `MILK_BUCKET` | Wsparcie | 10009 | PPM usuwa wszystkie negatywne efekty |
| `dynamit` | Dynamit | `TNT` | Narzędzia | 10010 | PPM w skałę macierzystą niszczy ją |
| `excalibur` | Excalibur | `GOLDEN_SWORD` | Bronie | 10011 | Z każdym zabójstwem zyskuje wyższy poziom Sharpness |
| `koronaanarchi` | Korona Anarchii | `GOLDEN_HELMET` | Zbroje | 10012 | Założona na głowę daje Speed 2, Fire Res 1, Siła 2, Odporność 3, Szczęście 1 |
| `kosa` | Kosa | `NETHERITE_HOE` | Bronie | 10013 | Po uderzeniu przeciwnika nadaje mu efekt ślepoty |
| `kostkarubika` | Kostka Rubika | `SLIME_BLOCK` | Sabotaż | 10014 | Uderz gracza, aby przemieszać jego ekwipunek |
| `krewwampira` | Krew Wampira | `POTION` | Wsparcie | 10015 | PPM przywraca pełne zdrowie |
| `kupaanarchi` | Kupa Anarchii | `BROWN_DYE` | Efekty | 10016 | Trzymając w ręce dostajesz Siłę II na zawsze |
| `lewejajko` | Lewe Jajko | `EGG` | Sabotaż | 10017 | Wyrzuca trafionego przeciwnika w powietrze |
| `lizak` | Lizak | `SUGAR` | Efekty | 10018 | Trzymając w ręce dostajesz Siłę I na zawsze |
| `lopatagrincha` | Łopata Grincha | `IRON_SHOVEL` | Bronie | 10019 | Obraca głowę przeciwnika i zamraża go na 3 sekundy |
| `lukkupidyna` | Łuk Kupidyna | `BOW` | Bronie | 10020 | Szansa na oślepienie trafionego gracza |
| `mace` | Mace | `MACE` | Bronie | 10021 | Pełne obrażenia tylko w Endzie, poza nim prawie nie rani |
| `magicznycukierek` | Magiczny Cukierek | `HONEYCOMB` | Wsparcie | 10022 | PPM daje SPEED V na 5 sekund |
| `marchewkowakusza` | Marchewkowa Kusza | `CROSSBOW` | Bronie | 10023 | Przyciąga trafionego przeciwnika do siebie |
| `marchewkowymiecz` | Marchewkowy Miecz | `CARROT_ON_A_STICK` | Bronie | 10024 | Zamraża przeciwnika na sekundę po uderzeniu |
| `nieskonczonafajerwerka` | Nieskończona Fajerwerka | `FIREWORK_ROCKET` | Wsparcie | 10025 | Pozwala na nieskończone latanie na elytrze |
| `parawan` | Parawan | `BLUE_BANNER` | Wsparcie | 10026 | Po użyciu odrzuca wszystkich przeciwników w okolicy |
| `piekielnatarcza` | Piekielna Tarcza | `SHIELD` | Zbroje | 10027 | 25% szansy na odbicie ataku wroga |
| `piernik` | Piernik | `COOKIE` | Wsparcie | 10028 | PPM daje HASTE 10 na 10 sekund |
| `plecakdrakuli` | Plecak Drakuli | `SHULKER_BOX` | Wsparcie | 10029 | Przenośny ekwipunek otwierany nawet podczas walki |
| `rozakupidyna` | Róża Kupidyna | `POPPY` | Efekty | 10030 | Trzymając w ręce: Odporność I oraz Regeneracja I |
| `rozdzkailuzjonisty` | Różdżka Iluzjonisty | `BLAZE_ROD` | Bronie | 10031 | LPM: szczęki Evokera, PPM: znikasz i tworzysz klona |
| `rozga` | Rózga | `STICK` | Bronie | 10032 | Patyk z zaklęciem KNOCKBACK 4 |
| `sakiewkadropu` | Sakiewka Dropu | `BUNDLE` | Wsparcie | 10033 | Przedmioty zabitego gracza trafiają do twojego ekwipunku |
| `siekieragrincha` | Siekiera Grincha | `DIAMOND_AXE` | Bronie | 10034 | Piorun w przeciwnika zabierający 30% jego życia |
| `smoczymiecz` | Smoczy Miecz | `DIAMOND_SWORD` | Bronie | 10035 | PPM wystrzeliwuje perłę kresu |
| `sniezka` | Śnieżka | `SNOWBALL` | Sabotaż | 10036 | Po trafieniu zamieniasz się miejscem z przeciwnikiem |
| `sniezynka` | Śnieżynka | `SNOWBALL` | Sabotaż | 10037 | Rzuć w gracza, aby go zamrozić na krótką chwilę |
| `splesnialakanapka` | Spleśniała Kanapka | `BREAD` | Bronie | 10038 | Po uderzeniu zaraża przeciwnika chorobą |
| `totemulaskawienia` | Totem Ułaskawienia | `TOTEM_OF_UNDYING` | Wsparcie | 10039 | Po śmierci nie tracisz przedmiotów |
| `trojzabposejdona` | Trójząb Posejdona | `TRIDENT` | Bronie | 10040 | W miejscu uderzenia przyzywa piorun i odpycha wrogów |
| `turbodomek` | Turbo-Domek | `OAK_DOOR` | Sabotaż | 10041 | Po wystrzeleniu buduje prostą pułapkę/domek w miejscu eksplozji |
| `turbotrap` | Turbo-Trap | `IRON_BARS` | Sabotaż | 10042 | Po wystrzeleniu zamyka pobliskich graczy w klatce |
| `wampirzejablko` | Wampirze Jabłko | `GOLDEN_APPLE` | Wsparcie | 10043 | Po zjedzeniu daje Siłę II na krótki czas |
| `watacukrowa` | Wata Cukrowa | `PINK_DYE` | Wsparcie | 10044 | Naprawia jeden element zbroi do pełnej trwałości |
| `wedkaguardiania` | Wędka Guardiania | `FISHING_ROD` | Wędki | 10045 | Przyzywa Strażnika nakładającego zmęczenie kopania |
| `wedkanielota` | Wędka Nielota | `FISHING_ROD` | Wędki | 10046 | Złapany gracz nie może lecieć elytrą |
| `wedkasurferka` | Wędka Surferka | `FISHING_ROD` | Wędki | 10047 | Przyciąga cię do bloków i graczy |
| `wyrzutniahydroklatki` | Wyrzutnia Hydro Klatki | `DISPENSER` | Sabotaż | 10048 | Wystrzeliwuje pocisk tworzący wodną klatkę |
| `wzmocnionaelytra` | Wzmocniona Elytra | `ELYTRA` | Zbroje | 10049 | Po naładowaniu uwalnia falę uderzeniową przy zderzeniu z ziemią |
| `zaczarowanie` | Zaczarowanie Przedmiotu | `ENCHANTING_TABLE` | Wsparcie | 10050 | Otwiera menu do zaczarowywania |
| `zajeczymiecz` | Zajęczy Miecz | `GOLDEN_SWORD` | Bronie | 10051 | Po uderzeniu przeciwnik nie może skakać przez 4 sekundy |
| `zatrutyolowek` | Zatruty Ołówek | `STICK` | Bronie | 10052 | Po uderzeniu przeciwnik dostaje efekt zatrucia |
| `zlamaneserce` | Złamane Serce | `RED_DYE` | Bronie | 10053 | Po uderzeniu: spowolnienie + powolne opadanie |
| `zmutowanycreeper` | Jajko Zmutowanego Creepera | `CREEPER_SPAWN_EGG` | Sabotaż | 10054 | Przyzywa creepera zabierającego 50% życia |

### Zbroje

Zbroje są **grupami** — jedna komenda daje cały komplet:

| Grupa | Elementy | CMD |
|---|---|---|
| `anarchicznazbroja` (alias `zbroja`) | hełm / napierśnik / spodnie / buty (netherite) | 10060–10063 |
| `anarchicznazbroja2` (alias `zbroja2`) | mocniejszy wariant II | 10064–10067 |

```
/anarchiaitemy giveitem zbroja
/anarchiaitemy giveitem anarchicznazbroja2 Steve
```

Pełen komplet aktywuje **bonus setowy** (dodatkowe efekty, konfigurowalne w `items.yml`).

Aliasy dostępne również dla pojedynczych przedmiotów: `korona`, `rozdzka`, `trojzab`,
`koronaanarchii`, `kupaanarchii`, `różdżkailuzjonisty`.

## System regionów

Wbudowany, lekki system regionów cuboidalnych — bez WorldGuarda.

```
/anarchiaitemy region wand              # różdżka (LPM = poz. 1, PPM = poz. 2)
/anarchiaitemy region pos1|pos2         # zaznaczenie z aktualnej pozycji
/anarchiaitemy region create <nazwa>
/anarchiaitemy region delete <nazwa>
/anarchiaitemy region list | info [nazwa] | tp <nazwa> | gui
/anarchiaitemy region flag <nazwa> <flaga> <true|false>
/anarchiaitemy region priority <nazwa> <liczba>
```

Flagi: `pvp`, `build`, `interact`, `items`, `explosions`, `enter`.
Flaga `items` blokuje używanie eventówek wewnątrz regionu — idealne na spawn.
Regiony mogą się nakładać; wygrywa ten o wyższym `priority`.

## Eventy serwerowe

```
/anarchiaitemy event start <nazwa> [argumenty]
/anarchiaitemy event stop
/anarchiaitemy event list
```

| Event | Opis |
|---|---|
| `dropparty` | Wyrzuca skonfigurowane nagrody (eventówki lub `MATERIAL:ILOŚĆ`) na spawnie |
| `losowanie` | Losuje przedmiot dla losowego gracza online |
| `koth` | King of the Hill na wybranym regionie: `event start koth <region>` |
| `tntrain` | Deszcz TNT w promieniu wokół startującego |

Każdy event konfigurujesz w `config.yml` → `events.<id>`.

## Konfiguracja

### `config.yml` (skrót)

| Klucz | Opis |
|---|---|
| `settings.enabled-worlds` | Lista światów, w których działają eventówki (pusta = wszystkie) |
| `settings.require-use-permission` | Czy wymagać `iAnarchiaitemy.use` |
| `settings.cooldown-actionbar` | Pokazywanie pozostałego cooldownu na action barze |
| `settings.effect-task-period` | Co ile ticków odświeżane są efekty trzymanych/założonych przedmiotów |
| `default-flags` | Flagi doklejane do każdego przedmiotu |
| `regions.*` | Włączenie systemu regionów, materiał różdżki, domyślne flagi |
| `events.*` | Ustawienia poszczególnych eventów |
| `enchanting.*` | Maksymalne poziomy zaklęć w menu `/anarchiaitemy enchant` |
| `gui.*` | Tytuły menu i materiał wypełniacza |

### Obsługiwane flagi przedmiotów

`HIDE_ENCHANTS`, `HIDE_ATTRIBUTES`, `HIDE_UNBREAKABLE`, `HIDE_DESTROYS`,
`HIDE_PLACED_ON`, `HIDE_POTION_EFFECTS` (dodatkowo `HIDE_DYE`, `HIDE_ARMOR_TRIM`).

Ustawiasz je globalnie w `config.yml` → `default-flags` albo per przedmiot
w `items.yml` → `items.<id>.flags`.

### `items.yml` — przykład

```yaml
items:
  excalibur:
    enabled: true
    category: Bronie
    material: GOLDEN_SWORD
    name: '&e&lExcalibur'
    lore:
      - '&7Z każdym zabójstwem staje się silniejszy.'
    custom-model-data: 10011      # <- dopasuj do swojego teksturepacka
    unbreakable: true
    glow: true
    flags:
      - HIDE_ENCHANTS
    enchantments:
      sharpness: 1
    settings:
      kills-per-level: 5
      max-level: 10
```

Wyłączenie przedmiotu: `enabled: false`. Zmiana modelu: `custom-model-data`.
Wszystkie liczby mechaniki (obrażenia, promienie, czasy, cooldowny) siedzą w `settings`.

## Struktura projektu

```
src/main/java/gg/anarchia/itemy/
├── AnarchiaItemy.java        # główna klasa pluginu
├── Keys.java                 # klucze PersistentDataContainer
├── command/                  # /anarchiaitemy + 8 podkomend
├── config/                   # ConfigManager, Messages
├── events/                   # system eventów (dropparty, losowanie, koth, tntrain)
├── gui/                      # panel, podgląd przedmiotów, eventy, regiony, plecak
├── item/
│   ├── CustomItem.java       # baza + DSL każdej eventówki
│   ├── Handlers.java         # 15 interfejsów zdarzeń przedmiotu
│   ├── ItemManager.java      # rejestr, items.yml, task efektów
│   └── items/                # 55 klas przedmiotów
├── listener/                 # Item, Gui, Book, Mob, Region
├── region/                   # Region + RegionManager
└── util/                     # 14 klas narzędziowych
tools/                        # skrypty statycznej weryfikacji źródeł (bez JDK)
```

### Dodawanie własnej eventówki

```java
public class MojaEventowka extends CustomItem implements Handlers.RightClick {

    public MojaEventowka() {
        super("mojaeventowka", Material.STICK, "&b&lMoja Eventówka");
        category("Wsparcie");
        model(10100);
        lore("&7PPM aby błysnąć.");
        glow();
        cooldown(5.0D);
        setting("radius", 4);
    }

    @Override
    public void onRightClick(Player player, ItemStack item, PlayerInteractEvent event) {
        if (!checkCooldown(player)) {
            return;
        }
        Particles.spawn(player.getLocation(), Particles.FLAME, 40);
        Sounds.play(player, Sounds.LEVEL_UP, 1.0F, 1.2F);
    }
}
```

Dopisz `register(new MojaEventowka());` w `ItemManager#registerDefaults()` — reszta
(konfiguracja, GUI, tab-completion, `giveitem`) dzieje się automatycznie.

---

*Plugin niezależny, niepowiązany oficjalnie z Anarchia.GG. Tekstury pozostają
własnością autorów oryginalnego teksturepacka.*
