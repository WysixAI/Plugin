package gg.anarchia.itemy.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Prosty helper do kolorowania tekstu (&amp;-kody + hex) i wysylania wiadomosci.
 */
public final class Text {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private Text() {
    }

    public static Component parse(String input) {
        if (input == null) {
            return Component.empty();
        }
        return LEGACY.deserialize(input).decoration(TextDecoration.ITALIC, false);
    }

    public static List<Component> parse(List<String> input) {
        List<Component> out = new ArrayList<>();
        if (input == null) {
            return out;
        }
        for (String line : input) {
            out.add(parse(line));
        }
        return out;
    }

    public static String plain(Component component) {
        if (component == null) {
            return "";
        }
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static String strip(String legacy) {
        return plain(parse(legacy));
    }

    public static void send(CommandSender sender, String message) {
        if (sender == null || message == null || message.isEmpty()) {
            return;
        }
        sender.sendMessage(parse(message));
    }

    public static void actionBar(Player player, String message) {
        if (player == null || message == null) {
            return;
        }
        player.sendActionBar(parse(message));
    }

    public static void title(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null) {
            return;
        }
        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadeIn * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L));
        player.showTitle(Title.title(parse(title == null ? "" : title), parse(subtitle == null ? "" : subtitle), times));
    }

    public static String replace(String input, String... placeholders) {
        if (input == null) {
            return "";
        }
        String out = input;
        for (int i = 0; i + 1 < placeholders.length; i += 2) {
            out = out.replace(placeholders[i], placeholders[i + 1]);
        }
        return out;
    }
}
