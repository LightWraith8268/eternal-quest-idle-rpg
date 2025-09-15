# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Eternal Quest** is an idle RPG mobile game built with Kotlin and Jetpack Compose. The game features deterministic offline progression, skill-based gameplay, and prestige mechanics for long-term engagement.

## Development Commands

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Install debug build to device
./gradlew installDebug

# Run lint checks
./gradlew lint

# Clean build
./gradlew clean

# Generate debug APK
./gradlew assembleDebug
```

## Core Architecture

### Data Layer
- **Room Database**: Primary persistence layer with entities for Player, Skills, Bank, Items, Monsters, ActivityState, Config
- **JSON Config System**: External configuration files for skills, items, recipes, and monsters
- **Save/Load System**: Integrity-checked save states with offline progress calculation

### Game Engine
- **Tick Engine**: Handles active vs offline progression with 33% offline rate modifier
- **Time Service**: Deterministic time tracking for consistent progression
- **Activity System**: One-active-skill rule with automatic cancellation on switch

### UI Architecture
- **Jetpack Compose**: Modern declarative UI framework
- **Tabbed Navigation**: Skills, Bank, Character, Settings tabs
- **Offline Summary**: "While you were gone" popup system

## Development Phases

### Phase 0: Foundations (Complete)
- Project setup with Kotlin + Jetpack Compose
- Room database schema implementation
- Time service and tick engine
- Basic UI shell with navigation

### Phase 1: Core Loop MVP (Complete)
- XP/leveling system (skills cap at 100)
- Initial skills: Mining, Woodcutting, Fishing, Cooking
- Bank system: 5 tabs, 15 slots per tab
- Offline progression with 7-day maximum, 33% rate

### Phase 2: Combat Alpha (Complete)
- Time-based combat, auto-eat, loot tables, combat UI and DB
- Migrations 1→2, 2→3, 3→4 validated

### Phase 3: Bank Expansion & QoL Store (Complete)
- Dynamic bank capacity (5→10 tabs, 15→50 slots)
- QoL Store with pricing, prerequisites, affordability, and owned state
- Gold balance integration; Bank stats and Store CTA from Bank

### Phase 4: Extended Skills & Crafting (Complete)
- Added higher-tier gathering and cooking chains (Coal, Maple, Tuna, Mithril, Yew, Swordfish)
- New activities with level gates and XP pacing; UI shows locked requirements
- Repository upserts new items on update to preserve saves

### Phase 5: Smithing & Recipes (Complete)
- New skill: Smithing (unlocked by default)
- Smelting: Iron/Gold/Mithril bars with item costs and level gates
- Forging: Iron Sword, Iron Helmet (bar consumption, level requirements)
- Start-activity material checks/consumption; rewards deposited to bank

### Phase 6: Prestige System (Complete)
- Per-skill prestige available at level 100
- Prestige action resets skill to level 1 and increments prestige count
- XP gains scale with prestige (+5% per prestige)

### Phase 7: Ascension System (Complete)
- Full character reset action available from Character screen when all skills are level 100
- Awards Ethereal Sigils (1 per maxed skill), increments Ascension count
- Resets skills, bank, upgrades/gold, and combat state; preserves item definitions

### Phase 8: Sigil Meta-Perk Store (Complete)
- Spend Ethereal Sigils on permanent meta-perks (account-level)
- Implemented: XP Bonus (+2% XP/level), Speed (-2% time/level), Loot (+2% chance/level), caps at 5
- Perks persist across Ascensions; costs Sigils from Player

### Phase 9: Multi-Character & Launch Prep (Complete)
- Multi-profile support (up to 3) via per-profile databases
- Settings screen lets you select and save active profile (restart to apply)
- No data cross-contamination; each profile has isolated progression and economy

### Phase 10: Combat Expansion & Area System (Complete)
- **Combat Level System**: Combined stat calculation (max 600) replacing individual level requirements
- **Expanded World**: 17 overworld areas + 15 dungeon systems with progressive difficulty
- **Crafting Materials**: Animal/creature drops for crafting (leather, feathers, fur, etc.)
- **Dungeon Progression**: Linear unlock system requiring previous dungeon completion
- **Area-Based Spawning**: Enemies assigned to specific areas with appropriate combat levels

### Phase 11: Content Expansion & Balance (In Progress)
- **World Expansion**: Additional 5 overworld areas for extended endgame
- **Enemy Diversity**: 100+ unique enemies across all areas and dungeons
- **Combat Balance**: Adjusted progression curve for 600 max combat level
- **Material Economy**: Comprehensive crafting material drops from all creature types

## Key Design Principles

### Progression Systems
- **Offline Safety**: All progress must be deterministic and recoverable
- **One Active Skill**: Only one skill can be active at a time
- **Prestige Mechanics**: Skills can be reset at level 100 for permanent bonuses
- **Ascension System**: Full character resets for meta-progression with Ethereal Sigils

### Economy Design
- **No Real Money**: All upgrades purchasable with in-game gold only
- **Bank Expansion**: Progressive costs for additional tabs (up to 10) and slots (up to 50 per tab)
- **Resource Flow**: Gather → Bank → Craft → Combat cycle

### Technical Requirements
- **Save Integrity**: Checksums and validation for all save data
- **Offline Simulation**: Maximum 7 days at 33% efficiency
- **Multi-Character**: Separate save states per profile, shared UI settings
- **Cross-Platform**: Android-focused with potential for expansion

## Database Schema

### Core Entities
- `Player`: Character stats, levels, current activity, ascension count, sigils
- `Skills`: Individual skill levels, XP, prestige counts
- `BankItem`: Item storage with tab/slot organization
- `Item`: Item definitions, quantities, metadata
- `CombatStats`: Player combat stats and equipment
- `CurrentEnemy`: Active enemy state during combat
- `ActivityState`: Current action progress, timers
- `PlayerUpgrades`: QoL upgrades purchased from store
- `GoldBalance`: Player's gold currency
- `SigilPerks`: Meta-perk levels purchased with sigils

### Relationships
- Player 1:Many Skills
- Player 1:Many Bank slots
- Skills 1:Many Items (via recipes)
- Combat 1:Many Items (via loot tables)

## Testing Strategy

### Critical Test Areas
- Save/load integrity across force-close and airplane mode
- Offline progression calculation accuracy
- Time service determinism
- Bank item stacking and retrieval
- Combat mechanics and damage calculations
- Prestige/Ascension reset validation
- Profile switching and isolation

### Performance Considerations
- Efficient Room queries for large item collections
- Compose recomposition optimization for real-time updates
- Background processing for offline calculations
- Memory management for long gaming sessions

## Configuration Management

### JSON Config Files (in `app/src/main/assets/config/`)
- `recipes.json`: Extra activities and crafting recipes
- `perks.json`: Sigil perk caps and costs
- `upgrades.json`: QoL upgrade costs and limits
- `store.json`: Store category labels, icons, and order

### Dynamic Content
- Skill progression formulas (XpSystem)
- Combat damage calculations (CombatEngine)
- Resource yield rates (TickEngine)
- Prestige bonus multipliers (+5% per prestige)