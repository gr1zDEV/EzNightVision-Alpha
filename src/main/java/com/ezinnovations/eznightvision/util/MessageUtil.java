package com.ezinnovations.eznightvision.util;

import com.ezinnovations.eznightvision.EzNightVision;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class MessageUtil {

    private final EzNightVision plugin;

    public MessageUtil(EzNightVision plugin) {
        this.plugin = plugin;
    }

    public void send(CommandSender sender, String key, String... replacements) {
        String prefix = color(getRaw("messages.prefix", "&8[&bEzNightVision&8] "));
        String base = color(getRaw("messages." + key, fallbackForKey(key)));

        if (replacements.length % 2 == 0) {
            for (int i = 0; i < replacements.length; i += 2) {
                String find = replacements[i];
                String replace = replacements[i + 1];
                base = base.replace(find, replace);
            }
        }

        sender.sendMessage(prefix + base);
    }

    private String getRaw(String path, String fallback) {
        String value = plugin.getConfig().getString(path);
        return value != null ? value : fallback;
    }

    private String fallbackForKey(String key) {
        return switch (key) {
            case "enabled-self" -> "&aNight Vision enabled.";
            case "disabled-self" -> "&cNight Vision disabled.";
            case "toggled-on" -> "&aNight Vision toggled on.";
            case "toggled-off" -> "&cNight Vision toggled off.";
            case "enabled-other" -> "&aEnabled Night Vision for &f%player%&a.";
            case "disabled-other" -> "&cDisabled Night Vision for &f%player%&c.";
            case "no-permission" -> "&cYou do not have permission to do that.";
            case "player-not-found" -> "&cThat player could not be found.";
            case "console-usage" -> "&cConsole must use /nightvision <on|off> <player>.";
            case "invalid-usage" -> "&eUsage: /nightvision [on|off] [player]";
            default -> "&cMissing message: " + key;
        };
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
