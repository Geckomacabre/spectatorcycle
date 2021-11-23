package com.kosakorner.spectator.cycle;

import com.kosakorner.spectator.Spectator;
import com.kosakorner.spectator.config.Permissions;
import com.kosakorner.spectator.player.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Cycle {

    private Player owner;
    private Player last;
    private List<Player> alreadyVisited = new ArrayList<>();
    private List<Player> toVisit = new ArrayList<>();

    private Random random = new Random();

    public Cycle(Player owner, Player last) {
        this.owner = owner;
        this.last = last;
    }

    public boolean hasNextPlayer() {
        return alreadyVisited.size() != toVisit.size();
    }

    public Player getLastPlayer() {
        return last;
    }

    public Player getNextPlayer(boolean respectAFK, boolean over1) {
        updateLists();
        if (over1 == false) {
            last = null;
        }
        if (toVisit.size() == 0) {
            return null;
        }
        if (toVisit.size() == 1) {
            return toVisit.get(0);
        }
        Player player = toVisit.get(random.nextInt(toVisit.size()));
        if (player.equals(last)) {
            return getNextPlayer(respectAFK, over1);
        }
        if (respectAFK) {
            if (PlayerListener.afkHashMap.get(player).before(new Timestamp(System.currentTimeMillis() - 5000))) {
                return getNextPlayer(true, over1);
            }
        }

        last = player;


        if (over1 == true) {
            alreadyVisited.add(player);
        }

        return player;




    }

    private void updateLists() {
        List<Player> toRemove = new ArrayList<>();
        toVisit = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player player : toVisit) {
            if (Spectator.hasPermission(player, Permissions.BYPASS_VIEWABLE)) {
                toRemove.add(player);
            }
        }
        toVisit.removeAll(toRemove);
        // Clear the toVisit list of players that have been visited.
        for (Player player : alreadyVisited) {
            if (!player.isOnline()) {
                alreadyVisited.remove(player);
            }
            toVisit.remove(player);
        }
        toVisit.remove(owner);
        for (Player player : Spectator.trackedSpectators) {
            toVisit.remove(player);
        }
    }

}
