package com.ezinnovations.eznightvision.command;

import com.ezinnovations.eznightvision.service.NightVisionService;
import com.ezinnovations.eznightvision.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class NightVisionCommand implements CommandExecutor {

    private static final String PERMISSION_USE = "eznightvision.use";
    private static final String PERMISSION_OTHERS = "eznightvision.others";
    private static final String PERMISSION_ADMIN = "eznightvision.admin";

    private final NightVisionService nightVisionService;
    private final MessageUtil messageUtil;

    public NightVisionCommand(NightVisionService nightVisionService, MessageUtil messageUtil) {
        this.nightVisionService = nightVisionService;
        this.messageUtil = messageUtil;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                messageUtil.send(sender, "console-usage");
                return true;
            }

            if (!sender.hasPermission(PERMISSION_USE)) {
                messageUtil.send(sender, "no-permission");
                return true;
            }

            boolean enabled = nightVisionService.toggleForPlayer(player);
            messageUtil.send(sender, enabled ? "toggled-on" : "toggled-off");
            return true;
        }

        String action = args[0].toLowerCase();
        if (!action.equals("on") && !action.equals("off")) {
            messageUtil.send(sender, "invalid-usage");
            return true;
        }

        boolean enable = action.equals("on");

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                messageUtil.send(sender, "console-usage");
                return true;
            }

            if (!sender.hasPermission(PERMISSION_USE)) {
                messageUtil.send(sender, "no-permission");
                return true;
            }

            nightVisionService.setForPlayer(player, enable);
            messageUtil.send(sender, enable ? "enabled-self" : "disabled-self");
            return true;
        }

        if (!canModifyOthers(sender)) {
            messageUtil.send(sender, "no-permission");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            messageUtil.send(sender, "player-not-found");
            return true;
        }

        nightVisionService.setForPlayer(target, enable);
        messageUtil.send(sender, enable ? "enabled-other" : "disabled-other", "%player%", target.getName());
        messageUtil.send(target, enable ? "enabled-self" : "disabled-self");
        return true;
    }

    private boolean canModifyOthers(CommandSender sender) {
        return sender.hasPermission(PERMISSION_OTHERS) || sender.hasPermission(PERMISSION_ADMIN);
    }
}
