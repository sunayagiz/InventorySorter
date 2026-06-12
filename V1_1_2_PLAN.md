# v1.1.2 "Bulletproof" Update Plan

## Step 1: Core Security & Server Validation (DONE)
- **Task 1A: Bounds Guard (DoS Prevention)** -> Validate `startSlot` and `endSlot` on the server. Clamp values to `handler.slots.size() - 1`, ensure `startSlot <= endSlot`, and limit the maximum sort range to 150 slots to prevent thread hanging.
- **Task 1B: Dupe Guard (Exploit Prevention)** -> In `SortingLogic.java`, skip extraction/insertion for any slot that is an output slot (e.g., `CraftingResultSlot`) or where `slot.canInsert()` / `slot.canTakeItems()` is false.
- **Task 1C: Rate Limiting (Spam Prevention)** -> Implement a 500ms cooldown per player `UUID` in `ModNetworking.java` to drop spam packets.

## Step 2: Client-Side Creative & UX Fixes (DONE)
- **Task 2A: Creative Tab Context** -> In `HandledScreenMixin`, only allow `sortActiveInventory()` if the active Creative tab is the 'Survival Inventory'.
- **Task 2B: Asset Fallback** -> Implement a primitive drawing fallback in `renderWidget` if `sort_button.png` fails to bind.

## Step 3: Deployment & Polish (DONE)
- **Task 3A: Modular Dependencies** -> Refine `fabric.mod.json` to use exact API modules.
- **Task 3B: Version Bump & Release** -> Update `gradle.properties`, `CHANGELOG.md`, and deploy to the Modrinth folder.
