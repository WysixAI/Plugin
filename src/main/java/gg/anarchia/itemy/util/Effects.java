package gg.anarchia.itemy.util;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Wszystkie efekty uzywane przez plugin w jednym miejscu - latwo podmienic
 * gdyby nazwy w API kiedykolwiek sie zmienily.
 */
public final class Effects {

    public static final PotionEffectType SPEED = PotionEffectType.SPEED;
    public static final PotionEffectType SLOWNESS = PotionEffectType.SLOWNESS;
    public static final PotionEffectType HASTE = PotionEffectType.HASTE;
    public static final PotionEffectType MINING_FATIGUE = PotionEffectType.MINING_FATIGUE;
    public static final PotionEffectType STRENGTH = PotionEffectType.STRENGTH;
    public static final PotionEffectType JUMP_BOOST = PotionEffectType.JUMP_BOOST;
    public static final PotionEffectType NAUSEA = PotionEffectType.NAUSEA;
    public static final PotionEffectType REGENERATION = PotionEffectType.REGENERATION;
    public static final PotionEffectType RESISTANCE = PotionEffectType.RESISTANCE;
    public static final PotionEffectType FIRE_RESISTANCE = PotionEffectType.FIRE_RESISTANCE;
    public static final PotionEffectType WATER_BREATHING = PotionEffectType.WATER_BREATHING;
    public static final PotionEffectType INVISIBILITY = PotionEffectType.INVISIBILITY;
    public static final PotionEffectType BLINDNESS = PotionEffectType.BLINDNESS;
    public static final PotionEffectType NIGHT_VISION = PotionEffectType.NIGHT_VISION;
    public static final PotionEffectType HUNGER = PotionEffectType.HUNGER;
    public static final PotionEffectType WEAKNESS = PotionEffectType.WEAKNESS;
    public static final PotionEffectType POISON = PotionEffectType.POISON;
    public static final PotionEffectType WITHER = PotionEffectType.WITHER;
    public static final PotionEffectType ABSORPTION = PotionEffectType.ABSORPTION;
    public static final PotionEffectType GLOWING = PotionEffectType.GLOWING;
    public static final PotionEffectType LEVITATION = PotionEffectType.LEVITATION;
    public static final PotionEffectType LUCK = PotionEffectType.LUCK;
    public static final PotionEffectType SLOW_FALLING = PotionEffectType.SLOW_FALLING;
    public static final PotionEffectType DARKNESS = PotionEffectType.DARKNESS;

    private static final Set<PotionEffectType> NEGATIVE = new HashSet<>(Arrays.asList(
            SLOWNESS, MINING_FATIGUE, NAUSEA, BLINDNESS, HUNGER, WEAKNESS, POISON, WITHER, LEVITATION, DARKNESS,
            PotionEffectType.INSTANT_DAMAGE, PotionEffectType.UNLUCK, PotionEffectType.BAD_OMEN));

    private Effects() {
    }

    public static boolean isNegative(PotionEffectType type) {
        return type != null && NEGATIVE.contains(type);
    }

    private static final java.util.Map<String, PotionEffectType> BY_NAME = new java.util.HashMap<>();

    static {
        BY_NAME.put("speed", SPEED);
        BY_NAME.put("slowness", SLOWNESS);
        BY_NAME.put("haste", HASTE);
        BY_NAME.put("mining_fatigue", MINING_FATIGUE);
        BY_NAME.put("strength", STRENGTH);
        BY_NAME.put("jump_boost", JUMP_BOOST);
        BY_NAME.put("nausea", NAUSEA);
        BY_NAME.put("regeneration", REGENERATION);
        BY_NAME.put("resistance", RESISTANCE);
        BY_NAME.put("fire_resistance", FIRE_RESISTANCE);
        BY_NAME.put("water_breathing", WATER_BREATHING);
        BY_NAME.put("invisibility", INVISIBILITY);
        BY_NAME.put("blindness", BLINDNESS);
        BY_NAME.put("night_vision", NIGHT_VISION);
        BY_NAME.put("hunger", HUNGER);
        BY_NAME.put("weakness", WEAKNESS);
        BY_NAME.put("poison", POISON);
        BY_NAME.put("wither", WITHER);
        BY_NAME.put("absorption", ABSORPTION);
        BY_NAME.put("glowing", GLOWING);
        BY_NAME.put("levitation", LEVITATION);
        BY_NAME.put("luck", LUCK);
        BY_NAME.put("slow_falling", SLOW_FALLING);
        BY_NAME.put("darkness", DARKNESS);
    }

    public static PotionEffectType byName(String name) {
        if (name == null) {
            return null;
        }
        return BY_NAME.get(name.toLowerCase(Locale.ROOT).replace(' ', '_'));
    }

    /** Nakłada efekt (ukryte czastki, bez ikony ambient). */
    public static void apply(LivingEntity entity, PotionEffectType type, int ticks, int amplifier) {
        if (entity == null || type == null) {
            return;
        }
        entity.addPotionEffect(new PotionEffect(type, Math.max(1, ticks), Math.max(0, amplifier), false, false, true));
    }

    /** Nakłada efekt tylko gdy gracz go nie ma lub ma slabszy/konczacy sie. */
    public static void refresh(LivingEntity entity, PotionEffectType type, int ticks, int amplifier) {
        if (entity == null || type == null) {
            return;
        }
        PotionEffect current = entity.getPotionEffect(type);
        if (current != null && current.getAmplifier() > amplifier) {
            return;
        }
        if (current != null && current.getAmplifier() == amplifier && current.getDuration() > ticks / 2) {
            return;
        }
        apply(entity, type, ticks, amplifier);
    }

    public static void clearNegative(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        for (PotionEffect effect : new java.util.ArrayList<>(entity.getActivePotionEffects())) {
            if (isNegative(effect.getType())) {
                entity.removePotionEffect(effect.getType());
            }
        }
        entity.setFireTicks(0);
        entity.setFreezeTicks(0);
    }

    /** Blokuje skakanie (JUMP_BOOST o amplifierze 128 = brak skoku). */
    public static void blockJump(LivingEntity entity, int ticks) {
        apply(entity, JUMP_BOOST, ticks, 128);
    }
}
