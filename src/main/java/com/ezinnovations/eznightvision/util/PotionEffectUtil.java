package com.ezinnovations.eznightvision.util;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class PotionEffectUtil {

    private static final Constructor<PotionEffect> SIX_ARG_CONSTRUCTOR;

    static {
        Constructor<PotionEffect> constructor = null;
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
            // Older API without icon support.
        }
        SIX_ARG_CONSTRUCTOR = constructor;
    }

    private PotionEffectUtil() {
    }

    public static PotionEffect createNightVision(int duration, int amplifier) {
        if (SIX_ARG_CONSTRUCTOR != null) {
            try {
                return SIX_ARG_CONSTRUCTOR.newInstance(PotionEffectType.NIGHT_VISION, duration, amplifier, false, false, false);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                // Fall through to legacy constructor.
            }
        }

        return new PotionEffect(PotionEffectType.NIGHT_VISION, duration, amplifier, false, false);
    }

    public static boolean isIconSupported() {
        return SIX_ARG_CONSTRUCTOR != null;
    }
}
