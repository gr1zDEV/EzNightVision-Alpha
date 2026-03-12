package com.ezinnovations.eznightvision;

import com.ezinnovations.eznightvision.command.NightVisionCommand;
import com.ezinnovations.eznightvision.command.NightVisionTabCompleter;
import com.ezinnovations.eznightvision.listener.PlayerListener;
import com.ezinnovations.eznightvision.service.NightVisionService;
import com.ezinnovations.eznightvision.storage.PlayerStateStorage;
import com.ezinnovations.eznightvision.util.MessageUtil;
import com.ezinnovations.eznightvision.util.SchedulerUtil;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class EzNightVision extends JavaPlugin {

    private PlayerStateStorage playerStateStorage;
    private MessageUtil messageUtil;
    private SchedulerUtil schedulerUtil;
    private NightVisionService nightVisionService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.playerStateStorage = new PlayerStateStorage(this);
        this.playerStateStorage.load();

        this.messageUtil = new MessageUtil(this);
        this.schedulerUtil = new SchedulerUtil(this);
        this.nightVisionService = new NightVisionService(this, playerStateStorage, schedulerUtil);

        registerCommand();
        getServer().getPluginManager().registerEvents(new PlayerListener(nightVisionService), this);

        for (Player player : getServer().getOnlinePlayers()) {
            nightVisionService.applyIfEnabled(player);
        }

        getLogger().info("EzNightVision enabled.");
    }

    @Override
    public void onDisable() {
        if (playerStateStorage != null) {
            playerStateStorage.save();
        }
    }

    private void registerCommand() {
        NightVisionCommand commandHandler = new NightVisionCommand(nightVisionService, messageUtil);
        NightVisionTabCompleter tabCompleter = new NightVisionTabCompleter();

        PluginCommand nightVisionCommand = Objects.requireNonNull(getCommand("nightvision"), "nightvision command missing");
        nightVisionCommand.setExecutor(commandHandler);
        nightVisionCommand.setTabCompleter(tabCompleter);

        PluginCommand nvCommand = Objects.requireNonNull(getCommand("nv"), "nv command missing");
        nvCommand.setExecutor(commandHandler);
        nvCommand.setTabCompleter(tabCompleter);
    }
}
