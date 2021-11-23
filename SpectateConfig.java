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


public class SpectateConfig implements CommandExecutor {

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("MirrorInventory")) {
                if (!(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not true or false.");
                }
                else {
                    Config.mirrorInventory = Boolean.parseBoolean(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Set " + args[0] + " to " + args[1]);
                }
            } else if (args[0].equalsIgnoreCase("HideFromTab")) {
                if (!(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not true or false.");
                }
                else {
                    Config.hideFromTab = Boolean.parseBoolean(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Set " + args[0] + " to " + args[1]);
                }
            } else if (args[0].equalsIgnoreCase("CycleOnPlayerDeath")) {
                if (!(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not true or false.");
                }
                else {
                    Config.cycleOnPlayerDeath = Boolean.parseBoolean(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Set " + args[0] + " to " + args[1]);
                }
            } else if (args[0].equalsIgnoreCase("RememberSurvivalPosition")) {
                if (!(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not true or false.");
                }
                else {
                    Config.rememberSurvivalPosition = Boolean.parseBoolean(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Set " + args[0] + " to " + args[1]);
                }
            } else if (args[0].equalsIgnoreCase("OnlySpecPlayers")) {
                if (!(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not true or false.");
                }
                else {
                    Config.specNoPlayer = Boolean.parseBoolean(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Set " + args[0] + " to " + args[1]);
                }
            } else if (args[0].equalsIgnoreCase("DismountMode")) {
                if (!(args[1].equalsIgnoreCase("default") || args[1].equalsIgnoreCase("lock") || args[1].equalsIgnoreCase("unspec"))) {
                    sender.sendMessage(ChatColor.RED + args[1] + " must be default, lock, or unspec.");
                }
                else {
                    Config.dismountMode = args[1];
                    sender.sendMessage(ChatColor.GREEN + "Set " + args[0] + " to " + args[1]);
                }
            } else if (args[0].equalsIgnoreCase("EnableBypasses")) {
                if (!(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not true or false.");
                }
                else {
                    Config.enableBypasses = Boolean.parseBoolean(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Set " + args[0] + " to " + args[1]);
                }
            } else if (args[0].equalsIgnoreCase("SupportInventoryBlocks")) {
                if (!(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not true or false.");
                } else {
                    Config.inventoryBlockSupport = Boolean.parseBoolean(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Set " + args[0] + " to " + args[1]);
                }
            } else if (args[0].equalsIgnoreCase("StopCycleOnDisconnect")) {
                if (!(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not true or false.");
                } else {
                    Config.cycleStopOnDC = Boolean.parseBoolean(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Set " + args[0] + " to " + args[1]);
                }
            }

            else {
                sender.sendMessage(ChatColor.RED + args[0] + " is not a valid key.");
            }
            Config.saveConfig();

        }
        else {
            sender.sendMessage(ChatColor.RED + "Usage: /spectateconfig <key> <value>");
        }
        return true;
    }

}
