# Eternal Quest

Eternal Quest is an idle RPG for Android built with Kotlin and Jetpack Compose. It features deterministic offline progression, robust data integrity, and long‑term meta systems (Prestige, Ascension, Sigil perks).

## Features (v1.0.0)
- Core loop: Mining, Woodcutting, Fishing, Cooking with XP/levels (cap 100)
- Combat system: time‑based attacks, curated loot tables, auto‑eat priorities, and equipment loadouts with best‑owned guidance
- Bank & QoL Store: dynamic tabs (5→10), slots (15→50), sorting/search tools, auto‑sell, and inventory selling directly from the store
- Expanded content: higher‑tier resources plus JSON‑loaded activities, skills, items, enemies, areas, and loot tables for easy tuning
- Meta progression: Prestige per skill, Ascension resets with Ethereal Sigils, and Sigil meta‑perks for XP/speed/loot bonuses
- Presentation & profiles: Material You theming, combat log toggle, in‑app changelog, sprite preview, and up to 3 isolated profiles with live switching

## Build & Run
- Build: `./gradlew build`
- Assemble: `./gradlew :app:assembleDebug`
- Install (device/emulator): `./gradlew installDebug`
- Android config: `compileSdk 34`, `targetSdk 34`, `minSdk 26`

## Configuration
- JSON assets under `app/src/main/assets/config/`:
  - `recipes.json` – extra activities/recipes
  - `perks.json` – perk caps/costs
  - `upgrades.json` – QoL upgrade costs/limits
  - `store.json` – store category labels, icons, and order

## Versioning
- Version scheme: SemVer (MAJOR.MINOR.PATCH)
- App `versionName`: `1.0.0` (`app/build.gradle`), `versionCode`: 1
- See `CHANGELOG.md` for release history

## Testing
- Unit tests: `./gradlew test`
- Lint: `./gradlew :app:lint`

## Roadmap (post‑1.0)
- More JSON‑driven content (enemies, items)
- Store polish (badges, discovery), animations/FX
- Cloud backup hooks, localization, accessibility pass
