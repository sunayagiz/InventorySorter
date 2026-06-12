package com.sunay.inventorysorter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.CraftingResultSlot;
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

        // Bounds checks and DoS prevention
        if (start < 0 || end < 0) return;
        if (end >= handler.slots.size()) {
            end = handler.slots.size() - 1;
        }
        if (start > end) return;
        if (end - start > 150) {
            LOGGER.warn("Blocked DoS-prone sort attempt: {}-{} (size: {}) from player {}", start, end, end - start, player.getName().getString());
            return;
        }

        // Creative Mode Logic: Allow sorting in Creative if it's safe
        if (player.isCreative()) {
            if (handler instanceof GenericContainerScreenHandler) {
                // Sorting containers (chests) is always safe in creative
                LOGGER.info("Sorting container in Creative Mode.");
            } else if (handler instanceof PlayerScreenHandler) {
                // For player inventory in creative, only allow the main 27 slots (9-35)
                if (start < 9 || end > 35) {
                    LOGGER.warn("Blocked unsafe creative sort attempt on player inventory: {}-{}", start, end);
                    return;
                }
                LOGGER.info("Sorting player inventory in Creative Mode.");
            } else {
                LOGGER.info("Sorting disabled in Creative Mode for {} to prevent item deletion.", handler.getClass().getSimpleName());
                return;
            }
        }

        LOGGER.info("Sorting slots {}-{} in handler {} for player {}", start, end, handler.getClass().getSimpleName(), player.getName().getString());
        
        List<ItemStack> stacks = new ArrayList<>();
        List<Integer> validSlotIndices = new ArrayList<>();
        
        try {
            // 1. Collect items and verify they can be copied
            for (int i = start; i <= end; i++) {
                if (i >= 0 && i < handler.slots.size()) {
                    Slot slot = handler.getSlot(i);
                    if (slot == null) continue;
                    
                    // Safety check: skip slots that don't allow taking, are output-only, or are results
                    if (!slot.canTakeItems(player) || slot instanceof CraftingResultSlot || !slot.canInsert(ItemStack.EMPTY)) {
                        continue;
                    }

                    validSlotIndices.add(i);
                    ItemStack stack = slot.getStack();
                    if (stack != null && !stack.isEmpty()) {
                        // Copy the stack. If this fails, we haven't modified the inventory yet.
                        stacks.add(stack.copy());
                    }
                }
            }

            // 1.5. Merge stacks
            List<ItemStack> mergedStacks = new ArrayList<>();
            for (ItemStack stack : stacks) {
                boolean fullyMerged = false;
                for (ItemStack existing : mergedStacks) {
                    if (ItemStack.areItemsAndComponentsEqual(stack, existing)) {
                        int transferAmount = Math.min(stack.getCount(), existing.getMaxCount() - existing.getCount());
                        if (transferAmount > 0) {
                            existing.increment(transferAmount);
                            stack.decrement(transferAmount);
                            if (stack.isEmpty()) {
                                fullyMerged = true;
                                break;
                            }
                        }
                    }
                }
                if (!fullyMerged && !stack.isEmpty()) {
                    mergedStacks.add(stack);
                }
            }
            stacks = mergedStacks;

            // 2. Sort stacks. Use a safe name retrieval to avoid crashes on malformed NBT.
            stacks.sort(Comparator.comparing(stack -> {
                try {
                    return stack.getName().getString().toLowerCase();
                } catch (Exception e) {
                    LOGGER.warn("Failed to get name for stack, using empty string for sorting", e);
                    return "";
                }
            }));

            // 3. Transactional modification: only modify inventory if steps 1 & 2 succeeded
            // First, clear all valid slots in the range
            for (int i : validSlotIndices) {
                Slot slot = handler.getSlot(i);
                if (slot != null) {
                    slot.setStack(ItemStack.EMPTY);
                }
            }

            // Then, place sorted items back into the valid slots
            for (int i = 0; i < stacks.size(); i++) {
                int slotIndex = validSlotIndices.get(i);
                Slot slot = handler.getSlot(slotIndex);
                if (slot != null) {
                    slot.setStack(stacks.get(i));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to complete sorting transaction for player {}", player.getName().getString(), e);
            // If we caught an exception here, we might be in an inconsistent state if it happened 
            // during the modification phase (Step 3). However, Step 1 & 2 are safe.
            // In Minecraft, Slot.setStack is unlikely to throw.
            return;
        }
        
        // Ensure changes are synced to the client
        handler.sendContentUpdates();
    }
}
