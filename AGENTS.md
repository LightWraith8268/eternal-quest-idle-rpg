# Repository Guidelines

## Project Structure & Modules
- `app/`: Android app (Kotlin + Jetpack Compose).
  - `src/main/java/`: Source code (package-based folders).
  - `src/main/res/`: Android resources; `AndroidManifest.xml`.
  - Config (present): `src/main/assets/config/` — `recipes.json` (extra activities), `perks.json` (Sigil perks), `upgrades.json` (QoL tuning), `store.json` (store UI). Items/enemies/skills are currently defined in code, not JSON.
- Root validation artifacts: `combat_test.kt`, `xp_test.kt`, `integration_test.kt`, `database_validation_test.kt`, `user_journey_test.kt`, `test_compilation.kt`, `database_test.sql` (do not modify).
- Gradle: `build.gradle`, `settings.gradle`, `gradle.properties`, `gradlew`, `gradlew.bat`.

## Build, Test, and Dev Commands
- Build: `./gradlew build` (Windows: `gradlew.bat build`).
- Unit tests: `./gradlew :app:testDebugUnitTest` or `./gradlew test`.
- Instrumented tests (if present): `./gradlew :app:connectedAndroidTest`.
- Lint/format: `./gradlew :app:lint` • Clean: `./gradlew clean`.
- Assemble/Install: `./gradlew :app:assembleDebug` • `./gradlew installDebug`.

## Architecture Overview
- Data layer (Room v9): `players`, `skills`, `items`, `bank_items`, `activity_states`, `combat_stats`, `current_enemy`, `player_upgrades`, `gold_balance`, `sigil_perks`. Enemies and items definitions are code-driven; `Enemy` is not persisted. Migrations 1→9 implemented.
- Engine: Tick engine + `TimeService` for ticks/offline calc; 7‑day offline cap with efficiency rate (base 33%, up to 60% via upgrades); one‑active‑skill rule; meta multipliers from Sigil perks.
- UI: Jetpack Compose with tabs (Skills, Bank, Combat, Character, Store, Settings) and an offline summary dialog.
- QoL Store: Dynamic bank capacity (tabs/slots) driven by `player_upgrades`; upgrades, perks, and store presentation partially JSON‑tunable.

## Current State (v1.0.0)
- Core loop skills: Mining, Woodcutting, Fishing, Cooking; Smithing (smelting/forging) with item costs and requirements.
- Combat alpha: time‑based attacks, auto‑eat (priority/best item), equipment, loot tables, gold rewards, safe defeat handling.
- Bank & QoL Store: base 5 tabs × 15 slots; upgrade to 10 tabs × 50 slots; sorting/search tools; auto‑sell; store categories/labels/icons/order via JSON; affordability, prerequisites, and “owned/new” states.
- Meta systems: Prestige (per skill), Ascension (resets, grants Ethereal Sigils), Sigil Meta‑Perks (XP/Speed/Loot) with JSON caps/costs.
- Offline progression: deterministic summary; 7‑day cap; base 33% efficiency configurable by upgrades to 40/50/60%.
- Profiles: 1–3 isolated profiles with live switch; DB per profile.

## Data & Schema Notes
- Room database version: 9; migrations 1→9 present in `EternalQuestDatabase`.
- Entities used by Room: Player, Skill, Item, BankItem, ActivityState, CombatStats, CurrentEnemy, PlayerUpgrades, GoldBalance, SigilPerks.
- Content sources: Activities base set in code; extras loaded from `assets/config/recipes.json`.
- Items/Weapons/Armors/Enemies: defined in Kotlin (hardcoded catalogs); not yet loaded from JSON.

## Coding Style & Naming
- Kotlin; 4-space indent; no tabs.
- Classes `PascalCase`; funcs/vars `camelCase`; constants `UPPER_SNAKE_CASE`.
- Packages lowercase (e.g., `com.eternalquest.feature`). Match file to top-level class.
- Favor idiomatic Kotlin (null-safety, data/sealed classes) and small, testable functions.

## Testing Guidelines
- Place unit tests in `app/src/test/java/...` mirroring packages; name `ClassNameTest.kt` with descriptive `test...()`.
- Focus: save/load integrity, offline calc accuracy, time determinism, bank stacking, combat damage, prestige/ascension.
- Run: `./gradlew :app:testDebugUnitTest`; reports under `app/build/reports/tests/`.

## Commit & PR Guidelines
- Conventional Commits (`feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `chore:`). Keep changes small and scoped (e.g., `feat(combat): add crit calc`).
- PRs: clear summary, rationale, linked issues, before/after notes; screenshots for UI; include APK/output paths when relevant.

## Agent & Config Tips
- Keep root validation files unchanged; avoid new modules without discussion.
- Prefer storing tunables in JSON. Currently JSON governs: activities (extras), perk caps/costs, QoL upgrade tuning, store labels/icons/order. Items/enemies/skills are code-driven today.
- Time determinism: consider injecting a clock/time source when adding tests or new logic; `TimeService` currently holds tick/offline constants.
- Bank capacity: UI/engine derive from `PlayerUpgrades`; avoid hardcoding in new code paths. Known gap: combat loot deposit still uses a 5×15 fallback — wire it to `PlayerUpgrades` when touching combat.

## Known Limitations / Follow-ups
- Items and enemies are defined in code; future work: move to JSON assets for easier tuning/content updates.
- `TimeService` uses `System.currentTimeMillis()` and hardcoded defaults; consider config + injectable clock for tests.
- Combat loot banking uses fixed capacity; align with dynamic capacity from `PlayerUpgrades`.
- `DatabaseProvider` migration list lags `EternalQuestDatabase` (up to 6→7 vs. 9); unify to ensure consistent upgrades across profiles.

## Versioning
- Current version: `1.0.0` (see `VERSION`, `app/build.gradle`).
- Roadmap highlights: more JSON-driven content (enemies/items), store polish, cloud backup hooks, localization, accessibility.
