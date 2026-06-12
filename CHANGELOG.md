# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
