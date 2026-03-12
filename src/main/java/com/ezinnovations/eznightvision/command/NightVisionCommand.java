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
            return handleToggleSelf(sender);
        }

        String action = args[0].toLowerCase();
        if (!"on".equals(action) && !"off".equals(action)) {
            messageUtil.send(sender, "invalid-usage");
            return true;
        }

        boolean enable = "on".equals(action);

        if (args.length == 1) {
            return handleSetSelf(sender, enable);
        }

        if (args.length == 2) {
            return handleSetOther(sender, enable, args[1]);
        }

        messageUtil.send(sender, "invalid-usage");
        return true;
    }

    private boolean handleToggleSelf(CommandSender sender) {
        if (!(sender instanceof Player)) {
            messageUtil.send(sender, "console-usage");
            return true;
        }

        if (!sender.hasPermission(PERMISSION_USE)) {
            messageUtil.send(sender, "no-permission");
            return true;
        }

        Player player = (Player) sender;
        boolean enabled = nightVisionService.toggleForPlayer(player);
        messageUtil.send(sender, enabled ? "toggled-on" : "toggled-off");
        return true;
    }

    private boolean handleSetSelf(CommandSender sender, boolean enable) {
        if (!(sender instanceof Player)) {
            messageUtil.send(sender, "console-usage");
            return true;
        }

        if (!sender.hasPermission(PERMISSION_USE)) {
            messageUtil.send(sender, "no-permission");
            return true;
        }

        Player player = (Player) sender;
        nightVisionService.setForPlayer(player, enable);
        messageUtil.send(sender, enable ? "enabled-self" : "disabled-self");
        return true;
    }

    private boolean handleSetOther(CommandSender sender, boolean enable, String targetName) {
        if (!canModifyOthers(sender)) {
            messageUtil.send(sender, "no-permission");
            return true;
        }

        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            messageUtil.send(sender, "player-not-found");
            return true;
        }

        nightVisionService.setForPlayer(target, enable);
        messageUtil.send(sender, enable ? "enabled-other" : "disabled-other", "%player%", target.getName());

        if (!sender.getName().equalsIgnoreCase(target.getName())) {
            messageUtil.send(target, enable ? "enabled-self" : "disabled-self");
        }

        return true;
    }

    private boolean canModifyOthers(CommandSender sender) {
        return sender.hasPermission(PERMISSION_OTHERS) || sender.hasPermission(PERMISSION_ADMIN);
    }
}
