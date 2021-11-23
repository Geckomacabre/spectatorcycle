package com.kosakorner.spectator.config;

import com.kosakorner.spectator.Spectator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    public static boolean mirrorInventory;
    public static boolean hideFromTab;
    public static boolean cycleOnPlayerDeath;
    public static boolean rememberSurvivalPosition;
    public static boolean specNoPlayer;
    public static String dismountMode;
    public static boolean enableBypasses;
    public static boolean inventoryBlockSupport;
    public static boolean cycleStopOnDC;

    public static void loadConfig() {
        File configFile = new File(Spectator.instance.getDataFolder(), "config.yml");
        FileConfiguration config;
        try {
            if (!configFile.exists()) {
                config = new YamlConfiguration();
                config.save(configFile);
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            config.set("Spectator.MirrorInventory", mirrorInventory = config.getBoolean("Spectator.MirrorInventory", true));
            config.set("Spectator.HideFromTab", hideFromTab = config.getBoolean("Spectator.HideFromTab", false));
            config.set("Spectator.CycleOnPlayerDeath", cycleOnPlayerDeath = config.getBoolean("Spectator.CycleOnPlayerDeath", false));
            config.set("Spectator.RememberSurvivalPosition", rememberSurvivalPosition = config.getBoolean("Spectator.RememberSurvivalPosition", true));
	        config.set("Spectator.OnlySpecPlayers", specNoPlayer = config.getBoolean("Spectator.OnlySpecPlayers", false));
            config.set("Spectator.DismountMode", dismountMode = config.getString("Spectator.DismountMode", "default"));
            config.set("Spectator.EnableBypasses", enableBypasses = config.getBoolean("Spectator.EnableBypasses", true));
            config.set("Spectator.SupportInventoryBlocks", inventoryBlockSupport = config.getBoolean("Spectator.SupportInventoryBlocks", true));
            config.set("Spectator.StopCycleOnDisconnect", cycleStopOnDC = config.getBoolean("Spectator.StopCycleOnDisconnect", false));
            config.options().header("MirrorInventory: Whether spectators should be able to see player inventories.\nHideFromTab: Whether spectators should be hidden from the tab menu.\nCycleOnPlayerDeath: Whether spectators should cycle between players when the player that they were spectating dies.\nRememberSurvivalPosition: Whether the survival position of the spectator should be remembered for when they unspec.\nOnlySpecPlayers: Whether players should be able to use /spec to enter spectator mode without being constrained to follow a player.\nDismountMode: What hitting the dismount key while spectating a player should do. Options are: default (the default behaviour), lock (keep players from dismounting), and unspec (unspec the spectator).\nEnableBypasses: WIP option to enable/disable bypasses. Right now it can be set to false as a way to spectate operators without a permission manager.");
            config.save(configFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveConfig() {
        File configFile = new File(Spectator.instance.getDataFolder(), "config.yml");
        FileConfiguration config;
        try {
            if (!configFile.exists()) {
                loadConfig();
            }
            config = YamlConfiguration.loadConfiguration(configFile);

            config.set("Spectator.MirrorInventory", mirrorInventory);
            config.set("Spectator.HideFromTab", hideFromTab);
            config.set("Spectator.CycleOnPlayerDeath", cycleOnPlayerDeath);
            config.set("Spectator.RememberSurvivalPosition", rememberSurvivalPosition);
            config.set("Spectator.OnlySpecPlayers", specNoPlayer);
            config.set("Spectator.DismountMode", dismountMode);
            config.set("Spectator.EnableBypasses", enableBypasses);
            config.set("Spectator.SupportInventoryBlocks", inventoryBlockSupport);
            config.set("Spectator.StopCycleOnDisconnect", cycleStopOnDC);

            config.save(configFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
