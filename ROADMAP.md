# InventorySorter Roadmap (v1.1.0)

This roadmap is the result of a comprehensive 10-agent virtual council analysis aimed at identifying strategic improvements for InventorySorter.

## Strategic Council Findings

### 1. UX Design & Accessibility
- **Current State:** Basic middle-click action is functional but lacks discoverability.
- **Action Items:** 
  - Implement a dedicated Configuration Screen (using Cloth Config or similar).
  - Add tooltips indicating "Middle-Click to Sort" on supported inventory screens.
  - Provide visual/audio feedback when sorting is executed successfully.

### 2. Multiplayer & Networking
- **Current State:** Sorting relies on client-side simulation or basic implementation, leading to potential desyncs.
- **Action Items:** 
  - Design a robust S2C/C2S packet system.
  - Move the actual sorting logic to the server-side to ensure deterministic results.
  - Implement a client-side prediction mechanism to maintain responsiveness.

### 3. Algorithm Expansion
- **Current State:** Basic sorting logic.
- **Action Items:**
  - Implement diverse sorting modes accessible via the config or a hotkey toggle:
    - **By ID:** Alphabetical by item identifier.
    - **By Rarity:** Common -> Uncommon -> Rare -> Epic.
    - **By Category:** Blocks, Combat, Tools, Food, etc.
    - **By Durability:** Group tools by type and sort by remaining durability.

### 4. Mod Compatibility
- **Current State:** Unknown interactions with major inventory mods.
- **Action Items:**
  - Investigate API/Mixins of "Inventory Profiles Next" and "REI/JEI/EMI".
  - Ensure our middle-click handler doesn't aggressively consume events meant for other mods.
  - Implement specific ignore lists for containers from popular storage mods (e.g., Applied Energistics 2, Tom's Simple Storage) if sorting breaks them.

### 5. Localization
- **Current State:** Primarily English (or unlocalized).
- **Action Items:**
  - Extract all hardcoded strings into `en_us.json`.
  - Solicit and integrate community translations for at least:
    - Turkish (`tr_tr.json`)
    - German (`de_de.json`)

### 6. DevOps & Standards
- **Current State:** Initial structure.
- **Action Items:**
  - Established `CHANGELOG.md` following "Keep a Changelog".
  - Established `VERSIONING_POLICY.md` adhering to SemVer.
  - Set up GitHub Actions for automated building and releasing.

### 7. Security & Exploit Prevention
- **Current State:** Middle-click sorting can sometimes trigger multiple times or cause lag.
- **Action Items:**
  - Audit sorting algorithm for item duplication vulnerabilities (e.g., sorting while dropping items or closing the container).
  - Implement a strict server-side rate limit (cooldown) on sorting requests to prevent packet spam.
  - Ensure transactions are atomic during server-side sorting.

### 8. Performance Optimization
- **Current State:** Unoptimized for massive inventories.
- **Action Items:**
  - Benchmark sorting on very large containers (e.g., double chests, modded diamond chests).
  - Optimize the item comparison logic to reduce CPU cycles.
  - Pre-allocate arrays/lists during sorting instead of dynamically resizing them.

### 9. Documentation
- **Current State:** Barebones README.
- **Action Items:**
  - Expand the `README.md` with features, installation instructions, and troubleshooting.
  - Create a GitHub Wiki detailing advanced configuration options and mod compatibility notes.
  - Document the source code (JavaDocs) for easier community contribution.

### 10. v1.1.0 Release Coordination
- **Summary:** The v1.1.0 release will focus on establishing the foundation for a stable, configurable, and robust mod.
- **Milestone Goals:**
  1. Migrate to Server-Side sorting architecture.
  2. Implement Cloth Config integration with basic Tooltips.
  3. Release English, German, and Turkish localizations.
  4. Fix identified duplication/desync risks.
