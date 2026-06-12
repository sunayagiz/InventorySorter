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

## Decision 3: v1.1.0 Networking Requirement
**Date:** 2026-06-12
**Status:** Planned
**Context:** Client-side only sorting can cause desync on multiplayer servers.
**Decision:** Future updates (v1.1.0+) must move core sorting logic to the server side using custom packets (C2S) to ensure server-side authority.
**Impact:** Will require a server-side mod component; improves multiplayer stability.
