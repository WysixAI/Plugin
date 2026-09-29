package gg.anarchia.itemy.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * Zapisywanie zawartosci ekwipunku (np. Plecak Drakuli) do Base64.
 */
public final class Serializer {

    private Serializer() {
    }

    public static String toBase64(ItemStack[] items) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             BukkitObjectOutputStream stream = new BukkitObjectOutputStream(out)) {
            stream.writeInt(items.length);
            for (ItemStack item : items) {
                stream.writeObject(item);
            }
            stream.flush();
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception exception) {
            return "";
        }
    }

    public static ItemStack[] fromBase64(String data, int fallbackSize) {
        if (data == null || data.isEmpty()) {
            return new ItemStack[fallbackSize];
        }
        try (ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             BukkitObjectInputStream stream = new BukkitObjectInputStream(in)) {
            int size = stream.readInt();
            ItemStack[] items = new ItemStack[Math.max(size, fallbackSize)];
            for (int i = 0; i < size; i++) {
                Object object = stream.readObject();
                items[i] = object instanceof ItemStack itemStack ? itemStack : null;
            }
            return items;
        } catch (Exception exception) {
            return new ItemStack[fallbackSize];
        }
    }
}
