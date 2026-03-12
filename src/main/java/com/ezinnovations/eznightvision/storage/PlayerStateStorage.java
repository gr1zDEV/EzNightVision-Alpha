package com.ezinnovations.eznightvision.storage;

import com.ezinnovations.eznightvision.EzNightVision;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class PlayerStateStorage {

    private final EzNightVision plugin;
    private final File storageFile;

    private FileConfiguration storageConfig;

    public PlayerStateStorage(EzNightVision plugin) {
        this.plugin = plugin;
        this.storageFile = new File(plugin.getDataFolder(), "playerdata.yml");
    }

    public void load() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Could not create plugin data folder.");
        }

        if (!storageFile.exists()) {
            try {
                if (!storageFile.createNewFile()) {
                    plugin.getLogger().warning("Could not create playerdata.yml file.");
                }
            } catch (IOException ex) {
                plugin.getLogger().severe("Failed to create playerdata.yml: " + ex.getMessage());
            }
        }

        this.storageConfig = YamlConfiguration.loadConfiguration(storageFile);
    }

    public void save() {
        if (storageConfig == null) {
            return;
        }

        try {
            storageConfig.save(storageFile);
        } catch (IOException ex) {
            plugin.getLogger().severe("Failed to save playerdata.yml: " + ex.getMessage());
        }
    }

    public boolean isEnabled(UUID uuid) {
        ensureLoaded();

        String path = path(uuid);
        if (storageConfig.contains(path)) {
            return storageConfig.getBoolean(path);
        }

        return plugin.getConfig().getBoolean("default-enabled", true);
    }

    public void setEnabled(UUID uuid, boolean enabled) {
        ensureLoaded();
        storageConfig.set(path(uuid), enabled);
        save();
    }

    private void ensureLoaded() {
        if (storageConfig == null) {
            load();
        }
    }

    private String path(UUID uuid) {
        return "players." + uuid;
    }
}
