package gg.anarchia.itemy.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Tymczasowe bloki (pulapki, klatki wodne) - po czasie wracaja do stanu sprzed zmiany.
 */
public final class BlockRestore {

    private final Set<Snapshot> pending = new LinkedHashSet<>();

    public void set(Block block, Material material, long ticks) {
        if (block == null || material == null) {
            return;
        }
        Snapshot snapshot = new Snapshot(block.getState());
        pending.add(snapshot);
        block.setType(material, false);
        Tasks.later(() -> restore(snapshot), ticks);
    }

    private void restore(Snapshot snapshot) {
        if (pending.remove(snapshot)) {
            snapshot.apply();
        }
    }

    public void restoreAll() {
        for (Snapshot snapshot : new LinkedHashSet<>(pending)) {
            snapshot.apply();
        }
        pending.clear();
    }

    public int size() {
        return pending.size();
    }

    private static final class Snapshot {
        private final BlockState state;

        private Snapshot(BlockState state) {
            this.state = state;
        }

        private void apply() {
            try {
                state.update(true, false);
            } catch (Exception ignored) {
                // swiat moze byc juz wyladowany
            }
        }
    }
}
