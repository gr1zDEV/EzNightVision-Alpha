package com.ezinnovations.eznightvision;

import com.ezinnovations.eznightvision.command.NightVisionCommand;
import com.ezinnovations.eznightvision.command.NightVisionTabCompleter;
import com.ezinnovations.eznightvision.listener.PlayerListener;
import com.ezinnovations.eznightvision.service.NightVisionService;
import com.ezinnovations.eznightvision.storage.PlayerStateStorage;
import com.ezinnovations.eznightvision.util.MessageUtil;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class EzNightVision extends JavaPlugin {

    private PlayerStateStorage playerStateStorage;
    private MessageUtil messageUtil;
    private NightVisionService nightVisionService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.playerStateStorage = new PlayerStateStorage(this);
        this.playerStateStorage.load();

        this.messageUtil = new MessageUtil(this);
        this.nightVisionService = new NightVisionService(this, playerStateStorage);

        registerCommand();
        getServer().getPluginManager().registerEvents(new PlayerListener(this, nightVisionService), this);

        getLogger().info("EzNightVision enabled.");
    }

    @Override
    public void onDisable() {
        if (playerStateStorage != null) {
            playerStateStorage.save();
        }
    }

    private void registerCommand() {
        NightVisionCommand executor = new NightVisionCommand(nightVisionService, messageUtil);
        NightVisionTabCompleter tabCompleter = new NightVisionTabCompleter();

        PluginCommand nightVision = Objects.requireNonNull(getCommand("nightvision"), "nightvision command missing");
        nightVision.setExecutor(executor);
        nightVision.setTabCompleter(tabCompleter);

        PluginCommand nv = Objects.requireNonNull(getCommand("nv"), "nv command missing");
        nv.setExecutor(executor);
        nv.setTabCompleter(tabCompleter);
    }
}
