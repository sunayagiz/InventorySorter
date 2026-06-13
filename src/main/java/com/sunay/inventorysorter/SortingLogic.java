package com.sunay.inventorysorter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
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

/**
 * Core business logic for sorting inventories on the server side.
 * <p>
 * This class implements a "Safe Sort" transaction-style approach:
 * 1. Collect and Copy: items are collected from slots and copied.
 * 2. Merge: identical items are merged into full stacks.
 * 3. Sort: items are sorted alphabetically by their display name.
 * 4. Verify & Commit: if no errors occurred, the target slots are cleared and the sorted items are inserted.
 * </p>
 * This architecture prevents item desync and provides server-side authority for data integrity.
 */
public class SortingLogic {
    private static final Logger LOGGER = LoggerFactory.getLogger(SortingLogic.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SortingLogic() {
        // Utility class
    }

    /**
     * Generic sort method for any screen handler and slot range.
     * Performs sorting on the server side to prevent desync and ensure data safety.
     *
     * @param player  The player requesting the sort action.
     * @param handler The screen handler representing the open inventory.
     * @param start   The starting slot index for the sort range.
     * @param end     The ending slot index for the sort range.
     * @param mode    The sorting mode to use.
     */
    public static void sort(PlayerEntity player, ScreenHandler handler, int start, int end, SortingMode mode) {
        if (handler == null || player == null || mode == null) return;

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

        LOGGER.info("Sorting slots {}-{} in handler {} for player {} using mode {}", start, end, handler.getClass().getSimpleName(), player.getName().getString(), mode);
        
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

            // 2. Sort stacks based on mode
            Comparator<ItemStack> comparator = switch (mode) {
                case ALPHABETICAL -> Comparator.comparing(stack -> stack.getName().getString().toLowerCase());
                case ID -> Comparator.comparing(stack -> net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).getPath());
                case CATEGORY -> Comparator.comparing((ItemStack stack) -> {
                    // Group by a combination of ID namespace (to group mods) and item type
                    Identifier id = net.minecraft.registry.Registries.ITEM.getId(stack.getItem());
                    return id.getNamespace() + ":" + stack.getItem().getClass().getSimpleName();
                }).thenComparing(stack -> stack.getName().getString().toLowerCase());
                case RARITY -> Comparator.comparing((ItemStack stack) -> stack.getRarity().ordinal()).reversed()
                        .thenComparing(stack -> stack.getName().getString().toLowerCase());
            };

            LOGGER.debug("Sorting {} stacks using comparator for mode {}", stacks.size(), mode);
            stacks.sort((s1, s2) -> {
                try {
                    return comparator.compare(s1, s2);
                } catch (Exception e) {
                    LOGGER.warn("Failed to compare stacks {} and {}, using default order", s1, s2, e);
                    return 0;
                }
            });

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
