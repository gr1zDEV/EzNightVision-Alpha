package com.ezinnovations.eznightvision.listener;

import com.ezinnovations.eznightvision.service.NightVisionService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;

public final class PlayerListener implements Listener {

    private final NightVisionService nightVisionService;

    public PlayerListener(NightVisionService nightVisionService) {
        this.nightVisionService = nightVisionService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        nightVisionService.applyIfEnabled(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        nightVisionService.scheduleRespawnReapply(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNightVisionRemoved(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getModifiedType() != PotionEffectType.NIGHT_VISION) {
            return;
        }

        Player player = (Player) event.getEntity();
        nightVisionService.reapplyIfTrackingEnabled(player);
    }
}
