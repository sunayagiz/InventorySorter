# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.2] - 2026-06-12

### Fixed
- **Critical Dupe Exploit:** Prevented item duplication by skipping sorting for crafting/output slots and verifying slot permissions.
- **DoS Vulnerability:** Added server-side bounds checking and maximum slot limits to prevent thread-hanging loops via malicious packets.
- **Spam Protection:** Implemented a 500ms rate limiter for sort requests to prevent packet spam.
- **Creative Ghost Items:** Fixed an issue where sorting from non-survival creative tabs caused ghost items. Sorting is now strictly tied to the Survival tab in Creative Mode.

### Added
- **UI Asset Fallback:** Added a text fallback ("S") that renders beneath the Cool S icon, ensuring the button remains identifiable if the texture fails to load.

## [1.1.1] - 2026-06-12

### Added
- **Cool S Icon:** Replaced text-based "S" button with a stylized "Cool S" transparent PNG icon.

### Changed
- **Smaller Button:** Reduced the sort button size from 20x20 to 12x12 for a cleaner UI.
- **Improved UI Layout:** Adjusted dynamic button positioning to account for the smaller size.

### Fixed
- **Creative Mode Sorting:** Enabled sorting in Creative Mode for containers (chests) and implemented safe sorting for the player's main inventory.

## [1.1.0] - 2026-06-12

### Added
- **Networking Support:** Implemented Server-Side Authority (C2S packets) to resolve client-server desync and "reverting items" bug.
- **Smart UI:** Added context-aware "Sort" button (S) that detects and targets player vs. container (chest) inventories.
- **Keybinding Enhancement:** 'R' key now works even when inventory screens are open.
- **Strategic Documents:** Added `ROADMAP.md`, `DECISIONS.md`, and `VERSIONING_POLICY.md`.

### Fixed
- **Creative Mode Deletion:** Fixed a bug where items were deleted when sorting in Creative Mode.
- **Data Safety:** Refactored sorting to a transaction-style "Safe Sort" (Copy -> Sort -> Verify -> Write) to prevent item loss.

## [1.0.0] - 2026-06-12
- Initial Release with basic client-side alphabetical sorting.
