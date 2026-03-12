package com.ezinnovations.eznightvision.listener;

import com.ezinnovations.eznightvision.EzNightVision;
import com.ezinnovations.eznightvision.service.NightVisionService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class PlayerListener implements Listener {

    private final NightVisionService nightVisionService;

    public PlayerListener(EzNightVision plugin, NightVisionService nightVisionService) {
        this.nightVisionService = nightVisionService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        nightVisionService.applyIfEnabled(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        nightVisionService.runPlayerTaskLater(player, 2L, () -> nightVisionService.applyIfEnabled(player));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPotionEffectChange(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (event.getModifiedType() == null || !nightVisionService.isNightVisionType(event.getModifiedType())) {
            return;
        }

        if (event.getAction() == EntityPotionEffectEvent.Action.REMOVED
                || event.getAction() == EntityPotionEffectEvent.Action.CLEARED) {
            nightVisionService.runPlayerTaskLater(player, 1L, () -> nightVisionService.applyIfEnabled(player));
        }
    }
}
