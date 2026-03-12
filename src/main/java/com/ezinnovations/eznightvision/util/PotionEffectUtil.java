package com.ezinnovations.eznightvision.util;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class PotionEffectUtil {

    private static final Constructor<PotionEffect> SIX_ARG_POTION_EFFECT_CTOR;

    static {
        Constructor<PotionEffect> constructor;
        try {
            constructor = PotionEffect.class.getConstructor(
                    PotionEffectType.class,
                    int.class,
                    int.class,
                    boolean.class,
                    boolean.class,
                    boolean.class
            );
        } catch (NoSuchMethodException ignored) {
            constructor = null;
        }
        SIX_ARG_POTION_EFFECT_CTOR = constructor;
    }

    private PotionEffectUtil() {
    }

    public static PotionEffect createNightVision(int durationTicks, int amplifier) {
        if (SIX_ARG_POTION_EFFECT_CTOR != null) {
            try {
                return SIX_ARG_POTION_EFFECT_CTOR.newInstance(
                        PotionEffectType.NIGHT_VISION,
                        Integer.valueOf(durationTicks),
                        Integer.valueOf(amplifier),
                        Boolean.FALSE,
                        Boolean.FALSE,
                        Boolean.FALSE
                );
            } catch (IllegalAccessException ignored) {
                // Fallback to old constructor below.
            } catch (InstantiationException ignored) {
                // Fallback to old constructor below.
            } catch (InvocationTargetException ignored) {
                // Fallback to old constructor below.
            }
        }

        return new PotionEffect(PotionEffectType.NIGHT_VISION, durationTicks, amplifier, false, false);
    }

    public static boolean isMatchingNightVision(PotionEffect effect, int minDurationTicks, int requiredAmplifier) {
        if (effect == null) {
            return false;
        }

        if (effect.getType() != PotionEffectType.NIGHT_VISION) {
            return false;
        }

        if (effect.getAmplifier() != requiredAmplifier) {
            return false;
        }

        if (effect.getDuration() < minDurationTicks / 4) {
            return false;
        }

        return !effect.hasParticles() && !effect.isAmbient();
    }
}
