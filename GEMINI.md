# Inventory Sorter - Gemini Instructions

## Project Overview
**Inventory Sorter** is a client-side Minecraft mod built for the **Fabric loader** (version 1.21). It allows players to sort their inventory and chests using a keybinding or a dedicated UI button.

- **Tech Stack:** Java 21, Fabric Loader, Gradle.
- **Architecture:**
  - **Main Mod:** `InventorySorter.java` handles common initialization.
  - **Client Mod:** `InventorySorterClient.java` manages client-only features like keybindings (default key: **R**).
  - **Core Logic:** `SortingLogic.java` contains the alphabetical sorting algorithms.
  - **UI (Mixins):** `HandledScreenMixin.java` injects a "Sort" button (labeled "S") into all container screens.

## Building and Running
This project uses Gradle. Key commands include:

- **Build Mod:** `./gradlew build` (Generates JAR in `build/libs/`)
- **Run Client:** `./gradlew runClient` (Launches Minecraft with the mod)
- **Run Server:** `./gradlew runServer` (Launches a dedicated server)
- **Clean Build:** `./gradlew clean`

## Development Conventions
- **Package Structure:** All code lives under `com.sunay.inventorysorter`.
- **Mixins:** UI modifications are handled via Mixins in `com.sunay.inventorysorter.mixin`.
- **Translations:** Localization strings are stored in `src/main/resources/assets/inventorysorter/lang/en_us.json`.
- **Git Workflow:** 
  - Follow atomic commits.
  - Push to `main` branch after each major task.
  - Maintain the `PLAN.md` file to track development phases.
