package com.sunay.inventorysorter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortingLogic {
    private static final Logger LOGGER = LoggerFactory.getLogger(SortingLogic.class);

    private SortingLogic() {
        // Utility class
    }

    /**
     * Sorts the main 27 slots of the player's inventory (slots 9-35) alphabetically by item name.
     */
    public static void sortInventory(PlayerInventory inventory) {
        if (inventory != null && inventory.player != null) {
            sort(inventory.player.currentScreenHandler, 9, 35);
        }
    }

    /**
     * Generic sort method for any screen handler and slot range.
     * Handles client-side thread safety by ensuring execution on the main client thread.
     */
    public static void sort(ScreenHandler handler, int start, int end) {
        if (handler == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (!client.isOnThread()) {
            client.execute(() -> sort(handler, start, end));
            return;
        }

        LOGGER.info("Sorting slots {}-{} in handler {}", start, end, handler.getClass().getSimpleName());
        
        List<ItemStack> stacks = new ArrayList<>();

        // Extract items from specified slots
        for (int i = start; i <= end; i++) {
            if (i >= 0 && i < handler.slots.size()) {
                ItemStack stack = handler.getSlot(i).getStack();
                if (!stack.isEmpty()) {
                    stacks.add(stack.copy());
                    handler.getSlot(i).setStack(ItemStack.EMPTY);
                }
            }
        }

        // Sort stacks alphabetically by their display name
        stacks.sort(Comparator.comparing(stack -> stack.getName().getString()));

        // Place sorted items back
        for (int i = 0; i < stacks.size(); i++) {
            int slotId = i + start;
            if (slotId <= end && slotId < handler.slots.size()) {
                handler.getSlot(slotId).setStack(stacks.get(i));
            }
        }
    }
}
