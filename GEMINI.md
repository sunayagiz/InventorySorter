# Inventory Sorter - Gemini Instructions

## Project Overview
**Inventory Sorter** is a client-side Minecraft mod for **Fabric 1.21**. It provides high-safety inventory sorting via keybindings and a smart UI button.

- **Tech Stack:** Java 21, Fabric Loader 1.21, Gradle 8.10.
- **Key Features:**
  - **Safe Sorting:** Uses a transaction-style approach (Copy -> Sort -> Verify -> Clear -> Refill) performed on the **Server Side** via custom networking packets to prevent desync.
  - **Stack Merging:** Automatically merges identical items into full stacks before sorting.
  - **Dual Sorting:** Sorting from a container screen simultaneously sorts both the container and the player's main inventory.
  - **Smart UI:** Detects screen context and handles keypresses (**R**) even when screens are open. Renders a custom "Cool S" transparent PNG icon.
  - **Creative Safety:** Automatically restricts sorting in Creative Mode to prevent inventory corruption (only allows survival tab and containers).
  - **Internationalization:** All UI strings are localized (default: English).

## Architecture
- **`com.sunay.inventorysorter.InventorySorter`:** Main entrypoint (Common). Registers Server Networking (`ModNetworking.registerC2SPackets()`).
- **`com.sunay.inventorysorter.InventorySorterClient`:** Client entrypoint; handles keybindings (Default: **R**).
- **`com.sunay.inventorysorter.ModNetworking`:** Handles Client-to-Server (C2S) packets with rate-limiting (500ms cooldown) to prevent DoS/spam.
- **`com.sunay.inventorysorter.SortingLogic`:** Core business logic on the server. Implements "Safe Sort", bounds checking, and stack merging.
- **`com.sunay.inventorysorter.mixin.HandledScreenMixin`:** Injects the "Cool S" button and handles context-aware Dual Sorting.

## Building and Running
- **Generate JAR:** `./gradlew build`
- **Dev Environment:** `./gradlew runClient`
- **Clean:** `./gradlew clean`
- **Deployment Path:** `C:\Users\ASUS\AppData\Roaming\ModrinthApp\profiles\Fabric 1.21\mods`
- **Mandatory Workflow:** After every version bump and successful build, the resulting `.jar` must be automatically moved to the Deployment Path, replacing old versions of this mod.

## Decision-Making
- **Mandatory:** Never make arbitrary decisions on behalf of the user regarding project features, keybindings, UI layout, or significant architectural shifts. Always **ask for preference**.

## Development Conventions
- **Data Safety:** Never modify inventories directly without a backup/verify step.
- **Localization:** Use `en_us.json` for all UI text. Access via `Text.translatable()`.
- **Git:** Use atomic commits with `type: message` format. Push to `main` frequently.
- **Roadmap:** Consult `ROADMAP.md` and `DECISIONS.md` before implementing large features.
