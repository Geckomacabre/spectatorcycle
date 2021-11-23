package com.kosakorner.spectator.command;

import com.kosakorner.spectator.Spectator;
import com.kosakorner.spectator.config.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCycleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                switch (args[0]) {
                    case "start":
                        if (args.length == 2) {
                            if (!Spectator.cycleHandler.isPlayerCycling(player)) {
                                try {
                                    int interval = Integer.parseInt(args[1]);
                                    Spectator.cycleHandler.startCycle(player, interval * 20);
                                    sender.sendMessage(Messages.translate("Messages.Spectate.CycleStart", "interval", interval));
                                    return true;
                                }
                                catch (NumberFormatException e) {
                                    return false;
                                }
                            }
                            else {
                                sender.sendMessage(Messages.translate("Messages.Spectate.CycleRunning"));
                                return true;
                            }
                        }
                        else {
                            sender.sendMessage(ChatColor.RED + "Usage: /spectatecycle start <seconds>");
                            return true;
                        }
                    case "stop":
                        if (Spectator.cycleHandler.isPlayerCycling(player)) {
                            Spectator.cycleHandler.stopCycle(player);
                            // The stop message is sent in the stopCycle() method.
                        }
                        else {
                            sender.sendMessage(Messages.translate("Messages.Spectate.CycleInactive"));
                        }
                        return true;
                }
            }
            return false;
        }
        else {
            sender.sendMessage(Messages.translate("Messages.Player.NotPlayer"));
        }
        return true;
    }

}
