package com.ezinnovations.eznightvision.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class NightVisionTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ENGLISH);
            List<String> suggestions = new ArrayList<String>();
            if ("on".startsWith(input)) {
                suggestions.add("on");
            }
            if ("off".startsWith(input)) {
                suggestions.add("off");
            }
            return suggestions;
        }

        if (args.length == 2 && ("on".equalsIgnoreCase(args[0]) || "off".equalsIgnoreCase(args[0]))) {
            String input = args[1].toLowerCase(Locale.ENGLISH);
            List<String> suggestions = new ArrayList<String>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                String name = player.getName();
                if (name.toLowerCase(Locale.ENGLISH).startsWith(input)) {
                    suggestions.add(name);
                }
            }
            return suggestions;
        }

        return Collections.emptyList();
    }
}
