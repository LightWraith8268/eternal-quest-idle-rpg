# Changelog

All notable changes to this project are documented in this file.

The format is based on Keep a Changelog, and this project adheres to Semantic Versioning.

## [Unreleased]
- Performance and FX polish
- JSON externalization for more systems
- Localization and accessibility pass

## [1.0.0] - 2025-09-13
### Added
- Core loop skills: Mining, Woodcutting, Fishing, Cooking
- Combat alpha: time‑based mechanics, equipment, loot tables, auto‑eat
- Bank & QoL Store: dynamic tabs/slots, sorting/search tools, auto‑sell
- Extended content tiers and JSON‑loaded activities (recipes.json)
- Prestige per skill with +5% XP per prestige
- Ascension with Ethereal Sigils and multi‑profile support (1–3)
- Sigil Meta‑Perks (XP/Speed/Loot), JSON‑tunable (perks.json)
- Store category labels, icons, and order (store.json)
- Live profile switching; profile delete confirmation
- Bank and store UI state persistence per profile

### Changed
- Costs/caps for QoL upgrades can be tuned via upgrades.json
- Combat log styled with icons and colors for clarity

### Fixed
- Safer bank handling: auto‑sell when bank is full (if enabled)
- Confirmation dialogs for Prestige and Ascension

[Unreleased]: https://example.com/compare/v1.0.0...HEAD
[1.0.0]: https://example.com/releases/v1.0.0
