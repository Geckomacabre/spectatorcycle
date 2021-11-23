package com.kosakorner.spectator.command;

import com.kosakorner.spectator.config.Config;
import com.kosakorner.spectator.config.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SpectateReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Config.loadConfig();
        Messages.loadMessages();
        sender.sendMessage(Messages.translate("Messages.Plugin.Reload"));
        return true;
    }

}
