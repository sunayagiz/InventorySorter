package com.sunay.inventorysorter;

import net.minecraft.entity.player.PlayerEntity;
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
     * Generic sort method for any screen handler and slot range.
     * Performs sorting on the server side to prevent desync.
     */
    public static void sort(PlayerEntity player, ScreenHandler handler, int start, int end) {
        if (handler == null || player == null) return;

        // Creative Mode Deletion Fix: Disable sorting or handle safely in creative
        if (player.isCreative()) {
            LOGGER.info("Sorting disabled in Creative Mode to prevent item deletion.");
            return;
        }

        LOGGER.info("Sorting slots {}-{} in handler {} for player {}", start, end, handler.getClass().getSimpleName(), player.getName().getString());
        
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
        
        // Ensure changes are synced to the client
        handler.sendContentUpdates();
    }
}
