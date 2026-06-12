# Inventory Sorter - Gemini Instructions

## Project Overview
**Inventory Sorter** is a client-side Minecraft mod for **Fabric 1.21**. It provides high-safety inventory sorting via keybindings and a smart UI button.

- **Tech Stack:** Java 21, Fabric Loader 1.21, Gradle 8.10.
- **Key Features:**
  - **Safe Sorting:** Uses a transaction-style approach (Copy -> Sort -> Verify -> Clear -> Refill) to prevent item loss.
  - **Smart UI:** Detects if the player is looking at their own inventory or a container (chest) and targets the correct slots.
  - **Internationalization:** All UI strings are localized (default: English).

## Architecture
- **`com.sunay.inventorysorter.InventorySorter`:** Main entrypoint (Common).
- **`com.sunay.inventorysorter.InventorySorterClient`:** Client entrypoint; handles keybindings (Default: **R**).
- **`com.sunay.inventorysorter.SortingLogic`:** Core business logic. Implements "Safe Sort" and stack merging.
- **`com.sunay.inventorysorter.mixin.HandledScreenMixin`:** Injects the "Sort" button (S) and handles context-aware sorting.

## Building and Running
- **Generate JAR:** `./gradlew build`
- **Dev Environment:** `./gradlew runClient`
- **Clean:** `./gradlew clean`

## Development Conventions
- **Data Safety:** Never modify inventories directly without a backup/verify step.
- **Localization:** Use `en_us.json` for all UI text. Access via `Text.translatable()`.
- **Git:** Use atomic commits with `type: message` format. Push to `main` frequently.
- **Roadmap:** Consult `ROADMAP.md` and `DECISIONS.md` before implementing large features.
