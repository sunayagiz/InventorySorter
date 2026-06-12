package com.sunay.inventorysorter;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortingLogic {
    /**
     * Sorts the main 27 slots of the player's inventory (slots 9-35) alphabetically by item name.
     */
    public static void sortInventory(PlayerInventory inventory) {
        List<ItemStack> stacks = new ArrayList<>();
        
        // Extract items from slots 9 to 35
        for (int i = 9; i <= 35; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                stacks.add(stack.copy());
            }
            inventory.setStack(i, ItemStack.EMPTY);
        }

        // Sort stacks alphabetically by their display name
        stacks.sort(Comparator.comparing(stack -> stack.getName().getString()));

        // Place sorted items back into slots 9 to 35
        for (int i = 0; i < stacks.size(); i++) {
            inventory.setStack(i + 9, stacks.get(i));
        }
    }
}
