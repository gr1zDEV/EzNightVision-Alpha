package com.ezinnovations.eznightvision.storage;

import com.ezinnovations.eznightvision.EzNightVision;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerStateStorage {

    private final EzNightVision plugin;
    private final File dataFile;
    private final Map<UUID, Boolean> stateByUuid;

    public PlayerStateStorage(EzNightVision plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        this.stateByUuid = new HashMap<UUID, Boolean>();
    }

    public void load() {
        stateByUuid.clear();

        if (!dataFile.exists()) {
            ensureDataFolder();
            save();
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection section = config.getConfigurationSection("players");
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                boolean enabled = section.getBoolean(key);
                stateByUuid.put(uuid, enabled);
            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Skipping invalid UUID in playerdata.yml: " + key);
            }
        }
    }

    public void save() {
        ensureDataFolder();

        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Boolean> entry : stateByUuid.entrySet()) {
            config.set("players." + entry.getKey().toString(), entry.getValue());
        }

        try {
            config.save(dataFile);
        } catch (IOException ex) {
            plugin.getLogger().severe("Failed to save playerdata.yml: " + ex.getMessage());
        }
    }

    public boolean isEnabled(UUID uuid) {
        Boolean value = stateByUuid.get(uuid);
        if (value != null) {
            return value.booleanValue();
        }
        return plugin.getConfig().getBoolean("default-enabled", true);
    }

    public void setEnabled(UUID uuid, boolean enabled) {
        stateByUuid.put(uuid, Boolean.valueOf(enabled));
        save();
    }

    private void ensureDataFolder() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Could not create plugin data folder: " + plugin.getDataFolder().getAbsolutePath());
        }
    }
}
