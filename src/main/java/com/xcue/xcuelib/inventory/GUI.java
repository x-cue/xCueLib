package com.xcue.xcuelib.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface GUI extends InventoryHolder {

    void onInventoryClick(InventoryClickEvent e);

    void onInventoryOpen(InventoryOpenEvent e);

    void onInventoryClose(InventoryCloseEvent e);

    void addContent(Inventory inv);
}
