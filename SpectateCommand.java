package com.kosakorner.spectator.command;

import com.kosakorner.spectator.Spectator;
import com.kosakorner.spectator.config.Config;
import com.kosakorner.spectator.config.Messages;
import com.kosakorner.spectator.config.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SpectateCommand implements CommandExecutor {

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1 && Spectator.hasPermission(sender, Permissions.TELEPORT)) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    if (target.getUniqueId().equals(player.getUniqueId())) {
                        sender.sendMessage(Messages.translate("Messages.Spectate.Self"));
                        return true;
                    }
                    if (Spectator.spectatorRelations.get(player) == target) {
                        sender.sendMessage(Messages.translate("Messages.Spectate.NoChange", "player", target.getName()));
                        return true;
                    }
                    if (Spectator.spectatorRelations.get(target) == player || (Spectator.hasPermission(target, Permissions.BYPASS_VIEWABLE) && Config.enableBypasses == true)) {
                        sender.sendMessage(Messages.translate("Messages.Spectate.NoSpectate", "player", target.getName()));
                        return true;
                    }
                    Spectator.playerListener.spectatePlayer(player, target);
                    sender.sendMessage(Messages.translate("Messages.Spectate.Other", "player", target.getName()));
                }
                else {
                    sender.sendMessage(Messages.translate("Messages.Player.Offline", "player", args[0]));
                }
                return true;
            }
            else {
                if (!player.getGameMode().equals(GameMode.SPECTATOR)) {


                    if (Config.specNoPlayer == false) {
                        Spectator.playerListener.spectatePlayer(player, null);
                        sender.sendMessage(Messages.translate("Messages.Spectate.General"));
                    }
                    else {
                        sender.sendMessage(Messages.translate("Messages.Spectate.OnlySpecPlayer"));
                    }
                }



                else {
                    Spectator.playerListener.unspectatePlayer(player);
                    if (Spectator.cycleHandler.isPlayerCycling(player)) {
                        Spectator.cycleHandler.stopCycle(player);
                    }
                    sender.sendMessage(Messages.translate("Messages.Spectate.Off"));
                }
                return true;
            }
        }
        else {
            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    Spectator.playerListener.spectatePlayer(target, null);
                    sender.sendMessage(Messages.translate("Messages.Spectate.GiveOther", "player", target.getName()));
                }
                else {
                    sender.sendMessage(Messages.translate("Messages.Player.Offline", "player", args[0]));
                }
            }
            else {
                sender.sendMessage(ChatColor.RED + "Usage: /spectate <player>");
            }
        }

        return true;
    }

}
