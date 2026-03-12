package com.ezinnovations.eznightvision.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class NightVisionTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();
            if ("on".startsWith(input)) {
                completions.add("on");
            }
            if ("off".startsWith(input)) {
                completions.add("off");
            }
            return completions;
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
            String input = args[1].toLowerCase(Locale.ROOT);
            List<String> completions = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.getName().toLowerCase(Locale.ROOT).startsWith(input)) {
                    completions.add(player.getName());
                }
            });
            return completions;
        }

        return Collections.emptyList();
    }
}
