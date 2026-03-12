package com.ezinnovations.eznightvision.service;

import com.ezinnovations.eznightvision.EzNightVision;
import com.ezinnovations.eznightvision.storage.PlayerStateStorage;
import com.ezinnovations.eznightvision.util.PotionEffectUtil;
import com.ezinnovations.eznightvision.util.SchedulerUtil;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class NightVisionService {

    private static final int NIGHT_VISION_DURATION_TICKS = 20 * 60 * 60;
    private static final int NIGHT_VISION_AMPLIFIER = 0;

    private final EzNightVision plugin;
    private final PlayerStateStorage playerStateStorage;
    private final SchedulerUtil schedulerUtil;

    public NightVisionService(EzNightVision plugin, PlayerStateStorage playerStateStorage, SchedulerUtil schedulerUtil) {
        this.plugin = plugin;
        this.playerStateStorage = playerStateStorage;
        this.schedulerUtil = schedulerUtil;
    }

    public boolean toggleForPlayer(Player player) {
        boolean currentlyEnabled = playerStateStorage.isEnabled(player.getUniqueId());
        boolean newState = !currentlyEnabled;
        setForPlayer(player, newState);
        return newState;
    }

    public void setForPlayer(Player player, boolean enabled) {
        playerStateStorage.setEnabled(player.getUniqueId(), enabled);

        if (enabled) {
            applyNightVision(player);
        } else {
            removeNightVision(player);
        }
    }

    public void applyIfEnabled(Player player) {
        if (playerStateStorage.isEnabled(player.getUniqueId())) {
            applyNightVision(player);
        } else {
            removeNightVision(player);
        }
    }

    public void reapplyIfTrackingEnabled(final Player player) {
        if (!playerStateStorage.isEnabled(player.getUniqueId())) {
            return;
        }

        schedulerUtil.runPlayerTaskLater(player, new Runnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }
                applyNightVision(player);
            }
        }, 1L);
    }

    public void scheduleRespawnReapply(final Player player) {
        if (!playerStateStorage.isEnabled(player.getUniqueId())) {
            return;
        }

        schedulerUtil.runPlayerTaskLater(player, new Runnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }
                applyNightVision(player);
            }
        }, 2L);
    }

    private void applyNightVision(Player player) {
        PotionEffect current = player.getPotionEffect(PotionEffectType.NIGHT_VISION);
        if (PotionEffectUtil.isMatchingNightVision(current, NIGHT_VISION_DURATION_TICKS, NIGHT_VISION_AMPLIFIER)) {
            return;
        }

        PotionEffect effect = PotionEffectUtil.createNightVision(NIGHT_VISION_DURATION_TICKS, NIGHT_VISION_AMPLIFIER);
        player.addPotionEffect(effect, true);
    }

    private void removeNightVision(Player player) {
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }
}
