<div align="center">
  <div style="background-color: #2b2b2b; padding: 20px; border-radius: 10px; display: inline-block;">
    <img src="src/main/resources/assets/inventorysorter/icon.png" width="128" height="128" alt="Inventory Sorter Icon" style="image-rendering: pixelated;"/>
  </div>
  <h1>Inventory Sorter</h1>
  <p><em>A lightweight, safe, and intuitive inventory management mod for Minecraft Fabric.</em></p>

  [![Fabric](https://img.shields.io/badge/Loader-Fabric-orange?style=flat-square)](https://fabricmc.net/)
  [![Minecraft](https://img.shields.io/badge/Minecraft-1.21-success?style=flat-square)](https://minecraft.net/)
  [![Version](https://img.shields.io/badge/Version-1.1.6-blue?style=flat-square)]()
  [![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)](LICENSE)
</div>

---

## Overview
**Inventory Sorter** is a client-side (with server-side authority) Fabric mod that allows you to instantly organize your chests, barrels, and personal inventory. It focuses on **data safety**, ensuring your items are never lost during a crash, and provides a seamless UI experience.

## Key Features
- **Safe Transaction Sorting:** Items are copied, sorted, and verified on the server-side before any changes are made to your inventory. 100% protection against item deletion or duplication exploits.
- **Smart Stack Merging:** Before sorting, identical items are automatically merged into full stacks to save maximum space.
- **Context-Aware UI:** A stylized, non-intrusive "Cool S" button appears in containers. It dynamically adjusts its position to avoid overlapping with other mods like REI or JEI.
- **Dual Sorting:** Pressing the sort key while looking at a chest will simultaneously sort both the chest's contents and your own inventory as independent, safe actions.
- **Creative Mode Safe:** Intelligently disables sorting for dangerous creative tabs (like the search or crafting grid) to prevent ghost items, while fully supporting creative chest sorting.

## Installation

### Requirements
- **Minecraft:** `1.21`
- **Loader:** [Fabric Loader](https://fabricmc.net/use/installer/) `0.15.11+`
- **Dependency:** [Fabric API](https://modrinth.com/mod/fabric-api) `0.100.0+`

### Setup
1. Download the latest `.jar` from the [Releases](#) page (or compile it yourself).
2. Drop both `inventorysorter-x.x.x.jar` and the `fabric-api.jar` into your Minecraft `.minecraft/mods` folder.
3. Launch the game using the Fabric profile.

## Usage
- **Keybinding:** Press <kbd>R</kbd> (default) to sort the inventory you are currently looking at. Works whether the inventory screen is open or closed.
- **UI Button:** Click the small "S" icon in the top right of any chest, barrel, or inventory screen.

## Building from Source
This project uses the Gradle wrapper and requires Java 21.

```bash
# Clone the repository
git clone https://github.com/sunayagiz/InventorySorter.git
cd InventorySorter

# Build the mod JAR
./gradlew build
# The compiled JAR will be in build/libs/
```

## License
This project is licensed under the MIT License - see the `LICENSE` file for details.

## Contributing
Contributions are welcome! Please check the `ROADMAP.md` for planned features and open an issue before submitting major pull requests. Ensure all code follows the transactional safety protocols outlined in `DECISIONS.md`.
