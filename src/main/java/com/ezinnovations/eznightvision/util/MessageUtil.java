package com.ezinnovations.eznightvision.util;

import com.ezinnovations.eznightvision.EzNightVision;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class MessageUtil {

    private final EzNightVision plugin;

    public MessageUtil(EzNightVision plugin) {
        this.plugin = plugin;
    }

    public void send(CommandSender sender, String key) {
        send(sender, key, null, null);
    }

    public void send(CommandSender sender, String key, String placeholder, String value) {
        String prefix = color(plugin.getConfig().getString("messages.prefix", "&8[&bEzNightVision&8] "));
        String message = plugin.getConfig().getString("messages." + key, defaultMessage(key));

        if (placeholder != null && value != null) {
            message = message.replace(placeholder, value);
        }

        sender.sendMessage(prefix + color(message));
    }

    private String defaultMessage(String key) {
        if ("enabled-self".equals(key)) {
            return "&aNight Vision enabled.";
        }
        if ("disabled-self".equals(key)) {
            return "&cNight Vision disabled.";
        }
        if ("toggled-on".equals(key)) {
            return "&aNight Vision toggled on.";
        }
        if ("toggled-off".equals(key)) {
            return "&cNight Vision toggled off.";
        }
        if ("enabled-other".equals(key)) {
            return "&aEnabled Night Vision for &f%player%&a.";
        }
        if ("disabled-other".equals(key)) {
            return "&cDisabled Night Vision for &f%player%&c.";
        }
        if ("no-permission".equals(key)) {
            return "&cYou do not have permission to do that.";
        }
        if ("player-not-found".equals(key)) {
            return "&cThat player could not be found.";
        }
        if ("console-usage".equals(key)) {
            return "&cConsole must use /nightvision <on|off> <player>.";
        }
        return "&eUsage: /nightvision [on|off] [player]";
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
