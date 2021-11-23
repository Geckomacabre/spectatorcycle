package com.kosakorner.spectator.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class InventoryHandler {

    private static final Map<Player, ItemStack[]> inventories = new HashMap<>();
    private static final Map<Player, ItemStack[]> armorStacks = new HashMap<>();

    public static void mirrorInventory(Player player, Player target) {
        restoreInventory(player);
        inventories.put(player, player.getInventory().getContents());
        armorStacks.put(player, player.getInventory().getArmorContents());
        player.getInventory().clear();
        player.getInventory().setContents(target.getInventory().getContents());
        player.getInventory().setArmorContents(target.getInventory().getArmorContents());
        player.updateInventory();
    }

    public static void resendInventory(Player player, Player target) {
        target.getInventory().clear();
        target.getInventory().setContents(player.getInventory().getContents());
        target.getInventory().setArmorContents(player.getInventory().getArmorContents());
        target.updateInventory();
    }

    public static void restoreInventory(Player player) {
        if (inventories.containsKey(player)) {
            PlayerInventory inventory = player.getInventory();
            inventory.clear();
            ItemStack[] items = inventories.get(player);
            inventories.remove(player);
            ItemStack[] armor = armorStacks.get(player);
            armorStacks.remove(player);
            if (items != null) {
                inventory.setContents(items);
            }
            if (armor != null) {
                inventory.setArmorContents(armor);
            }
            player.updateInventory();
        }
    }

    public static void restoreAllInventories() {
        for (Player player : inventories.keySet()) {
            if (player.isOnline()) {
                restoreInventory(player);
            }
        }
    }

}
