# Architectural Decisions Record (ADR)

## Decision 1: Transaction-Based Sorting
**Date:** 2026-06-12
**Status:** Implemented
**Context:** Standard sorting often clears slots and refills them. If the game crashes mid-process, items are lost.
**Decision:** We implemented a "Safe Sort" pattern: items are copied to a list, verified, and only then is the inventory cleared and refilled.
**Impact:** Significantly higher data safety; slightly more memory usage during the sort operation (negligible for 27 slots).

## Decision 2: Smart Button Context Detection
**Date:** 2026-06-12
**Status:** Implemented
**Context:** A single button needs to work for both player inventories and chests.
**Decision:** Use a Mixin on `HandledScreen` that checks the active `ScreenHandler`. It dynamically identifies if it should sort slots 9-35 (Player) or 0-N (Container).
**Impact:** Intuitive UX; prevents sorting the player's pockets when they intended to sort a chest.

## Decision 3: Server-Side Authority (Networking)
**Date:** 2026-06-12
**Status:** Implemented
**Context:** Client-side only sorting causes items to revert (desync) because the server is not aware of the slot changes.
**Decision:** We implemented a C2S (Client-to-Server) packet using Fabric Networking API. The client sends a `SortPayload`, and the server performs the actual `SortingLogic.sort()`.
**Impact:** 100% synchronization on both single-player and multiplayer; eliminates the "reverting items" bug.
