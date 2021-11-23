package com.kosakorner.spectator.player;

import com.kosakorner.spectator.Spectator;
import com.kosakorner.spectator.config.Config;
import com.kosakorner.spectator.config.Messages;
import com.kosakorner.spectator.config.Permissions;
import com.kosakorner.spectator.cycle.CycleHandler;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.*;

import java.sql.Timestamp;
import java.util.*;




@SuppressWarnings("unused")
public class PlayerListener implements Listener {

    private final Set<Player> hiddenPlayers = new HashSet<>();
    private final Map<Player, PlayerAttributes> attributesCache = new HashMap<>();
    private Map<Player, Player> prevMap = null;
    public PlayerListener() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Spectator.instance, new Runnable() {
            Player prevTarget = null;
            @Override
            public void run() {
                for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
                    final Player player = entry.getKey();
                    final Player target = entry.getValue();

                    if (!player.getWorld().equals(target.getWorld()) || player.getLocation().distanceSquared(target.getLocation()) > 1) {
                    //if (!player.getSpectatorTarget().equals(target)) {


                        if (isSneaking == true || !player.getWorld().equals(target.getWorld())) {
                            Bukkit.getScheduler().runTaskLater(Spectator.instance, new Runnable() {
                                @Override
                                public void run() {
                                    player.setSpectatorTarget(null);
                                    player.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                    player.setSpectatorTarget(target);
                                }
                            }, 5);
                        }
                    }
                }
            }
        }, 0, 20);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Spectator.instance, new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
                    InventoryHandler.resendInventory(entry.getValue(), entry.getKey());
                }
            }
        }, 0, 15);
    }

    /**
     *
     * @param player This is the player that should enter spectator mode.
     * @param target This is the target player.
     */
    public void spectatePlayer(final Player player, final Player target) {
        if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
            attributesCache.put(player, new PlayerAttributes(player));
        }
        player.setGameMode(GameMode.SPECTATOR);
        if (!Spectator.trackedSpectators.contains(player)) {
            Spectator.trackedSpectators.add(player);
        }
        if (Config.hideFromTab) {
            updatePlayerVisibility(player, false);
        }
        if (target != null) {
            if (Spectator.hasPermission(player, Permissions.INVENTORY)) {
                InventoryHandler.restoreInventory(player);
                InventoryHandler.mirrorInventory(player, target);
            }
            player.sendTitle(target.getName(), null, 10, 70, 20);
            player.setSpectatorTarget(null);
            player.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN);
            Bukkit.getScheduler().runTaskLater(Spectator.instance, new Runnable() {
                @Override
                public void run() {
                    player.setSpectatorTarget(target);
                    Spectator.spectatorRelations.remove(player);
                    Spectator.spectatorRelations.put(player, target);
                }
            }, 5);
        }
    }

    /**
     *
     * @param player This is the player that should exit spectate mode, NOT the target player.
     */
    @SuppressWarnings("deprecation")
    public void unspectatePlayer(final Player player) {
        // Check if the location is safe.
        Location location = null;
        if (Config.rememberSurvivalPosition) {
            location = attributesCache.get(player).getLocation();
        }
        if (location == null) {
            location = player.getLocation();
            float pitch = location.getPitch();
            float yaw = location.getYaw();
            if (!location.getBlock().getType().equals(Material.AIR) || !player.isOnGround()) {
                location = location.getWorld().getHighestBlockAt(location).getLocation();
                location.setPitch(pitch);
                location.setYaw(yaw);
            }
        }
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        Spectator.trackedSpectators.remove(player);
        Spectator.spectatorRelations.remove(player);
        if (Spectator.hasPermission(player, Permissions.INVENTORY)) {
            InventoryHandler.restoreInventory(player);
        }
        if (Config.hideFromTab) {
            updatePlayerVisibility(player, true);
        }
        GameMode gameMode = attributesCache.get(player).getGameMode();
        attributesCache.remove(player);
        player.setGameMode(gameMode);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    @SuppressWarnings("deprecation")
	public void updatePlayerVisibility(Player player, boolean show) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.getUniqueId().equals(player.getUniqueId())) {
                if (!Spectator.hasPermission(target, Permissions.BYPASS_TABLIST)) {
                    if (show) {
                        hiddenPlayers.remove(player);
                        target.showPlayer(player);
                    }
                    else {
                        hiddenPlayers.add(player);
                        target.hidePlayer(player);
                    }
                }
            }
        }
    }

    /**
     This handles players disconnecting.
     */
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!Spectator.hasPermission(player, Permissions.BYPASS_TABLIST)) {
            for (Player hidden : hiddenPlayers) {
                player.hidePlayer(hidden);
            }
        }
        if (!Config.cycleStopOnDC) {
            if (Spectator.cyclingSpectatorsInfo.containsKey(player.getUniqueId())) {
                Spectator.cycleHandler.startCycle(player, Spectator.cyclingSpectatorsInfo.get(player.getUniqueId()));
            }
        }


    }

    /**
    This handles players quitting, pretty self explanatory.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (Spectator.trackedSpectators.contains(player)) {
            unspectatePlayer(player);
            if (!Config.cycleStopOnDC) {
                //Spectator.cyclingSpectatorsInfo.put(player, 1);

                //Eh.. I could leave this blank..

            }
            else {
                //Nah, feel like doing nothing would make sense in this case.
                //Good Code (TM)
            }
        }
        for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
            if (entry.getValue().equals(player)) {
                Player spectator = entry.getKey();
                if (!Spectator.cycleHandler.isPlayerCycling(spectator)) {
                    dismountTarget(spectator);
                }
                else {
                    Spectator.cycleHandler.startCycle(spectator, 1500);
                }
            }
        }
    }

    /**
     * This handles player deaths.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Config.cycleOnPlayerDeath) {
            Player player = event.getEntity();
            for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
                if (entry.getValue().equals(player)) {
                    Player spectator = entry.getKey();
                    if (Spectator.cycleHandler.isPlayerCycling(spectator)) {
                        Spectator.cycleHandler.restartCycle(spectator);
                    }
                }
            }
        }
    }


    boolean isSneaking = false;

    /**
     * This handles when players perform the dismount operation. This would mean that the player could be trying to stop targeting a player while in spectator mode.
     */
    @EventHandler
    public void onPlayerDismount(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!Spectator.cycleHandler.isPlayerCycling(player)) {
            // Only capture the button down event.
            if (event.isSneaking()) {
                dismountTarget(player);
                isSneaking = true;
            }
        }
        else {
            if (event.isSneaking()) {
                player.sendMessage(Messages.translate("Messages.Spectate.CycleNoDismount"));
            }
            event.setCancelled(true);
        }
    }

    /**
     * This checks and acts on the dismountMode setting. It is used in onPlayerDismount, however can be called from elsewhere.
     * @param player The spectator to be affected.
     */
    public void dismountTarget(final Player player) {
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            //if (Config.specNoPlayer == false) {
            if (Config.dismountMode.equals("default")) {
                if (player.getSpectatorTarget() != null && player.getSpectatorTarget().getType().equals(EntityType.PLAYER)) {
                    if (Spectator.hasPermission(player, Permissions.INVENTORY)) {
                        InventoryHandler.restoreInventory(player);
                    }
                    Spectator.spectatorRelations.remove(player);
                    player.setSpectatorTarget(player);
                }
            }
            else if (Config.dismountMode.equals("lock")) {
                //if (player.getSpectatorTarget() != null && player.getSpectatorTarget().getType().equals(EntityType.PLAYER)) {
                Bukkit.getScheduler().runTaskLater(Spectator.instance, new Runnable() {
                    @Override
                    public void run() {
                        player.setSpectatorTarget(Spectator.spectatorRelations.get(player));
                    }
                }, 5);

                //}
            }
            else if (Config.dismountMode.equals("unspec")) {
                unspectatePlayer(player);
            }
        }
    }


    /**
     * This is called when a player changes gamemode. If the player is in spectator mode and tries to switch to another gamemode, the switch is blocked.
     */
    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (Spectator.trackedSpectators.contains(player)) {
            player.sendMessage(Messages.translate("Messages.Player.GameModeBlocked"));
            event.setCancelled(true);
        }
    }

    /**
     * This is called when a player tries to interact with any inventory (In the form of a click). If they are in spectator mode this interaction is blocked.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getGameMode().equals(GameMode.SPECTATOR)) {
            event.setCancelled(true);
        }
    }

    /**
     * This is called when a player tries to interact with any inventory (In the form of a drag). If they are in spectator mode this interaction is blocked.
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked().getGameMode().equals(GameMode.SPECTATOR)) {
            event.setCancelled(true);
        }
    }

    /**
     * Restores all spectators to their pre-spectate position.
     */
    public void restoreAllSpectators() {
        for (Player player : Spectator.trackedSpectators) {
            unspectatePlayer(player);
        }
    }



    public static HashMap<Player, Timestamp> afkHashMap = new HashMap<Player, Timestamp>();
    public static HashMap<Player, Inventory> inventoryHashMap = new HashMap<Player, Inventory>();

    /**
     * This is called when a player moves. It is currently used to flag them as not AFK.
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent e) {
        afkHashMap.put(e.getPlayer(), new Timestamp(System.currentTimeMillis()));
    }

    /**
     * This is called when a player chats. It is currently used to flag them as not AFK.
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        afkHashMap.put(e.getPlayer(), new Timestamp(System.currentTimeMillis()));
    }

    /**
     * This is called when a player interacts with something. It is currently used to flag them as not AFK.
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent e) {
        afkHashMap.put(e.getPlayer(), new Timestamp(System.currentTimeMillis()));
    }

    /**
     * This is called when a player clicks something in an inventory. It is currently used to flag them as not AFK.
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerInventory(InventoryClickEvent e) {
        afkHashMap.put((Player) e.getWhoClicked(), new Timestamp(System.currentTimeMillis()));
    }

    /**
     * This is called when a player logs in. It is currently used to flag them as not AFK.
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent e) {
        afkHashMap.put(e.getPlayer(), new Timestamp(System.currentTimeMillis()));
    }

    /**
     * This is called when a player opens their inventory. It is currently used to flag them as not AFK.
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onOpenInventory(InventoryOpenEvent e) {
        if (Config.inventoryBlockSupport) {
            inventoryHashMap.put((Player) e.getPlayer(), e.getInventory());
            for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
                if (entry.getValue().equals(e.getPlayer())) {
                    entry.getKey().openInventory(e.getInventory());
                }
            }
        }


    }

    /**
     * This is called when a player closes their inventory. It is currently used to flag them as not AFK.
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onCloseInventory(InventoryCloseEvent e) {
        if (Config.inventoryBlockSupport) {
            inventoryHashMap.put((Player) e.getPlayer(), e.getInventory());
            for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
                if (entry.getValue().equals(e.getPlayer())) {
                    entry.getKey().closeInventory();
                }
            }
        }


    }

    /**
     * This is called when a player disconnects. It is currently used to remove them from any spectate cycles that they might be a part of to avoid errors later on.
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        if (Spectator.cycleHandler.isPlayerCycling(e.getPlayer())) {
            Spectator.cycleHandler.stopCycle(e.getPlayer());
        }


    }



}
