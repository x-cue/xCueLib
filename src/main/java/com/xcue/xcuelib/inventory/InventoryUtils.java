package com.xcue.xcuelib.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class InventoryUtils {
    /**
     * @param inventory Any inventory
     * @param item ItemStack with data to match
     * @return Exact amount of the specified material the inventory can receive
     */
    public static int getAvailableSpace(@NonNull Inventory inventory, @NonNull ItemStack item) {
        int freeSpace = 0;

        for (ItemStack slot : inventory.getStorageContents()) {

            if (slot == null) {
                freeSpace += item.getMaxStackSize();
            } else if (slot.isSimilar(item)) {
                freeSpace += item.getMaxStackSize() - slot.getAmount();
            }
        }

        return freeSpace;
    }

    /**
     * @param inventory Any inventory
     * @return Amount of empty slots
     */
    public static int getEmptySlots(Inventory inventory) {
        int emptySlots = 0;

        for (ItemStack item : inventory.getStorageContents()) {
            if (item == null) {
                emptySlots++;
            }
        }

        return emptySlots;
    }
}

