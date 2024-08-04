package com.xcue.xcuelib.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryHandler implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof GUI) {
            event.setCancelled(true);
            if (event.getClickedInventory() == event.getInventory())
                ((GUI) event.getInventory().getHolder()).onInventoryClick(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof GUI) {
            if (event.getInventory() == event.getInventory())
                ((GUI) event.getInventory().getHolder()).onInventoryClose(event);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof GUI) {
            if (event.getInventory() == event.getInventory())
                ((GUI) event.getInventory().getHolder()).onInventoryOpen(event);
        }
    }
}
