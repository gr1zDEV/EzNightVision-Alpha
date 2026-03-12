package com.ezinnovations.eznightvision.service;

import com.ezinnovations.eznightvision.EzNightVision;
import com.ezinnovations.eznightvision.storage.PlayerStateStorage;
import com.ezinnovations.eznightvision.util.PotionEffectUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Consumer;

public final class NightVisionService {

    private static final int VERY_LONG_DURATION = Integer.MAX_VALUE;
    private static final int AMPLIFIER = 0;

    private final EzNightVision plugin;
    private final PlayerStateStorage playerStateStorage;

    public NightVisionService(EzNightVision plugin, PlayerStateStorage playerStateStorage) {
        this.plugin = plugin;
        this.playerStateStorage = playerStateStorage;
    }

    public boolean toggleForPlayer(Player player) {
        boolean enabled = isEnabled(player.getUniqueId());
        boolean newState = !enabled;
        setForPlayer(player, newState);
        return newState;
    }

    public void setForPlayer(Player player, boolean enabled) {
        UUID uuid = player.getUniqueId();
        playerStateStorage.setEnabled(uuid, enabled);

        if (enabled) {
            applyIfEnabled(player);
            return;
        }

        remove(player);
    }

    public void applyIfEnabled(Player player) {
        if (!player.isOnline() || player.isDead()) {
            return;
        }

        if (!isEnabled(player.getUniqueId())) {
            remove(player);
            return;
        }

        if (hasExpectedNightVision(player)) {
            return;
        }

        PotionEffect effect = PotionEffectUtil.createNightVision(VERY_LONG_DURATION, AMPLIFIER);
        player.addPotionEffect(effect, true);
    }

    public void remove(Player player) {
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    public boolean isEnabled(UUID uuid) {
        return playerStateStorage.isEnabled(uuid);
    }

    public boolean hasExpectedNightVision(Player player) {
        PotionEffect existing = player.getPotionEffect(PotionEffectType.NIGHT_VISION);
        if (existing == null) {
            return false;
        }

        if (existing.getAmplifier() != AMPLIFIER) {
            return false;
        }

        if (existing.hasParticles()) {
            return false;
        }

        if (existing.isAmbient()) {
            return false;
        }

        if (existing.getDuration() <= 200) {
            return false;
        }

        return !PotionEffectUtil.isIconSupported() || !existing.hasIcon();
    }

    public boolean isNightVisionType(PotionEffectType type) {
        return PotionEffectType.NIGHT_VISION.equals(type);
    }

    /**
     * Runs a delayed task in a Folia-safe way when available, with Bukkit fallback.
     */
    public void runPlayerTaskLater(Player player, long delayTicks, Runnable task) {
        if (!player.isOnline()) {
            return;
        }

        if (tryRunFoliaEntityTask(player, delayTicks, task)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    @SuppressWarnings("unchecked")
    private boolean tryRunFoliaEntityTask(Player player, long delayTicks, Runnable task) {
        try {
            Method getScheduler = player.getClass().getMethod("getScheduler");
            Object scheduler = getScheduler.invoke(player);
            if (scheduler == null) {
                return false;
            }

            Method runDelayed = scheduler.getClass().getMethod(
                    "runDelayed",
                    org.bukkit.plugin.Plugin.class,
                    Consumer.class,
                    Runnable.class,
                    long.class
            );

            Consumer<Object> consumer = ignored -> task.run();
            Runnable retired = () -> { };
            runDelayed.invoke(scheduler, plugin, consumer, retired, delayTicks);
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            return false;
        }
    }
}
