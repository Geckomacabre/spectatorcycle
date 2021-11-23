package com.kosakorner.spectator.cycle;

import com.kosakorner.spectator.Spectator;
import com.kosakorner.spectator.config.Messages;
import com.kosakorner.spectator.player.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class CycleHandler {

    private Map<Player, CycleTask> cycleTasks = new HashMap<>();
    private Map<Player, Cycle> playerCycles = new HashMap<>();
    private Player lastPlayer = null;


    /**
     * Basic check to see if the player is cycling or not.
     * @param player The player to check the status of.
     * @return A Boolean based on whether the player is in a spectate cycle.
     */
    public boolean isPlayerCycling(Player player) {
        return playerCycles.containsKey(player);
    }

    /**
     * This starts a player in a spectate cycle given the amount of ticks between transitions.
     * @param player This is the player that should enter spectator mode in a spectate cycle.
     * @param ticks This is the amount of game ticks that should be allocated to each player in the cycle. There are 20 game ticks in a second. (Ideally. This might not always be true if the server is lagging.)
     */
    public void startCycle(final Player player, int ticks) {
        playerCycles.put(player, new Cycle(player, null));
        Spectator.cyclingSpectatorsInfo.put(player.getUniqueId(), ticks);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Spectator.instance, new Runnable() {
            @Override
            public void run() {
                Cycle cycle = playerCycles.get(player);
                if (!cycle.hasNextPlayer()) {
                    Player last = cycle.getLastPlayer();
                    cycle = new Cycle(player, last);
                    playerCycles.put(player, cycle);
                }
                boolean respectAFK = false;
                int counter = 0;
                for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
                    if (PlayerListener.afkHashMap.get(currentPlayer).after(new Timestamp(System.currentTimeMillis()-5000)) && !currentPlayer.equals(player)){
                        respectAFK = true;
                        counter++;
                        break;
                    }
                }
                Player next = cycle.getNextPlayer(respectAFK, (counter>1));
                if (next != null) {
                    if (!next.equals(lastPlayer)) {
                        Spectator.playerListener.spectatePlayer(player, next);
                        lastPlayer = next;
                    }
                }
                else {
                    Spectator.playerListener.spectatePlayer(player, player);
                    player.teleport(new Location(player.getWorld(), 169, 83, -258, 64, 18));
                    //Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "title " + player.getDisplayName() +" title " + "'No Players to Spectate'");
                    //System.out.println("/title " + player.getDisplayName() +" title " + "'No Players to Spectate'");
                    player.sendTitle("No Players Online", "Join now!", 10, 70, 20);
                }
            }
        }, 0, ticks);
        cycleTasks.put(player, new CycleTask(task, ticks));
    }


    /**
     * Stops any ongoing spectate cycles for a given player.
     * @param player The player to stop the spectate cycle of.
     */
    public void stopCycle(Player player) {
        cycleTasks.get(player).getTask().cancel();
        cycleTasks.remove(player);
        playerCycles.remove(player);
        Spectator.playerListener.dismountTarget(player);
        player.sendMessage(Messages.translate("Messages.Spectate.CycleStop"));
    }

    /**
     * Restarts any ongoing spectate cycles for a given player.
     * @param player The player to restart the spectate cycle of.
     */
    public void restartCycle(Player player) {
        CycleTask task = cycleTasks.get(player);
        task.getTask().cancel();
        cycleTasks.remove(player);
        startCycle(player, task.getInterval());
    }

    private class CycleTask {

        private BukkitTask task;
        private int interval;

        /**
         * As of current, I have no idea what this does or what its purpose is.
         * @param task Unknown
         * @param interval Unknown. Possibly the spectate cycle interval.
         */
        public CycleTask(BukkitTask task, int interval) {
            this.task = task;
            this.interval = interval;
        }
        /**
         * As of current, I have no idea what this does or what its purpose is.
         * @param task
         * @param interval
         */
        public BukkitTask getTask() {
            return task;
        }
        /**
         * As of current, I have no idea what this does or what its purpose is.
         * @param task
         * @param interval
         */
        public int getInterval() {
            return interval;
        }

    }

}
