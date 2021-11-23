package com.kosakorner.spectator.player;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Store player attributes while spectating.
 */
public class PlayerAttributes {

    private GameMode gameMode;
    private Location location;

    /**
     * Likely initialization.
     * @param player Likely the player to create an instance of the class for.
     */
    public PlayerAttributes(Player player) {
        gameMode = player.getGameMode();
        location = player.getLocation();
    }

    /**
     * Get the gamemode player attribute.
     * @return The gamemode player attribute.
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * Get the location player attribute
     * @return The location player attribute.
     */
    public Location getLocation() {
        return location;
    }

}
