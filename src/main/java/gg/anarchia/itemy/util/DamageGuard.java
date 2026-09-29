package gg.anarchia.itemy.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Zabezpieczenie przed nieskonczona petla obrazen (przedmiot zadaje obrazenia
 * wewnatrz obslugi zdarzenia obrazen).
 */
public final class DamageGuard {

    private static final Set<UUID> ACTIVE = new HashSet<>();

    private DamageGuard() {
    }

    public static void damage(LivingEntity victim, double amount, Entity source) {
        if (victim == null || victim.isDead() || amount <= 0) {
            return;
        }
        if (!ACTIVE.add(victim.getUniqueId())) {
            return;
        }
        try {
            victim.setNoDamageTicks(0);
            if (source == null) {
                victim.damage(amount);
            } else {
                victim.damage(amount, source);
            }
        } catch (Exception ignored) {
            // ignorujemy - np. gracz wyszedl z serwera
        } finally {
            ACTIVE.remove(victim.getUniqueId());
        }
    }

    public static boolean isBusy(Entity entity) {
        return entity != null && ACTIVE.contains(entity.getUniqueId());
    }
}
