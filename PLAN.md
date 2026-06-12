# Project: Inventory Sorter (Minecraft Fabric Mod)

## Goal
Build a client-side Minecraft mod for the Fabric loader that allows users to sort their inventory and chests with a single keypress.

## Technology Stack
- **Loader:** Fabric
- **Minecraft Version:** 1.21 (Latest)
- **Language:** Java
- **Build Tool:** Gradle

## Architecture
1. **Core Logic:** Sorting algorithms for different item types (alphabetical, category, stack size).
2. **GUI/Input:** Keybinding to trigger sorting and optional UI buttons in inventory screens.
3. **Network:** (Optional) Server-side support for smoother sorting on multiplayer.

## Development Phases
- **Phase 1: Scaffolding.** Initialize Gradle project with Fabric dependencies.
- **Phase 2: Input Handling.** Register keybindings.
- **Phase 3: Inventory Logic.** Implement sorting algorithms and container manipulation.
- **Phase 4: UI Enhancements.** Add buttons to inventory screens.

## Task Delegation
- **agent-scaffold:** Set up `build.gradle`, `fabric.mod.json`, and base package structure.
- **agent-logic:** Implement `SortingAlgorithm.java` and inventory utility classes.
- **agent-ui:** Handle Mixins for inventory screens and keybinding registration.
