package com.xcue.xcuelib.utils;

import com.xcue.xcuelib.compatibility.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class PlayerUtils {

    public static UUID getOfflineUUID(String name) {
        UUID uuid = null;
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName().equals(name)) {
                uuid = offlinePlayer.getUniqueId();
            }
        }
        return uuid;
    }

    public static void giveItem(Player player, ItemStack item, String message) {
        if (player == null || !player.isOnline() || item == null) {
            return;
        }

        Map<Integer, ItemStack> leftover = player.getInventory().addItem(item);
        if (!leftover.isEmpty()) {
            if (message != null && !message.isEmpty())
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

            leftover.values().forEach(it -> player.getWorld().dropItemNaturally(player.getLocation(), it));
        }
    }

    public static ItemStack getHeldItem(Player player) {
        return ServerVersion.isServerVersionAbove(ServerVersion.V1_8) ? player.getInventory().getItemInMainHand() :
                player.getInventory().getItemInHand();
    }

    public static ItemStack getOffHeldItem(Player player) {
        return ServerVersion.isServerVersionAbove(ServerVersion.V1_8) ? player.getInventory().getItemInOffHand() :
                player.getInventory().getItemInHand();
    }
}
