# Creative Mode Edge-Case Report: Inventory Sorter

## 1. Executive Summary
The "Inventory Sorter" mod currently treats all tabs in the Creative Mode inventory as equivalent to the standard Survival Inventory. This leads to unexpected behavior where hidden inventories are sorted, and potential desync issues occur when using functional tabs like "Item Search".

## 2. Identified Edge Cases

### A. The 'Survival Inventory' vs. 'Item Search' Tab
*   **Problem:** In `HandledScreenMixin.java`, the mod hardcodes a slot range of 9-35 for any `CreativeInventoryScreen`.
*   **Technical Root Cause:**
    ```java
    if ((Object)this instanceof InventoryScreen || (Object)this instanceof CreativeInventoryScreen) {
        startSlot = 9;
        endSlot = 35;
    }
    ```
*   **Impact:**
    *   **In the Survival Tab (Creative):** Works as expected (sorts the player's 27 main inventory slots).
    *   **In the Search Tab (Creative):** The user sees search results. If they click "Sort", the mod sends a packet to sort slots 9-35. The server, seeing the player's `PlayerScreenHandler`, sorts the **actual survival inventory** which is currently hidden.
    *   **User Experience:** The user clicks sort expecting the search results to reorder, but instead, their hidden items are rearranged without any visual feedback in the current tab.

### B. Slot Range Invalidity (9-35)
*   **Problem:** Does the range 9-35 hold true for all tabs? **No.**
*   **Details:** 
    *   In the Creative Menu, the `CreativeScreenHandler` on the client-side dynamically re-maps slots.
    *   For most tabs, the player's main inventory (9-35) is **not present** in the container at all. Only the Hotbar (0-8) is consistently available (though its slot indices may shift to 45-53 depending on the tab).
    *   Sending a request to sort 9-35 while on a tab that only has 9 slots (or where 9-35 are "ghost" items from a category) is logically inconsistent.

### C. Server-Side Handler Mismatch
*   **Problem:** The server-side logic in `SortingLogic.java` relies on `handler instanceof PlayerScreenHandler` to allow creative sorting.
*   **Risk:** While this prevents sorting on inappropriate handlers, it allows sorting on the `PlayerScreenHandler` even when the client is using a `CreativeInventoryScreen` with a different tab open. This creates a state where the client and server are "looking" at different things but interacting with the same underlying data in a confusing way.

## 3. Desync and Visual Glitches
When `SortingLogic.sort` finishes, it calls `handler.sendContentUpdates()`.
*   If a player is in the Search tab, the server will send an update for the player's survival inventory.
*   This can cause the client to temporarily display survival items in the search grid or cause "ghosting" where items appear to be in one place but are actually in another until the tab is refreshed.

## 4. Recommendations
1.  **Tab Filtering:** Modify `HandledScreenMixin.java` to check the active tab.
    ```java
    if (this instanceof CreativeInventoryScreen creative) {
        if (creative.getSelectedTab() == ItemGroups.INVENTORY) {
            startSlot = 9;
            endSlot = 35;
        } else {
            // Disable sorting or target different slots
            return;
        }
    }
    ```
2.  **Visual Feedback:** If sorting is disabled for a specific tab, the "Sort" button should be hidden or grayed out to prevent user confusion.
3.  **Dynamic Slot Mapping:** Instead of hardcoding 9-35, the mod should identify slots that belong to the `PlayerInventory` specifically, regardless of their index in the current `ScreenHandler`.

## 5. Conclusion
The slot range 9-35 **does not** hold true for all Creative tabs. It is only valid for the 'Survival Inventory' tab. Sorting in other tabs leads to "invisible sorting" of the survival inventory, which is a UX bug and a potential source of client-server desync.
