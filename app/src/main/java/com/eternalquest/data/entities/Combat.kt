package com.eternalquest.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "combat_stats")
data class CombatStats(
    @PrimaryKey val playerId: Int = 1,
    val hitpoints: Int = 100,
    val maxHitpoints: Int = 100,
    val attack: Int = 1,
    val strength: Int = 1,
    val defense: Int = 1,
    val ranged: Int = 1,
    val magic: Int = 1,
    val prayer: Int = 1,
    val combatXp: Long = 0L,
    val equippedWeapon: String? = null,
    val equippedArmor: String? = null,
    val autoEatEnabled: Boolean = true,
    val autoEatFoodId: String? = null,
    val autoEatThreshold: Float = 0.5f,
    val useBestAutoEat: Boolean = false,
    val isInCombat: Boolean = false,
    val currentEnemyId: String? = null,
    val combatStartTime: Long? = null,
    val lastPlayerAttack: Long? = null,
    val lastEnemyAttack: Long? = null
) {
    // Calculate combat level from all combat stats (max 600)
    fun calculateCombatLevel(): Int {
        val baseLevel = (attack + strength + defense + hitpoints + ranged + magic + prayer) / 7.0
        val attackBonus = maxOf(attack, strength) * 0.325
        val defenseBonus = defense * 0.25
        val rangedBonus = ranged * 0.325
        val magicBonus = magic * 0.325
        val hitpointsBonus = hitpoints * 0.25
        val prayerBonus = prayer * 0.125

        val totalLevel = (baseLevel + attackBonus + defenseBonus + rangedBonus + magicBonus + hitpointsBonus + prayerBonus).toInt()
        return totalLevel.coerceAtMost(600)
    }
}

@Entity(tableName = "enemies")
data class Enemy(
    @PrimaryKey val id: String,
    val name: String,
    val level: Int,
    val hitpoints: Int,
    val maxHitpoints: Int,
    val attack: Int,
    val strength: Int,
    val defense: Int,
    val attackSpeed: Long, // milliseconds between attacks
    val accuracy: Float = 0.8f,
    val experienceReward: Long,
    val combatLevelRequired: Int = 1,
    val description: String,
    val area: EnemyArea = EnemyArea.STARTING_MEADOW
)

@Entity(tableName = "current_enemy")
data class CurrentEnemy(
    @PrimaryKey val id: Int = 1,
    val enemyId: String,
    val currentHp: Int,
    val maxHp: Int,
    val nextAttackTime: Long,
    val combatStartTime: Long
)

data class Weapon(
    val id: String,
    val name: String,
    val attackBonus: Int,
    val strengthBonus: Int,
    val attackSpeed: Long, // milliseconds between attacks
    val accuracy: Float,
    val levelRequired: Int,
    val category: WeaponCategory,
    val description: String
)

data class Armor(
    val id: String,
    val name: String,
    val defenseBonus: Int,
    val hitpointBonus: Int,
    val levelRequired: Int,
    val slot: ArmorSlot,
    val description: String
)

enum class WeaponCategory {
    SWORD,
    AXE,
    BOW,
    STAFF,
    DAGGER,
    MACE,
    SPEAR,
    CROSSBOW
}

enum class ArmorSlot {
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS,
    GLOVES,
    SHIELD,
    CAPE,
    AMULET,
    RING
}

data class LootDrop(
    val itemId: String,
    val quantity: Int,
    val chance: Float,
    val isRare: Boolean = false
)

data class CombatAction(
    val timestamp: Long,
    val attacker: CombatantType,
    val damage: Int,
    val accuracy: Boolean,
    val critical: Boolean = false,
    val remainingHp: Int
)

enum class CombatantType {
    PLAYER,
    ENEMY
}

enum class AreaType {
    OVERWORLD,
    DUNGEON,
    RAID,
    BOSS_ROOM
}

enum class EnemyArea {
    // Overworld Areas
    STARTING_MEADOW,
    DARK_FOREST,
    HAUNTED_CEMETERY,
    MOUNTAIN_PEAKS,
    DESERT_WASTELAND,
    FROZEN_TUNDRA,
    VOLCANIC_REGION,
    MYSTIC_SWAMP,
    CRYSTAL_CAVERNS,
    SHADOW_REALM,
    CELESTIAL_PLAINS,
    VOID_DIMENSION,
    PRIMAL_JUNGLE,
    ETHEREAL_PLANE,
    NIGHTMARE_REALM,
    ASTRAL_WASTES,
    TEMPORAL_RIFTS,
    CHAOS_STORMS,
    DIVINE_SANCTUM,
    ETERNAL_BATTLEGROUND,

    // Dungeon Areas
    GOBLIN_CAVES,
    SKELETON_CRYPTS,
    VAMPIRE_CASTLE,
    DRAGON_LAIR,
    VOID_TEMPLE,
    CRYSTAL_MINES,
    FIRE_TEMPLE,
    ICE_FORTRESS,
    SHADOW_DUNGEON,
    LIGHT_SANCTUARY,
    ANCIENT_RUINS,
    DEMON_PORTAL,
    TITAN_STRONGHOLD,
    COSMIC_OBSERVATORY,
    WORLD_TREE_ROOTS,
    ELEMENTAL_NEXUS,
    UNDEAD_CITADEL,
    BEAST_SANCTUARY,
    MECHANICAL_FORGE,
    ASTRAL_OBSERVATORY,
    BLOOD_CATHEDRAL,
    POISON_SWAMP_DEPTHS,
    THUNDER_PEAKS_DUNGEON,
    ABYSSAL_DEPTHS,
    NECROMANCER_TOWER,
    CLOCKWORK_CITADEL,
    CHAOS_REALM,
    PRIMORDIAL_CAVERNS,
    STARFALL_SANCTUARY,
    DOOMSPIRE_KEEP
}

data class GameArea(
    val id: String,
    val name: String,
    val type: AreaType,
    val minCombatLevel: Int,
    val maxCombatLevel: Int,
    val description: String,
    val unlockRequirement: String? = null,
    val isUnlocked: Boolean = true, // Overworld areas are unlocked by default
    val requiredPreviousDungeon: String? = null // For dungeon progression
)

data class DungeonFloor(
    val floorNumber: Int,
    val name: String,
    val enemies: List<String>,
    val bossEnemy: String? = null,
    val specialLoot: List<String> = emptyList()
)

object Weapons {
    // Swords
    val BRONZE_SWORD = Weapon(
        "bronze_sword", "Bronze Sword", 5, 0, 4000L, 0.75f, 1, WeaponCategory.SWORD,
        "A basic bronze sword for new warriors"
    )
    val IRON_SWORD = Weapon(
        "iron_sword", "Iron Sword", 10, 2, 3800L, 0.8f, 10, WeaponCategory.SWORD,
        "A sturdy iron sword with improved damage"
    )
    val STEEL_SWORD = Weapon(
        "steel_sword", "Steel Sword", 18, 5, 3600L, 0.85f, 25, WeaponCategory.SWORD,
        "A sharp steel blade for experienced fighters"
    )
    val MITHRIL_SWORD = Weapon(
        "mithril_sword", "Mithril Sword", 26, 8, 3400L, 0.9f, 35, WeaponCategory.SWORD,
        "A gleaming mithril blade"
    )
    val ADAMANT_SWORD = Weapon(
        "adamant_sword", "Adamant Sword", 34, 12, 3200L, 0.92f, 45, WeaponCategory.SWORD,
        "An exceptionally strong blade"
    )
    val DRAGON_SWORD = Weapon(
        "dragon_sword", "Dragon Sword", 42, 16, 3000L, 0.95f, 60, WeaponCategory.SWORD,
        "A legendary dragon-forged sword"
    )
    val RUNITE_SWORD = Weapon(
        "runite_sword", "Runite Sword", 50, 20, 2800L, 0.97f, 70, WeaponCategory.SWORD,
        "A blade of pure runic power"
    )
    val DRAGONITE_SWORD = Weapon(
        "dragonite_sword", "Dragonite Sword", 58, 24, 2600L, 0.98f, 80, WeaponCategory.SWORD,
        "A sword infused with dragon essence"
    )
    val CELESTIAL_SWORD = Weapon(
        "celestial_sword", "Celestial Sword", 66, 28, 2400L, 0.99f, 90, WeaponCategory.SWORD,
        "A divine blade from the heavens"
    )
    val VOID_SWORD = Weapon(
        "void_sword", "Void Sword", 74, 32, 2200L, 1.0f, 95, WeaponCategory.SWORD,
        "A blade that cuts through reality itself"
    )

    // Axes
    val BRONZE_AXE = Weapon(
        "bronze_axe", "Bronze Axe", 8, 3, 4500L, 0.7f, 5, WeaponCategory.AXE,
        "A heavy bronze axe that hits hard but slow"
    )
    val IRON_AXE = Weapon(
        "iron_axe", "Iron Axe", 15, 6, 4200L, 0.75f, 15, WeaponCategory.AXE,
        "An iron axe with devastating power"
    )
    val STEEL_AXE = Weapon(
        "steel_axe", "Steel Axe", 22, 8, 4000L, 0.8f, 25, WeaponCategory.AXE,
        "A sharp and heavy steel axe"
    )
    val MITHRIL_AXE = Weapon(
        "mithril_axe", "Mithril Axe", 30, 12, 3800L, 0.85f, 35, WeaponCategory.AXE,
        "A lightweight but powerful mithril axe"
    )
    val ADAMANT_AXE = Weapon(
        "adamant_axe", "Adamant Axe", 38, 16, 3600L, 0.9f, 45, WeaponCategory.AXE,
        "An exceptionally strong adamant axe"
    )
    val DRAGON_AXE = Weapon(
        "dragon_axe", "Dragon Axe", 46, 20, 3400L, 0.95f, 60, WeaponCategory.AXE,
        "A mythical axe infused with dragon power"
    )
    val RUNITE_AXE = Weapon(
        "runite_axe", "Runite Axe", 54, 24, 3200L, 0.97f, 70, WeaponCategory.AXE,
        "An axe that cleaves through armor"
    )
    val DRAGONITE_AXE = Weapon(
        "dragonite_axe", "Dragonite Axe", 62, 28, 3000L, 0.98f, 80, WeaponCategory.AXE,
        "An axe forged from dragon essence"
    )

    // Bows
    val OAK_BOW = Weapon(
        "oak_bow", "Oak Bow", 4, 0, 3000L, 0.8f, 1, WeaponCategory.BOW,
        "A simple wooden bow"
    )
    val WILLOW_BOW = Weapon(
        "willow_bow", "Willow Bow", 8, 2, 2800L, 0.82f, 10, WeaponCategory.BOW,
        "A flexible willow bow"
    )
    val MAPLE_BOW = Weapon(
        "maple_bow", "Maple Bow", 12, 4, 2600L, 0.84f, 20, WeaponCategory.BOW,
        "A sturdy maple bow"
    )
    val YEW_BOW = Weapon(
        "yew_bow", "Yew Bow", 20, 8, 2400L, 0.88f, 40, WeaponCategory.BOW,
        "A powerful yew longbow"
    )
    val MAGIC_BOW = Weapon(
        "magic_bow", "Magic Bow", 28, 12, 2200L, 0.92f, 50, WeaponCategory.BOW,
        "A bow infused with magical energy"
    )
    val ELVEN_BOW = Weapon(
        "elven_bow", "Elven Bow", 36, 16, 2000L, 0.96f, 65, WeaponCategory.BOW,
        "A masterwork bow crafted by elves"
    )
    val DRAGON_BOW = Weapon(
        "dragon_bow", "Dragon Bow", 44, 20, 1800L, 0.98f, 75, WeaponCategory.BOW,
        "A bow made from dragonbone"
    )
    val SHADOW_BOW = Weapon(
        "shadow_bow", "Shadow Bow", 52, 24, 1600L, 1.0f, 85, WeaponCategory.BOW,
        "A bow that fires arrows of pure darkness"
    )

    // Staffs
    val APPRENTICE_STAFF = Weapon(
        "apprentice_staff", "Apprentice Staff", 6, 4, 3500L, 0.85f, 1, WeaponCategory.STAFF,
        "A basic staff for novice mages"
    )
    val ADEPT_STAFF = Weapon(
        "adept_staff", "Adept Staff", 12, 8, 3300L, 0.88f, 15, WeaponCategory.STAFF,
        "A staff for practicing mages"
    )
    val MASTER_STAFF = Weapon(
        "master_staff", "Master Staff", 18, 12, 3100L, 0.91f, 30, WeaponCategory.STAFF,
        "A staff wielded by master mages"
    )
    val ARCANE_STAFF = Weapon(
        "arcane_staff", "Arcane Staff", 26, 18, 2900L, 0.94f, 45, WeaponCategory.STAFF,
        "A staff infused with arcane power"
    )
    val ELEMENTAL_STAFF = Weapon(
        "elemental_staff", "Elemental Staff", 34, 24, 2700L, 0.97f, 60, WeaponCategory.STAFF,
        "A staff that commands the elements"
    )
    val VOID_STAFF = Weapon(
        "void_staff", "Void Staff", 42, 30, 2500L, 0.99f, 75, WeaponCategory.STAFF,
        "A staff that channels void magic"
    )
    val COSMIC_STAFF = Weapon(
        "cosmic_staff", "Cosmic Staff", 50, 36, 2300L, 1.0f, 90, WeaponCategory.STAFF,
        "A staff that harnesses cosmic power"
    )

    // Daggers
    val BRONZE_DAGGER = Weapon(
        "bronze_dagger", "Bronze Dagger", 3, 2, 2500L, 0.9f, 1, WeaponCategory.DAGGER,
        "A quick bronze blade"
    )
    val IRON_DAGGER = Weapon(
        "iron_dagger", "Iron Dagger", 6, 4, 2300L, 0.92f, 10, WeaponCategory.DAGGER,
        "A sharp iron dagger"
    )
    val STEEL_DAGGER = Weapon(
        "steel_dagger", "Steel Dagger", 12, 8, 2100L, 0.94f, 25, WeaponCategory.DAGGER,
        "A razor-sharp steel dagger"
    )
    val MITHRIL_DAGGER = Weapon(
        "mithril_dagger", "Mithril Dagger", 18, 12, 1900L, 0.96f, 35, WeaponCategory.DAGGER,
        "A lightweight mithril dagger"
    )
    val POISON_DAGGER = Weapon(
        "poison_dagger", "Poison Dagger", 24, 16, 1700L, 0.98f, 50, WeaponCategory.DAGGER,
        "A dagger coated with deadly poison"
    )
    val SHADOW_DAGGER = Weapon(
        "shadow_dagger", "Shadow Dagger", 30, 20, 1500L, 0.99f, 65, WeaponCategory.DAGGER,
        "A dagger that strikes from the shadows"
    )
    val ASSASSIN_DAGGER = Weapon(
        "assassin_dagger", "Assassin Dagger", 36, 24, 1300L, 1.0f, 80, WeaponCategory.DAGGER,
        "The perfect weapon for silent kills"
    )

    // Maces
    val BRONZE_MACE = Weapon(
        "bronze_mace", "Bronze Mace", 7, 4, 4200L, 0.75f, 5, WeaponCategory.MACE,
        "A blunt bronze weapon"
    )
    val IRON_MACE = Weapon(
        "iron_mace", "Iron Mace", 14, 8, 4000L, 0.8f, 15, WeaponCategory.MACE,
        "A crushing iron mace"
    )
    val STEEL_MACE = Weapon(
        "steel_mace", "Steel Mace", 21, 12, 3800L, 0.85f, 25, WeaponCategory.MACE,
        "A heavy steel mace"
    )
    val ADAMANT_MACE = Weapon(
        "adamant_mace", "Adamant Mace", 35, 20, 3400L, 0.9f, 45, WeaponCategory.MACE,
        "An adamant war mace"
    )
    val DRAGON_MACE = Weapon(
        "dragon_mace", "Dragon Mace", 49, 28, 3000L, 0.95f, 60, WeaponCategory.MACE,
        "A mace forged from dragon bone"
    )

    // Spears
    val BRONZE_SPEAR = Weapon(
        "bronze_spear", "Bronze Spear", 6, 2, 4000L, 0.8f, 5, WeaponCategory.SPEAR,
        "A long bronze spear"
    )
    val IRON_SPEAR = Weapon(
        "iron_spear", "Iron Spear", 12, 6, 3800L, 0.83f, 15, WeaponCategory.SPEAR,
        "A sturdy iron spear"
    )
    val STEEL_SPEAR = Weapon(
        "steel_spear", "Steel Spear", 18, 10, 3600L, 0.86f, 25, WeaponCategory.SPEAR,
        "A sharp steel spear"
    )
    val MITHRIL_SPEAR = Weapon(
        "mithril_spear", "Mithril Spear", 24, 14, 3400L, 0.89f, 40, WeaponCategory.SPEAR,
        "A lightweight mithril spear"
    )
    val DRAGON_SPEAR = Weapon(
        "dragon_spear", "Dragon Spear", 38, 22, 3000L, 0.94f, 60, WeaponCategory.SPEAR,
        "A legendary dragon spear"
    )

    val ALL = listOf(
        // Swords
        BRONZE_SWORD, IRON_SWORD, STEEL_SWORD, MITHRIL_SWORD, ADAMANT_SWORD, DRAGON_SWORD, RUNITE_SWORD, DRAGONITE_SWORD, CELESTIAL_SWORD, VOID_SWORD,
        // Axes
        BRONZE_AXE, IRON_AXE, STEEL_AXE, MITHRIL_AXE, ADAMANT_AXE, DRAGON_AXE, RUNITE_AXE, DRAGONITE_AXE,
        // Bows
        OAK_BOW, WILLOW_BOW, MAPLE_BOW, YEW_BOW, MAGIC_BOW, ELVEN_BOW, DRAGON_BOW, SHADOW_BOW,
        // Staffs
        APPRENTICE_STAFF, ADEPT_STAFF, MASTER_STAFF, ARCANE_STAFF, ELEMENTAL_STAFF, VOID_STAFF, COSMIC_STAFF,
        // Daggers
        BRONZE_DAGGER, IRON_DAGGER, STEEL_DAGGER, MITHRIL_DAGGER, POISON_DAGGER, SHADOW_DAGGER, ASSASSIN_DAGGER,
        // Maces
        BRONZE_MACE, IRON_MACE, STEEL_MACE, ADAMANT_MACE, DRAGON_MACE,
        // Spears
        BRONZE_SPEAR, IRON_SPEAR, STEEL_SPEAR, MITHRIL_SPEAR, DRAGON_SPEAR
    )
}

object Armors {
    // Leather Set
    val LEATHER_HELMET = Armor(
        "leather_helmet", "Leather Helmet", 2, 0, 1, ArmorSlot.HELMET,
        "Basic protection for your head"
    )
    val LEATHER_CHESTPLATE = Armor(
        "leather_chestplate", "Leather Chestplate", 5, 0, 1, ArmorSlot.CHESTPLATE,
        "Light armor for your torso"
    )
    val LEATHER_LEGGINGS = Armor(
        "leather_leggings", "Leather Leggings", 3, 0, 1, ArmorSlot.LEGGINGS,
        "Flexible leg protection"
    )
    val LEATHER_BOOTS = Armor(
        "leather_boots", "Leather Boots", 2, 0, 1, ArmorSlot.BOOTS,
        "Light foot protection"
    )
    val LEATHER_GLOVES = Armor(
        "leather_gloves", "Leather Gloves", 1, 0, 1, ArmorSlot.GLOVES,
        "Basic hand protection"
    )

    // Iron Set
    val IRON_HELMET = Armor(
        "iron_helmet", "Iron Helmet", 8, 5, 10, ArmorSlot.HELMET,
        "Solid iron protection"
    )
    val IRON_CHESTPLATE = Armor(
        "iron_chestplate", "Iron Chestplate", 15, 10, 10, ArmorSlot.CHESTPLATE,
        "Heavy iron armor"
    )
    val IRON_LEGGINGS = Armor(
        "iron_leggings", "Iron Leggings", 12, 7, 10, ArmorSlot.LEGGINGS,
        "Sturdy iron leg armor"
    )
    val IRON_BOOTS = Armor(
        "iron_boots", "Iron Boots", 6, 2, 12, ArmorSlot.BOOTS,
        "Sturdy iron boots"
    )
    val IRON_GLOVES = Armor(
        "iron_gloves", "Iron Gloves", 4, 1, 10, ArmorSlot.GLOVES,
        "Iron gauntlets"
    )
    val IRON_SHIELD = Armor(
        "iron_shield", "Iron Shield", 10, 5, 10, ArmorSlot.SHIELD,
        "A solid iron shield"
    )

    // Steel Set
    val STEEL_HELMET = Armor(
        "steel_helmet", "Steel Helmet", 14, 8, 20, ArmorSlot.HELMET,
        "Strong steel headgear"
    )
    val STEEL_CHESTPLATE = Armor(
        "steel_chestplate", "Steel Chestplate", 24, 14, 20, ArmorSlot.CHESTPLATE,
        "Heavy steel armor"
    )
    val STEEL_LEGGINGS = Armor(
        "steel_leggings", "Steel Leggings", 20, 12, 20, ArmorSlot.LEGGINGS,
        "Reinforced steel leg armor"
    )
    val STEEL_BOOTS = Armor(
        "steel_boots", "Steel Boots", 10, 4, 22, ArmorSlot.BOOTS,
        "Heavy steel boots"
    )
    val STEEL_GLOVES = Armor(
        "steel_gloves", "Steel Gloves", 8, 3, 20, ArmorSlot.GLOVES,
        "Steel-plated gauntlets"
    )
    val STEEL_SHIELD = Armor(
        "steel_shield", "Steel Shield", 18, 10, 20, ArmorSlot.SHIELD,
        "A reinforced steel shield"
    )

    // Mithril Set
    val MITHRIL_HELMET = Armor(
        "mithril_helmet", "Mithril Helmet", 20, 12, 30, ArmorSlot.HELMET,
        "Lightweight mithril headgear"
    )
    val MITHRIL_CHESTPLATE = Armor(
        "mithril_chestplate", "Mithril Chestplate", 32, 20, 30, ArmorSlot.CHESTPLATE,
        "Light but strong mithril armor"
    )
    val MITHRIL_LEGGINGS = Armor(
        "mithril_leggings", "Mithril Leggings", 28, 18, 30, ArmorSlot.LEGGINGS,
        "Flexible mithril leg armor"
    )
    val MITHRIL_BOOTS = Armor(
        "mithril_boots", "Mithril Boots", 14, 6, 36, ArmorSlot.BOOTS,
        "Light and strong boots"
    )
    val MITHRIL_GLOVES = Armor(
        "mithril_gloves", "Mithril Gloves", 12, 5, 30, ArmorSlot.GLOVES,
        "Elegant mithril gauntlets"
    )
    val MITHRIL_SHIELD = Armor(
        "mithril_shield", "Mithril Shield", 24, 15, 30, ArmorSlot.SHIELD,
        "A lightweight mithril shield"
    )

    // Adamant Set
    val ADAMANT_HELMET = Armor(
        "adamant_helmet", "Adamant Helmet", 26, 16, 40, ArmorSlot.HELMET,
        "Exceptionally strong headgear"
    )
    val ADAMANT_CHESTPLATE = Armor(
        "adamant_chestplate", "Adamant Chestplate", 42, 26, 40, ArmorSlot.CHESTPLATE,
        "Nearly impenetrable armor"
    )
    val ADAMANT_LEGGINGS = Armor(
        "adamant_leggings", "Adamant Leggings", 36, 22, 40, ArmorSlot.LEGGINGS,
        "Adamant leg protection"
    )
    val ADAMANT_BOOTS = Armor(
        "adamant_boots", "Adamant Boots", 18, 8, 48, ArmorSlot.BOOTS,
        "Exceptionally sturdy boots"
    )
    val ADAMANT_GLOVES = Armor(
        "adamant_gloves", "Adamant Gloves", 16, 7, 40, ArmorSlot.GLOVES,
        "Adamant gauntlets"
    )
    val ADAMANT_SHIELD = Armor(
        "adamant_shield", "Adamant Shield", 32, 20, 40, ArmorSlot.SHIELD,
        "An adamant tower shield"
    )

    // Dragon Set
    val DRAGON_HELMET = Armor(
        "dragon_helmet", "Dragon Helmet", 34, 22, 60, ArmorSlot.HELMET,
        "Helmet forged from dragon scales"
    )
    val DRAGON_CHESTPLATE = Armor(
        "dragon_chestplate", "Dragon Chestplate", 55, 35, 60, ArmorSlot.CHESTPLATE,
        "Armor made from dragon hide"
    )
    val DRAGON_LEGGINGS = Armor(
        "dragon_leggings", "Dragon Leggings", 48, 30, 60, ArmorSlot.LEGGINGS,
        "Leg armor of dragon origin"
    )
    val DRAGON_BOOTS = Armor(
        "dragon_boots", "Dragon Boots", 24, 12, 60, ArmorSlot.BOOTS,
        "Boots crafted from dragon scales"
    )
    val DRAGON_GLOVES = Armor(
        "dragon_gloves", "Dragon Gloves", 22, 10, 60, ArmorSlot.GLOVES,
        "Gloves imbued with dragon power"
    )
    val DRAGON_SHIELD = Armor(
        "dragon_shield", "Dragon Shield", 42, 28, 60, ArmorSlot.SHIELD,
        "A shield forged from dragonbone"
    )

    // Runite Set
    val RUNITE_HELMET = Armor(
        "runite_helmet", "Runite Helmet", 42, 28, 70, ArmorSlot.HELMET,
        "Helmet inscribed with ancient runes"
    )
    val RUNITE_CHESTPLATE = Armor(
        "runite_chestplate", "Runite Chestplate", 68, 45, 70, ArmorSlot.CHESTPLATE,
        "Chest armor pulsing with runic power"
    )
    val RUNITE_LEGGINGS = Armor(
        "runite_leggings", "Runite Leggings", 60, 40, 70, ArmorSlot.LEGGINGS,
        "Leg armor etched with power runes"
    )
    val RUNITE_BOOTS = Armor(
        "runite_boots", "Runite Boots", 30, 16, 70, ArmorSlot.BOOTS,
        "Boots that leave runic footprints"
    )
    val RUNITE_GLOVES = Armor(
        "runite_gloves", "Runite Gloves", 28, 14, 70, ArmorSlot.GLOVES,
        "Gloves that enhance magical abilities"
    )
    val RUNITE_SHIELD = Armor(
        "runite_shield", "Runite Shield", 52, 36, 70, ArmorSlot.SHIELD,
        "A shield that deflects magic"
    )

    // Dragonite Set
    val DRAGONITE_HELMET = Armor(
        "dragonite_helmet", "Dragonite Helmet", 50, 34, 80, ArmorSlot.HELMET,
        "Helmet infused with pure dragon essence"
    )
    val DRAGONITE_CHESTPLATE = Armor(
        "dragonite_chestplate", "Dragonite Chestplate", 82, 55, 80, ArmorSlot.CHESTPLATE,
        "Armor that radiates draconic power"
    )
    val DRAGONITE_LEGGINGS = Armor(
        "dragonite_leggings", "Dragonite Leggings", 72, 48, 80, ArmorSlot.LEGGINGS,
        "Leg armor forged from dragon essence"
    )
    val DRAGONITE_BOOTS = Armor(
        "dragonite_boots", "Dragonite Boots", 36, 20, 80, ArmorSlot.BOOTS,
        "Boots that grant dragon-like agility"
    )
    val DRAGONITE_GLOVES = Armor(
        "dragonite_gloves", "Dragonite Gloves", 34, 18, 80, ArmorSlot.GLOVES,
        "Gloves that channel draconic energy"
    )
    val DRAGONITE_SHIELD = Armor(
        "dragonite_shield", "Dragonite Shield", 62, 44, 80, ArmorSlot.SHIELD,
        "A shield blessed by ancient dragons"
    )

    val ALL = listOf(
        // Leather Set
        LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, LEATHER_GLOVES,
        // Iron Set
        IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS, IRON_GLOVES, IRON_SHIELD,
        // Steel Set
        STEEL_HELMET, STEEL_CHESTPLATE, STEEL_LEGGINGS, STEEL_BOOTS, STEEL_GLOVES, STEEL_SHIELD,
        // Mithril Set
        MITHRIL_HELMET, MITHRIL_CHESTPLATE, MITHRIL_LEGGINGS, MITHRIL_BOOTS, MITHRIL_GLOVES, MITHRIL_SHIELD,
        // Adamant Set
        ADAMANT_HELMET, ADAMANT_CHESTPLATE, ADAMANT_LEGGINGS, ADAMANT_BOOTS, ADAMANT_GLOVES, ADAMANT_SHIELD,
        // Dragon Set
        DRAGON_HELMET, DRAGON_CHESTPLATE, DRAGON_LEGGINGS, DRAGON_BOOTS, DRAGON_GLOVES, DRAGON_SHIELD,
        // Runite Set
        RUNITE_HELMET, RUNITE_CHESTPLATE, RUNITE_LEGGINGS, RUNITE_BOOTS, RUNITE_GLOVES, RUNITE_SHIELD,
        // Dragonite Set
        DRAGONITE_HELMET, DRAGONITE_CHESTPLATE, DRAGONITE_LEGGINGS, DRAGONITE_BOOTS, DRAGONITE_GLOVES, DRAGONITE_SHIELD
    )
}

object Areas {
    // Overworld Areas
    val STARTING_MEADOW = GameArea(
        "starting_meadow", "Starting Meadow", AreaType.OVERWORLD, 1, 10,
        "A peaceful meadow where beginners learn to fight"
    )
    val DARK_FOREST = GameArea(
        "dark_forest", "Dark Forest", AreaType.OVERWORLD, 15, 35,
        "A twisted forest filled with dangerous creatures"
    )
    val HAUNTED_CEMETERY = GameArea(
        "haunted_cemetery", "Haunted Cemetery", AreaType.OVERWORLD, 30, 60,
        "An ancient graveyard where the dead refuse to rest"
    )
    val MOUNTAIN_PEAKS = GameArea(
        "mountain_peaks", "Mountain Peaks", AreaType.OVERWORLD, 50, 80,
        "Treacherous mountain peaks with powerful beasts"
    )
    val DESERT_WASTELAND = GameArea(
        "desert_wasteland", "Desert Wasteland", AreaType.OVERWORLD, 70, 100,
        "A harsh desert inhabited by deadly creatures"
    )
    val FROZEN_TUNDRA = GameArea(
        "frozen_tundra", "Frozen Tundra", AreaType.OVERWORLD, 90, 120,
        "An icy wasteland where only the strongest survive"
    )
    val VOLCANIC_REGION = GameArea(
        "volcanic_region", "Volcanic Region", AreaType.OVERWORLD, 110, 140,
        "A land of fire and molten rock"
    )
    val SHADOW_REALM = GameArea(
        "shadow_realm", "Shadow Realm", AreaType.OVERWORLD, 130, 160,
        "A dimension where darkness reigns supreme"
    )
    val VOID_DIMENSION = GameArea(
        "void_dimension", "Void Dimension", AreaType.OVERWORLD, 150, 180,
        "The space between worlds, home to cosmic horrors"
    )
    val PRIMAL_JUNGLE = GameArea(
        "primal_jungle", "Primal Jungle", AreaType.OVERWORLD, 170, 200,
        "A dense jungle from the world's beginning"
    )
    val ETHEREAL_PLANE = GameArea(
        "ethereal_plane", "Ethereal Plane", AreaType.OVERWORLD, 180, 220,
        "A ghostly dimension overlapping reality"
    )
    val NIGHTMARE_REALM = GameArea(
        "nightmare_realm", "Nightmare Realm", AreaType.OVERWORLD, 200, 250,
        "A twisted realm born from collective fears"
    )
    val ASTRAL_WASTES = GameArea(
        "astral_wastes", "Astral Wastes", AreaType.OVERWORLD, 250, 300,
        "Desolate wastes floating in the astral plane"
    )
    val TEMPORAL_RIFTS = GameArea(
        "temporal_rifts", "Temporal Rifts", AreaType.OVERWORLD, 300, 350,
        "Unstable tears in the fabric of time itself"
    )
    val CHAOS_STORMS = GameArea(
        "chaos_storms", "Chaos Storms", AreaType.OVERWORLD, 350, 400,
        "Violent storms of pure chaotic energy"
    )
    val DIVINE_SANCTUM = GameArea(
        "divine_sanctum", "Divine Sanctum", AreaType.OVERWORLD, 400, 500,
        "The sacred realm where gods once walked"
    )
    val ETERNAL_BATTLEGROUND = GameArea(
        "eternal_battleground", "Eternal Battleground", AreaType.OVERWORLD, 500, 600,
        "The ultimate proving ground for the strongest warriors"
    )

    // Dungeon Areas
    val GOBLIN_CAVES = GameArea(
        "goblin_caves", "Goblin Caves", AreaType.DUNGEON, 5, 15,
        "Underground caves infested with goblin tribes", null, true, null
    )
    val SKELETON_CRYPTS = GameArea(
        "skeleton_crypts", "Skeleton Crypts", AreaType.DUNGEON, 12, 25,
        "Ancient crypts filled with animated skeletons", null, false, "goblin_caves"
    )
    val VAMPIRE_CASTLE = GameArea(
        "vampire_castle", "Vampire Castle", AreaType.DUNGEON, 20, 35,
        "A dark castle ruled by vampire lords", null, false, "skeleton_crypts"
    )
    val DRAGON_LAIR = GameArea(
        "dragon_lair", "Dragon Lair", AreaType.DUNGEON, 40, 60,
        "The dwelling place of ancient dragons", null, false, "vampire_castle"
    )
    val VOID_TEMPLE = GameArea(
        "void_temple", "Void Temple", AreaType.DUNGEON, 80, 100,
        "A temple dedicated to the void itself", null, false, "dragon_lair"
    )
    val BLOOD_CATHEDRAL = GameArea(
        "blood_cathedral", "Blood Cathedral", AreaType.DUNGEON, 25, 40,
        "A twisted cathedral where blood magic is practiced", null, false, "void_temple"
    )
    val POISON_SWAMP_DEPTHS = GameArea(
        "poison_swamp_depths", "Poison Swamp Depths", AreaType.DUNGEON, 30, 45,
        "The toxic depths beneath a poisonous swamp", null, false, "blood_cathedral"
    )
    val THUNDER_PEAKS_DUNGEON = GameArea(
        "thunder_peaks_dungeon", "Thunder Peaks Dungeon", AreaType.DUNGEON, 35, 50,
        "Lightning-struck caverns in the mountain peaks", null, false, "poison_swamp_depths"
    )
    val ABYSSAL_DEPTHS = GameArea(
        "abyssal_depths", "Abyssal Depths", AreaType.DUNGEON, 50, 70,
        "The deepest trenches of an underground ocean", null, false, "thunder_peaks_dungeon"
    )
    val NECROMANCER_TOWER = GameArea(
        "necromancer_tower", "Necromancer Tower", AreaType.DUNGEON, 40, 60,
        "A tower where dark necromancy is studied", null, false, "abyssal_depths"
    )
    val CLOCKWORK_CITADEL = GameArea(
        "clockwork_citadel", "Clockwork Citadel", AreaType.DUNGEON, 45, 65,
        "A mechanical fortress powered by gears and steam", null, false, "necromancer_tower"
    )
    val CHAOS_REALM = GameArea(
        "chaos_realm", "Chaos Realm", AreaType.DUNGEON, 60, 80,
        "A realm where reality bends and twists chaotically", null, false, "clockwork_citadel"
    )
    val PRIMORDIAL_CAVERNS = GameArea(
        "primordial_caverns", "Primordial Caverns", AreaType.DUNGEON, 65, 85,
        "Ancient caverns from the world's creation", null, false, "chaos_realm"
    )
    val STARFALL_SANCTUARY = GameArea(
        "starfall_sanctuary", "Starfall Sanctuary", AreaType.DUNGEON, 70, 90,
        "A sanctuary where fallen stars are worshipped", null, false, "primordial_caverns"
    )
    val DOOMSPIRE_KEEP = GameArea(
        "doomspire_keep", "Doomspire Keep", AreaType.DUNGEON, 75, 95,
        "A fortress that brings doom to all who enter", null, false, "starfall_sanctuary"
    )

    val ALL_AREAS = listOf(
        // Overworld Areas (17 total)
        STARTING_MEADOW, DARK_FOREST, HAUNTED_CEMETERY, MOUNTAIN_PEAKS,
        DESERT_WASTELAND, FROZEN_TUNDRA, VOLCANIC_REGION, SHADOW_REALM,
        VOID_DIMENSION, PRIMAL_JUNGLE, ETHEREAL_PLANE, NIGHTMARE_REALM,
        ASTRAL_WASTES, TEMPORAL_RIFTS, CHAOS_STORMS, DIVINE_SANCTUM, ETERNAL_BATTLEGROUND,
        // Dungeon Areas (15 total)
        GOBLIN_CAVES, SKELETON_CRYPTS, VAMPIRE_CASTLE, DRAGON_LAIR, VOID_TEMPLE,
        BLOOD_CATHEDRAL, POISON_SWAMP_DEPTHS, THUNDER_PEAKS_DUNGEON, ABYSSAL_DEPTHS,
        NECROMANCER_TOWER, CLOCKWORK_CITADEL, CHAOS_REALM, PRIMORDIAL_CAVERNS,
        STARFALL_SANCTUARY, DOOMSPIRE_KEEP
    )
}

object Enemies {
    // STARTING MEADOW (Level 1-10) - Peaceful creatures and basic threats
    val RAT = Enemy(
        "rat", "Giant Rat", 2, 15, 15, 1, 1, 1, 3000L, 0.6f, 8L, 1,
        "A large, aggressive rat", EnemyArea.STARTING_MEADOW
    )
    val RABBIT = Enemy(
        "rabbit", "Wild Rabbit", 1, 8, 8, 1, 1, 0, 2800L, 0.9f, 4L, 1,
        "A harmless forest rabbit", EnemyArea.STARTING_MEADOW
    )
    val SLIME = Enemy(
        "slime", "Blue Slime", 1, 12, 12, 1, 1, 0, 3500L, 0.5f, 6L, 1,
        "A gelatinous blob that absorbs attacks", EnemyArea.STARTING_MEADOW
    )
    val GREEN_SLIME = Enemy(
        "green_slime", "Green Slime", 2, 18, 18, 2, 1, 1, 3200L, 0.55f, 8L, 1,
        "A more aggressive acidic slime", EnemyArea.STARTING_MEADOW
    )
    val FIELD_MOUSE = Enemy(
        "field_mouse", "Field Mouse", 1, 6, 6, 1, 1, 0, 2500L, 0.95f, 3L, 1,
        "A tiny field mouse", EnemyArea.STARTING_MEADOW
    )
    val SPARROW = Enemy(
        "sparrow", "Sparrow", 2, 10, 10, 1, 1, 1, 2000L, 0.85f, 5L, 1,
        "A small songbird", EnemyArea.STARTING_MEADOW
    )
    val FROG = Enemy(
        "frog", "Giant Frog", 3, 20, 20, 2, 2, 1, 3000L, 0.7f, 10L, 1,
        "A large amphibian with a powerful tongue", EnemyArea.STARTING_MEADOW
    )
    val BUTTERFLY = Enemy(
        "butterfly", "Rainbow Butterfly", 1, 5, 5, 1, 1, 0, 1800L, 1.0f, 2L, 1,
        "A beautiful but fragile butterfly", EnemyArea.STARTING_MEADOW
    )
    val COW = Enemy(
        "cow", "Wild Cow", 4, 35, 35, 2, 2, 3, 4000L, 0.6f, 12L, 2,
        "A peaceful grazing animal", EnemyArea.STARTING_MEADOW
    )
    val SHEEP = Enemy(
        "sheep", "Wild Sheep", 3, 25, 25, 1, 1, 2, 3500L, 0.5f, 8L, 1,
        "A fluffy wool-bearing creature", EnemyArea.STARTING_MEADOW
    )

    // DARK FOREST (Level 8-20) - Predators and dark creatures
    val WOLF = Enemy(
        "wolf", "Wolf", 4, 28, 28, 3, 3, 2, 3200L, 0.7f, 14L, 8,
        "A swift and hungry wolf", EnemyArea.DARK_FOREST
    )
    val DIRE_WOLF = Enemy(
        "dire_wolf", "Dire Wolf", 8, 55, 55, 6, 5, 4, 3400L, 0.75f, 28L, 15,
        "A massive, aggressive wolf", EnemyArea.DARK_FOREST
    )
    val SPIDER = Enemy(
        "spider", "Giant Spider", 3, 22, 22, 2, 2, 1, 2800L, 0.65f, 12L, 8,
        "A venomous giant spider", EnemyArea.DARK_FOREST
    )
    val BLACK_WIDOW = Enemy(
        "black_widow", "Black Widow", 6, 35, 35, 4, 3, 3, 2600L, 0.8f, 20L, 12,
        "A deadly poisonous spider", EnemyArea.DARK_FOREST
    )
    val TARANTULA = Enemy(
        "tarantula", "Tarantula", 9, 65, 65, 7, 6, 5, 3000L, 0.73f, 32L, 7,
        "A massive hairy spider"
    )
    val BAT = Enemy(
        "bat", "Vampire Bat", 3, 18, 18, 2, 1, 1, 2500L, 0.8f, 10L, 8,
        "A quick flying pest", EnemyArea.DARK_FOREST
    )
    val GIANT_BAT = Enemy(
        "giant_bat", "Giant Bat", 7, 45, 45, 5, 4, 3, 2700L, 0.82f, 24L, 5,
        "A large cave-dwelling bat"
    )
    val SHADOW_WOLF = Enemy(
        "shadow_wolf", "Shadow Wolf", 12, 80, 80, 10, 8, 7, 3600L, 0.8f, 45L, 10,
        "A wolf touched by dark magic"
    )
    val FOREST_TROLL = Enemy(
        "forest_troll", "Forest Troll", 15, 120, 120, 14, 12, 10, 4500L, 0.72f, 65L, 12,
        "A troll that lives deep in the forest"
    )
    val WILD_BOAR = Enemy(
        "wild_boar", "Wild Boar", 6, 40, 40, 5, 4, 3, 3800L, 0.7f, 18L, 4,
        "An aggressive wild pig"
    )
    val OWLBEAR = Enemy(
        "owlbear", "Owlbear", 14, 95, 95, 12, 10, 9, 4000L, 0.75f, 55L, 11,
        "A fearsome hybrid creature"
    )
    val RAVEN = Enemy(
        "raven", "Giant Raven", 5, 22, 22, 3, 2, 2, 2400L, 0.9f, 15L, 10,
        "A large intelligent corvid", EnemyArea.DARK_FOREST
    )
    val HAWK = Enemy(
        "hawk", "Forest Hawk", 6, 28, 28, 4, 3, 2, 2200L, 0.95f, 18L, 12,
        "A swift bird of prey", EnemyArea.DARK_FOREST
    )

    // HAUNTED CEMETERY (Level 15-30) - Undead creatures
    val ZOMBIE = Enemy(
        "zombie", "Zombie", 9, 70, 70, 8, 5, 7, 4200L, 0.65f, 48L, 8,
        "A shambling undead"
    )
    val SKELETON = Enemy(
        "skeleton", "Skeleton", 15, 80, 80, 12, 10, 12, 3800L, 0.8f, 75L, 12,
        "An undead skeleton warrior"
    )
    val SKELETON_ARCHER = Enemy(
        "skeleton_archer", "Skeleton Archer", 16, 75, 75, 14, 8, 10, 3200L, 0.9f, 80L, 13,
        "A skeletal marksman"
    )
    val SKELETON_MAGE = Enemy(
        "skeleton_mage", "Skeleton Mage", 18, 65, 65, 16, 6, 12, 3600L, 0.88f, 95L, 15,
        "A skeletal spellcaster"
    )
    val GHOUL = Enemy(
        "ghoul", "Ghoul", 12, 85, 85, 10, 8, 8, 3800L, 0.75f, 58L, 10,
        "A flesh-eating undead"
    )
    val WRAITH = Enemy(
        "wraith", "Wraith", 20, 125, 125, 20, 12, 15, 3600L, 0.9f, 140L, 17,
        "A ghostly spirit that drains life"
    )
    val BANSHEE = Enemy(
        "banshee", "Banshee", 22, 110, 110, 18, 10, 16, 3400L, 0.92f, 160L, 19,
        "A wailing spirit of the dead"
    )
    val GRAVE_DIGGER = Enemy(
        "grave_digger", "Grave Digger", 17, 100, 100, 15, 12, 11, 4000L, 0.78f, 85L, 14,
        "An undead that tends the graveyard"
    )
    val PHANTOM = Enemy(
        "phantom", "Phantom", 25, 140, 140, 22, 14, 18, 3500L, 0.95f, 180L, 21,
        "A translucent spirit of vengeance"
    )
    val BONE_GOLEM = Enemy(
        "bone_golem", "Bone Golem", 28, 200, 200, 25, 18, 22, 4800L, 0.7f, 220L, 25,
        "A construct made of bones"
    )

    // MOUNTAIN PEAKS (Level 25-40) - Hardy mountain creatures
    val MOUNTAIN_GOAT = Enemy(
        "mountain_goat", "Mountain Goat", 20, 90, 90, 12, 8, 15, 3600L, 0.8f, 70L, 18,
        "A sure-footed mountain climber"
    )
    val SNOW_LEOPARD = Enemy(
        "snow_leopard", "Snow Leopard", 24, 110, 110, 18, 15, 12, 3200L, 0.9f, 95L, 22,
        "A stealthy mountain predator"
    )
    val GRIFFIN = Enemy(
        "griffin", "Griffin", 32, 180, 180, 26, 20, 18, 3800L, 0.85f, 200L, 28,
        "A majestic eagle-lion hybrid"
    )
    val MOUNTAIN_TROLL = Enemy(
        "mountain_troll", "Mountain Troll", 30, 220, 220, 28, 22, 25, 4600L, 0.72f, 180L, 26,
        "A massive troll adapted to mountain life"
    )
    val YETI = Enemy(
        "yeti", "Yeti", 35, 250, 250, 30, 25, 22, 4200L, 0.78f, 220L, 32,
        "A legendary mountain beast"
    )
    val ROCK_GIANT = Enemy(
        "rock_giant", "Rock Giant", 38, 300, 300, 35, 28, 30, 5000L, 0.7f, 280L, 35,
        "A living mountain of stone"
    )
    val STORM_EAGLE = Enemy(
        "storm_eagle", "Storm Eagle", 28, 140, 140, 22, 18, 16, 3000L, 0.95f, 150L, 25,
        "An eagle that commands lightning"
    )
    val ICE_GIANT = Enemy(
        "ice_giant", "Ice Giant", 40, 320, 320, 38, 30, 32, 5200L, 0.75f, 300L, 37,
        "A giant made of pure ice"
    )

    // DESERT WASTELAND (Level 35-50) - Desert survivors
    val SAND_WORM = Enemy(
        "sand_worm", "Sand Worm", 42, 280, 280, 35, 25, 20, 4800L, 0.7f, 250L, 38,
        "A massive worm that burrows through sand"
    )
    val DESERT_SCORPION = Enemy(
        "desert_scorpion", "Desert Scorpion", 38, 200, 200, 30, 22, 28, 3600L, 0.8f, 220L, 35,
        "A giant venomous scorpion"
    )
    val CACTUS_GOLEM = Enemy(
        "cactus_golem", "Cactus Golem", 35, 240, 240, 28, 20, 35, 4400L, 0.65f, 200L, 32,
        "A living cactus animated by magic"
    )
    val MIRAGE_ASSASSIN = Enemy(
        "mirage_assassin", "Mirage Assassin", 45, 180, 180, 40, 35, 25, 2800L, 0.95f, 320L, 42,
        "A deadly killer that strikes from mirages"
    )
    val FIRE_SALAMANDER = Enemy(
        "fire_salamander", "Fire Salamander", 40, 220, 220, 32, 28, 24, 3800L, 0.82f, 280L, 37,
        "A lizard that breathes fire"
    )
    val SAND_GOLEM = Enemy(
        "sand_golem", "Sand Golem", 43, 300, 300, 38, 28, 40, 4600L, 0.7f, 300L, 40,
        "A construct made of shifting sand"
    )
    val DESERT_WRAITH = Enemy(
        "desert_wraith", "Desert Wraith", 47, 200, 200, 42, 30, 35, 3400L, 0.9f, 350L, 44,
        "The spirit of one lost in the desert"
    )

    // GOBLIN CAVES DUNGEON (Level 5-15) - Goblin variants
    val GOBLIN = Enemy(
        "goblin", "Goblin", 5, 35, 35, 3, 2, 3, 3500L, 0.7f, 20L, 3,
        "A small but cunning goblin warrior"
    )
    val GOBLIN_ARCHER = Enemy(
        "goblin_archer", "Goblin Archer", 7, 30, 30, 5, 2, 2, 3000L, 0.85f, 25L, 5,
        "A goblin with a crude bow"
    )
    val GOBLIN_SHAMAN = Enemy(
        "goblin_shaman", "Goblin Shaman", 10, 45, 45, 8, 3, 5, 3800L, 0.8f, 35L, 8,
        "A goblin that practices dark magic"
    )
    val GOBLIN_CHIEF = Enemy(
        "goblin_chief", "Goblin Chief", 15, 80, 80, 12, 8, 8, 4000L, 0.78f, 60L, 12,
        "The leader of a goblin tribe"
    )
    val HOBGOBLIN = Enemy(
        "hobgoblin", "Hobgoblin", 12, 70, 70, 10, 7, 6, 3700L, 0.75f, 45L, 10,
        "A larger, more dangerous goblin"
    )
    val GOBLIN_BERSERKER = Enemy(
        "goblin_berserker", "Goblin Berserker", 14, 85, 85, 15, 12, 5, 3200L, 0.8f, 55L, 11,
        "A frenzied goblin warrior"
    )

    // SKELETON CRYPTS DUNGEON (Level 12-25) - Undead variants
    val CRYPT_GUARDIAN = Enemy(
        "crypt_guardian", "Crypt Guardian", 20, 150, 150, 18, 15, 20, 4200L, 0.8f, 100L, 18,
        "A skeletal guardian of the crypts"
    )
    val BONE_KNIGHT = Enemy(
        "bone_knight", "Bone Knight", 22, 140, 140, 20, 16, 18, 4000L, 0.85f, 120L, 20,
        "An armored skeletal warrior"
    )
    val SKELETON_LORD = Enemy(
        "skeleton_lord", "Skeleton Lord", 25, 180, 180, 24, 18, 22, 4400L, 0.88f, 150L, 23,
        "The ruler of the skeleton army"
    )
    val DEATH_KNIGHT = Enemy(
        "death_knight", "Death Knight", 28, 200, 200, 26, 20, 25, 4600L, 0.9f, 180L, 26,
        "A fallen paladin risen as undead"
    )

    // VAMPIRE CASTLE DUNGEON (Level 20-35) - Vampire variants
    val VAMPIRE_SPAWN = Enemy(
        "vampire_spawn", "Vampire Spawn", 22, 120, 120, 18, 14, 16, 3400L, 0.9f, 140L, 20,
        "A newly turned vampire"
    )
    val VAMPIRE = Enemy(
        "vampire", "Vampire", 26, 170, 170, 22, 16, 20, 3600L, 0.9f, 200L, 22,
        "A fast and deadly vampire"
    )
    val VAMPIRE_LORD = Enemy(
        "vampire_lord", "Vampire Lord", 32, 240, 240, 28, 22, 26, 3800L, 0.92f, 280L, 30,
        "An ancient and powerful vampire"
    )
    val VAMPIRE_BAT_SWARM = Enemy(
        "vampire_bat_swarm", "Vampire Bat Swarm", 25, 100, 100, 20, 15, 12, 2200L, 0.95f, 160L, 23,
        "A swarm of vampire bats"
    )

    // DRAGON LAIR DUNGEON (Level 40-60) - Dragon variants
    val WYRMLING = Enemy(
        "wyrmling", "Wyrmling", 42, 220, 220, 32, 25, 28, 4200L, 0.85f, 300L, 40,
        "A young dragon"
    )
    val DRAKE = Enemy(
        "drake", "Drake", 48, 280, 280, 38, 30, 32, 4600L, 0.88f, 400L, 45,
        "A smaller dragon without wings"
    )
    val WYVERN = Enemy(
        "wyvern", "Wyvern", 40, 260, 260, 26, 18, 24, 4200L, 0.85f, 380L, 34,
        "A fierce wyvern"
    )
    val DRAGON = Enemy(
        "dragon", "Young Dragon", 50, 300, 300, 25, 20, 25, 5000L, 0.9f, 500L, 40,
        "A fearsome young dragon"
    )
    val ANCIENT_DRAGON = Enemy(
        "ancient_dragon", "Ancient Dragon", 65, 600, 600, 60, 50, 55, 6000L, 0.92f, 1500L, 60,
        "An ancient wyrm of devastating power"
    )

    // VOID DIMENSION (Level 85-100) - Cosmic horrors
    val VOID_WALKER = Enemy(
        "void_walker", "Void Walker", 70, 500, 500, 70, 45, 60, 5500L, 0.95f, 2000L, 65,
        "A being that exists between dimensions"
    )
    val COSMIC_HORROR = Enemy(
        "cosmic_horror", "Cosmic Horror", 75, 700, 700, 75, 60, 65, 7000L, 0.88f, 2500L, 70,
        "An incomprehensible entity from beyond"
    )
    val PRIMORDIAL = Enemy(
        "primordial", "Primordial Being", 80, 800, 800, 80, 65, 70, 6500L, 0.9f, 3000L, 75,
        "One of the first beings to exist"
    )
    val WORLD_EATER = Enemy(
        "world_eater", "World Eater", 90, 1200, 1200, 100, 85, 90, 8000L, 0.85f, 5000L, 85,
        "A colossal entity that devours worlds"
    )
    val DEITY = Enemy(
        "deity", "Fallen Deity", 100, 1500, 1500, 120, 100, 110, 7500L, 0.95f, 10000L, 95,
        "A god stripped of divine power"
    )
    val VOID_DRAGON = Enemy(
        "void_dragon", "Void Dragon", 95, 1400, 1400, 110, 95, 100, 7800L, 0.9f, 8000L, 90,
        "A dragon that has consumed the void"
    )
    val REALITY_RIPPER = Enemy(
        "reality_ripper", "Reality Ripper", 85, 900, 900, 90, 75, 80, 6800L, 0.92f, 4000L, 82,
        "A being that tears through reality itself"
    )

    // BANDIT/HUMANOID ENEMIES (Various areas)
    val BANDIT = Enemy(
        "bandit", "Bandit", 7, 42, 42, 6, 4, 5, 3600L, 0.75f, 28L, 5,
        "A rogue looking for easy loot"
    )
    val KOBOLD = Enemy(
        "kobold", "Kobold", 6, 38, 38, 4, 3, 4, 3400L, 0.72f, 24L, 4,
        "A sneaky reptilian humanoid"
    )
    val BEAR = Enemy(
        "bear", "Cave Bear", 8, 55, 55, 7, 6, 4, 4000L, 0.7f, 35L, 6,
        "A massive cave-dwelling bear"
    )
    val GNOLL = Enemy(
        "gnoll", "Gnoll", 9, 48, 48, 8, 5, 6, 3800L, 0.74f, 32L, 7,
        "A hyena-like humanoid warrior"
    )
    val ORC = Enemy(
        "orc", "Orc", 10, 60, 60, 8, 6, 8, 4000L, 0.75f, 45L, 8,
        "A brutal orc with crude weapons"
    )
    val TROLL = Enemy(
        "troll", "Troll", 12, 90, 90, 12, 10, 8, 4200L, 0.7f, 70L, 10,
        "A hulking troll with thick skin"
    )
    val OGRE = Enemy(
        "ogre", "Ogre", 14, 105, 105, 15, 12, 10, 4500L, 0.68f, 85L, 12,
        "A brutish giant with a massive club"
    )
    val HARPY = Enemy(
        "harpy", "Harpy", 13, 85, 85, 11, 8, 9, 3200L, 0.85f, 75L, 11,
        "A winged creature with a piercing screech"
    )
    val MINOTAUR = Enemy(
        "minotaur", "Minotaur", 16, 120, 120, 16, 14, 12, 4800L, 0.76f, 95L, 14,
        "A bull-headed labyrinth guardian"
    )
    val MAGE = Enemy(
        "mage", "Mage", 18, 110, 110, 16, 8, 10, 3800L, 0.85f, 110L, 15,
        "A spellcaster with arcane power"
    )
    val KNIGHT = Enemy(
        "knight", "Knight", 22, 140, 140, 18, 14, 18, 3800L, 0.88f, 150L, 18,
        "A well‑armored warrior"
    )
    val ASSASSIN = Enemy(
        "assassin", "Shadow Assassin", 24, 130, 130, 22, 18, 14, 2800L, 0.95f, 180L, 20,
        "A deadly killer that strikes from shadows"
    )
    val WARLORD = Enemy(
        "warlord", "Orc Warlord", 25, 180, 180, 24, 20, 22, 4000L, 0.82f, 200L, 22,
        "A powerful orc commander"
    )

    // ELEMENTAL/MAGICAL ENEMIES (Various areas)
    val GOLEM = Enemy(
        "golem", "Stone Golem", 30, 220, 220, 28, 14, 26, 4400L, 0.7f, 280L, 28,
        "A living construct of stone"
    )
    val DEMON = Enemy(
        "demon", "Lesser Demon", 32, 240, 240, 30, 22, 24, 3800L, 0.85f, 320L, 30,
        "A fiend from the underworld"
    )
    val LICH = Enemy(
        "lich", "Lich", 35, 200, 200, 35, 18, 28, 4200L, 0.92f, 400L, 32,
        "An undead sorcerer of immense power"
    )
    val ELEMENTAL = Enemy(
        "elemental", "Fire Elemental", 33, 210, 210, 32, 24, 20, 3600L, 0.88f, 350L, 31,
        "A being of pure elemental fire"
    )
    val CHIMERA = Enemy(
        "chimera", "Chimera", 38, 280, 280, 36, 28, 30, 4600L, 0.84f, 450L, 35,
        "A three-headed beast of legend"
    )
    val TITAN = Enemy(
        "titan", "Stone Titan", 45, 350, 350, 40, 32, 38, 5200L, 0.78f, 600L, 40,
        "An ancient giant of immense power"
    )
    val PHOENIX = Enemy(
        "phoenix", "Phoenix", 42, 300, 300, 38, 30, 32, 4800L, 0.9f, 550L, 38,
        "A legendary bird that rises from ashes"
    )
    val HYDRA = Enemy(
        "hydra", "Hydra", 48, 380, 380, 42, 36, 35, 5000L, 0.82f, 700L, 44,
        "A multi-headed serpentine beast"
    )
    val ANGEL = Enemy(
        "angel", "Fallen Angel", 50, 320, 320, 45, 28, 40, 4400L, 0.94f, 800L, 46,
        "A once-pure being corrupted by darkness"
    )
    val ARCHLICH = Enemy(
        "archlich", "Archlich", 52, 300, 300, 50, 25, 45, 4600L, 0.96f, 900L, 48,
        "The most powerful of undead sorcerers"
    )

    // ASTRAL WASTES (Combat Level 250-300) - Astral beings
    val ASTRAL_DRIFTER = Enemy(
        "astral_drifter", "Astral Drifter", 75, 800, 800, 80, 70, 75, 6000L, 0.9f, 3500L, 250,
        "A wandering soul lost in the astral plane", EnemyArea.ASTRAL_WASTES
    )
    val VOID_PHANTOM = Enemy(
        "void_phantom", "Void Phantom", 80, 900, 900, 85, 75, 80, 6200L, 0.92f, 4000L, 260,
        "A ghostly entity from the void between worlds", EnemyArea.ASTRAL_WASTES
    )
    val ASTRAL_LEVIATHAN = Enemy(
        "astral_leviathan", "Astral Leviathan", 90, 1200, 1200, 100, 85, 90, 7000L, 0.88f, 5500L, 280,
        "A massive creature that swims through astral currents", EnemyArea.ASTRAL_WASTES
    )

    // TEMPORAL RIFTS (Combat Level 300-350) - Time-warped entities
    val CHRONO_STALKER = Enemy(
        "chrono_stalker", "Chrono Stalker", 85, 1000, 1000, 95, 80, 85, 6500L, 0.95f, 4500L, 300,
        "A predator that hunts across multiple timelines", EnemyArea.TEMPORAL_RIFTS
    )
    val TIME_WRAITH = Enemy(
        "time_wraith", "Time Wraith", 90, 1100, 1100, 100, 85, 90, 6800L, 0.93f, 5000L, 320,
        "A spirit torn from the flow of time", EnemyArea.TEMPORAL_RIFTS
    )
    val TEMPORAL_GUARDIAN = Enemy(
        "temporal_guardian", "Temporal Guardian", 95, 1400, 1400, 110, 95, 100, 7200L, 0.91f, 6000L, 340,
        "An ancient protector of the timestream", EnemyArea.TEMPORAL_RIFTS
    )

    // CHAOS STORMS (Combat Level 350-400) - Chaotic entities
    val CHAOS_ELEMENTAL = Enemy(
        "chaos_elemental", "Chaos Elemental", 100, 1300, 1300, 115, 90, 95, 7000L, 0.89f, 5800L, 350,
        "A being of pure chaotic energy", EnemyArea.CHAOS_STORMS
    )
    val STORM_TITAN = Enemy(
        "storm_titan", "Storm Titan", 105, 1500, 1500, 120, 100, 110, 7500L, 0.87f, 6500L, 370,
        "A colossal titan born from chaotic storms", EnemyArea.CHAOS_STORMS
    )
    val CHAOS_LORD = Enemy(
        "chaos_lord", "Chaos Lord", 110, 1700, 1700, 130, 110, 120, 8000L, 0.85f, 7500L, 390,
        "A powerful lord who commands chaos itself", EnemyArea.CHAOS_STORMS
    )

    // DIVINE SANCTUM (Combat Level 400-500) - Divine beings
    val SERAPH = Enemy(
        "seraph", "Seraph", 120, 1800, 1800, 140, 120, 130, 8500L, 0.93f, 8000L, 400,
        "A six-winged celestial guardian", EnemyArea.DIVINE_SANCTUM
    )
    val ARCHANGEL = Enemy(
        "archangel", "Archangel", 130, 2000, 2000, 150, 130, 140, 9000L, 0.95f, 9000L, 450,
        "A powerful celestial warrior", EnemyArea.DIVINE_SANCTUM
    )
    val DIVINE_AVATAR = Enemy(
        "divine_avatar", "Divine Avatar", 140, 2200, 2200, 160, 140, 150, 9500L, 0.97f, 10000L, 480,
        "The physical manifestation of divine will", EnemyArea.DIVINE_SANCTUM
    )

    // ETERNAL BATTLEGROUND (Combat Level 500-600) - Ultimate warriors
    val ETERNAL_CHAMPION = Enemy(
        "eternal_champion", "Eternal Champion", 150, 2500, 2500, 180, 160, 170, 10000L, 0.95f, 12000L, 500,
        "A warrior who has fought for eternity", EnemyArea.ETERNAL_BATTLEGROUND
    )
    val GODSLAYER = Enemy(
        "godslayer", "Godslayer", 160, 2800, 2800, 200, 180, 190, 11000L, 0.97f, 15000L, 550,
        "One who has slain gods and claimed their power", EnemyArea.ETERNAL_BATTLEGROUND
    )
    val ETERNAL_SOVEREIGN = Enemy(
        "eternal_sovereign", "Eternal Sovereign", 200, 3500, 3500, 250, 220, 230, 12000L, 0.99f, 25000L, 590,
        "The ultimate ruler of the eternal battleground", EnemyArea.ETERNAL_BATTLEGROUND
    )

    val ALL = listOf(
        // Tier 1
        SLIME, RAT, BAT, SPIDER, WOLF,
        // Tier 2
        GOBLIN, KOBOLD, BEAR, BANDIT, GNOLL,
        // Tier 3
        ZOMBIE, ORC, TROLL, HARPY, OGRE, MINOTAUR,
        // Tier 4
        SKELETON, MAGE, WRAITH, KNIGHT, ASSASSIN, WARLORD,
        // Tier 5
        VAMPIRE, GOLEM, DEMON, ELEMENTAL, LICH, CHIMERA,
        // Tier 6
        WYVERN, TITAN, PHOENIX, HYDRA, ANGEL, ARCHLICH,
        // Tier 7
        DRAGON, ANCIENT_DRAGON, VOID_WALKER, COSMIC_HORROR, PRIMORDIAL, WORLD_EATER, DEITY
    )
}

object LootTables {
    // Tier 1 Loot Tables
    val SLIME_LOOT = listOf(
        LootDrop("raw_meat", 1, 0.6f),
        LootDrop("copper_ore", 1, 0.2f)
    )
    val RAT_LOOT = listOf(
        LootDrop("raw_meat", 1, 0.8f),
        LootDrop("rat_tail", 1, 0.3f),
        LootDrop("rat_fur", 1, 0.4f),
        LootDrop("copper_ore", 1, 0.1f)
    )
    val RABBIT_LOOT = listOf(
        LootDrop("raw_meat", 1, 0.9f),
        LootDrop("rabbit_fur", 1, 0.8f),
        LootDrop("rabbit_foot", 1, 0.2f)
    )
    val SPARROW_LOOT = listOf(
        LootDrop("bird_feather", 2, 0.9f),
        LootDrop("bird_egg", 1, 0.3f)
    )
    val COW_LOOT = listOf(
        LootDrop("raw_meat", 3, 1.0f),
        LootDrop("cow_hide", 1, 0.7f),
        LootDrop("cow_horn", 1, 0.3f)
    )
    val SHEEP_LOOT = listOf(
        LootDrop("raw_meat", 2, 0.8f),
        LootDrop("wool_tuft", 2, 0.9f),
        LootDrop("sheep_horn", 1, 0.2f)
    )
    val BAT_LOOT = listOf(
        LootDrop("raw_meat", 1, 0.5f),
        LootDrop("copper_ore", 1, 0.15f)
    )
    val SPIDER_LOOT = listOf(
        LootDrop("spider_silk", 1, 0.7f),
        LootDrop("raw_meat", 1, 0.4f)
    )
    val WOLF_LOOT = listOf(
        LootDrop("wolf_pelt", 1, 0.7f),
        LootDrop("raw_meat", 1, 0.4f),
        LootDrop("wolf_fang", 1, 0.3f),
        LootDrop("copper_ore", 1, 0.2f)
    )
    val RAVEN_LOOT = listOf(
        LootDrop("black_feather", 3, 0.9f),
        LootDrop("bird_beak", 1, 0.4f),
        LootDrop("shiny_object", 1, 0.2f)
    )
    val HAWK_LOOT = listOf(
        LootDrop("hawk_feather", 2, 0.8f),
        LootDrop("bird_talon", 2, 0.6f),
        LootDrop("sharp_beak", 1, 0.3f)
    )

    // Tier 2 Loot Tables
    val GOBLIN_LOOT = listOf(
        LootDrop("goblin_ear", 1, 0.5f),
        LootDrop("copper_ore", 2, 0.4f),
        LootDrop("iron_ore", 1, 0.2f),
        LootDrop("bronze_sword", 1, 0.05f, true)
    )
    val BANDIT_LOOT = listOf(
        LootDrop("bandit_mask", 1, 0.4f),
        LootDrop("iron_ore", 1, 0.3f),
        LootDrop("iron_sword", 1, 0.08f, true)
    )
    val KOBOLD_LOOT = listOf(
        LootDrop("copper_ore", 2, 0.6f),
        LootDrop("iron_ore", 1, 0.3f),
        LootDrop("bronze_dagger", 1, 0.1f, true)
    )
    val BEAR_LOOT = listOf(
        LootDrop("bear_claw", 1, 0.6f),
        LootDrop("wolf_pelt", 2, 0.5f),
        LootDrop("raw_meat", 3, 0.8f)
    )
    val GNOLL_LOOT = listOf(
        LootDrop("iron_ore", 2, 0.5f),
        LootDrop("bronze_axe", 1, 0.1f, true),
        LootDrop("raw_meat", 2, 0.6f)
    )

    // Tier 3 Loot Tables
    val ORC_LOOT = listOf(
        LootDrop("orc_tooth", 1, 0.6f),
        LootDrop("iron_ore", 3, 0.7f),
        LootDrop("raw_meat", 2, 0.5f),
        LootDrop("iron_sword", 1, 0.1f, true)
    )
    val ZOMBIE_LOOT = listOf(
        LootDrop("zombie_fragment", 1, 0.7f),
        LootDrop("bone", 1, 0.6f),
        LootDrop("iron_ore", 1, 0.3f)
    )
    val TROLL_LOOT = listOf(
        LootDrop("troll_hide", 1, 0.5f),
        LootDrop("bone", 2, 0.5f),
        LootDrop("silver_ore", 1, 0.2f)
    )
    val OGRE_LOOT = listOf(
        LootDrop("bear_claw", 2, 0.6f),
        LootDrop("iron_ore", 3, 0.7f),
        LootDrop("steel_mace", 1, 0.08f, true)
    )
    val HARPY_LOOT = listOf(
        LootDrop("harpy_feather", 2, 0.8f),
        LootDrop("silver_ore", 1, 0.4f),
        LootDrop("maple_bow", 1, 0.1f, true)
    )
    val MINOTAUR_LOOT = listOf(
        LootDrop("demon_horn", 1, 0.3f),
        LootDrop("iron_ore", 4, 0.8f),
        LootDrop("steel_axe", 1, 0.12f, true)
    )

    // Tier 4 Loot Tables
    val SKELETON_LOOT = listOf(
        LootDrop("bone", 2, 0.9f),
        LootDrop("iron_ore", 2, 0.6f),
        LootDrop("steel_sword", 1, 0.08f, true),
        LootDrop("iron_helmet", 1, 0.05f, true)
    )
    val MAGE_LOOT = listOf(
        LootDrop("silver_ore", 2, 0.6f),
        LootDrop("gold_ore", 1, 0.2f),
        LootDrop("adept_staff", 1, 0.1f, true)
    )
    val KNIGHT_LOOT = listOf(
        LootDrop("iron_helmet", 1, 0.12f, true),
        LootDrop("iron_chestplate", 1, 0.12f, true),
        LootDrop("steel_sword", 1, 0.15f, true)
    )
    val WRAITH_LOOT = listOf(
        LootDrop("void_shard", 1, 0.3f),
        LootDrop("bone", 3, 0.8f),
        LootDrop("silver_ore", 2, 0.5f)
    )
    val ASSASSIN_LOOT = listOf(
        LootDrop("poison_dagger", 1, 0.15f, true),
        LootDrop("gold_ore", 2, 0.4f),
        LootDrop("shadow_dagger", 1, 0.05f, true)
    )
    val WARLORD_LOOT = listOf(
        LootDrop("orc_tooth", 3, 0.8f),
        LootDrop("steel_chestplate", 1, 0.2f, true),
        LootDrop("adamant_sword", 1, 0.08f, true)
    )

    // Tier 5 Loot Tables
    val VAMPIRE_LOOT = listOf(
        LootDrop("vampire_fang", 1, 0.6f),
        LootDrop("gold_ore", 2, 0.5f),
        LootDrop("mithril_ore", 1, 0.3f)
    )
    val GOLEM_LOOT = listOf(
        LootDrop("golem_core", 1, 0.5f),
        LootDrop("mithril_ore", 2, 0.6f),
        LootDrop("adamantite_ore", 1, 0.2f)
    )
    val DEMON_LOOT = listOf(
        LootDrop("demon_horn", 2, 0.7f),
        LootDrop("void_shard", 1, 0.4f),
        LootDrop("mithril_sword", 1, 0.12f, true)
    )
    val LICH_LOOT = listOf(
        LootDrop("lich_phylactery", 1, 0.3f),
        LootDrop("void_shard", 2, 0.6f),
        LootDrop("arcane_staff", 1, 0.15f, true)
    )
    val ELEMENTAL_LOOT = listOf(
        LootDrop("titan_essence", 1, 0.5f),
        LootDrop("mithril_ore", 3, 0.7f),
        LootDrop("elemental_staff", 1, 0.1f, true)
    )
    val CHIMERA_LOOT = listOf(
        LootDrop("dragon_scale", 2, 0.6f),
        LootDrop("harpy_feather", 3, 0.8f),
        LootDrop("dragon_sword", 1, 0.08f, true)
    )

    // Tier 6 Loot Tables
    val WYVERN_LOOT = listOf(
        LootDrop("wyvern_scale", 2, 0.7f),
        LootDrop("dragon_bone", 1, 0.4f),
        LootDrop("adamantite_ore", 2, 0.5f)
    )
    val TITAN_LOOT = listOf(
        LootDrop("titan_essence", 3, 0.8f),
        LootDrop("runite_ore", 2, 0.6f),
        LootDrop("dragon_axe", 1, 0.15f, true)
    )
    val PHOENIX_LOOT = listOf(
        LootDrop("harpy_feather", 5, 1.0f),
        LootDrop("dragon_scale", 3, 0.8f),
        LootDrop("elemental_staff", 1, 0.2f, true)
    )
    val HYDRA_LOOT = listOf(
        LootDrop("dragon_scale", 5, 0.9f),
        LootDrop("void_shard", 2, 0.6f),
        LootDrop("dragon_spear", 1, 0.12f, true)
    )
    val ANGEL_LOOT = listOf(
        LootDrop("angel_wing", 1, 0.8f),
        LootDrop("celestial_ore", 1, 0.4f),
        LootDrop("cosmic_staff", 1, 0.1f, true)
    )
    val ARCHLICH_LOOT = listOf(
        LootDrop("lich_phylactery", 2, 0.7f),
        LootDrop("void_shard", 3, 0.8f),
        LootDrop("void_staff", 1, 0.15f, true)
    )

    // Tier 7 Loot Tables
    val DRAGON_LOOT = listOf(
        LootDrop("dragon_scale", 3, 1.0f),
        LootDrop("dragon_bone", 1, 0.8f),
        LootDrop("runite_ore", 2, 0.6f),
        LootDrop("dragon_sword", 1, 0.2f, true)
    )
    val ANCIENT_DRAGON_LOOT = listOf(
        LootDrop("dragon_scale", 8, 1.0f),
        LootDrop("dragon_bone", 4, 1.0f),
        LootDrop("dragonite_ore", 3, 0.8f),
        LootDrop("dragonite_sword", 1, 0.25f, true),
        LootDrop("dragon_chestplate", 1, 0.15f, true)
    )
    val VOID_WALKER_LOOT = listOf(
        LootDrop("void_shard", 5, 1.0f),
        LootDrop("ethereal_ore", 2, 0.6f),
        LootDrop("void_sword", 1, 0.2f, true)
    )
    val COSMIC_HORROR_LOOT = listOf(
        LootDrop("void_shard", 8, 1.0f),
        LootDrop("celestial_ore", 3, 0.7f),
        LootDrop("cosmic_staff", 1, 0.3f, true)
    )
    val PRIMORDIAL_LOOT = listOf(
        LootDrop("titan_essence", 10, 1.0f),
        LootDrop("ethereal_ore", 5, 0.9f),
        LootDrop("celestial_sword", 1, 0.25f, true)
    )
    val WORLD_EATER_LOOT = listOf(
        LootDrop("void_shard", 15, 1.0f),
        LootDrop("ethereal_ore", 8, 1.0f),
        LootDrop("void_sword", 1, 0.4f, true),
        LootDrop("ethereal_sword", 1, 0.1f, true)
    )
    val DEITY_LOOT = listOf(
        LootDrop("angel_wing", 5, 1.0f),
        LootDrop("ethereal_ore", 10, 1.0f),
        LootDrop("celestial_sword", 1, 0.5f, true),
        LootDrop("ethereal_sword", 1, 0.2f, true)
    )

    fun getLootTable(enemyId: String): List<LootDrop> {
        return when (enemyId) {
            // Tier 1
            "slime" -> SLIME_LOOT
            "rat" -> RAT_LOOT
            "rabbit" -> RABBIT_LOOT
            "sparrow" -> SPARROW_LOOT
            "cow" -> COW_LOOT
            "sheep" -> SHEEP_LOOT
            "bat" -> BAT_LOOT
            "spider" -> SPIDER_LOOT
            "wolf" -> WOLF_LOOT
            "raven" -> RAVEN_LOOT
            "hawk" -> HAWK_LOOT
            // Tier 2
            "goblin" -> GOBLIN_LOOT
            "bandit" -> BANDIT_LOOT
            "kobold" -> KOBOLD_LOOT
            "bear" -> BEAR_LOOT
            "gnoll" -> GNOLL_LOOT
            // Tier 3
            "orc" -> ORC_LOOT
            "zombie" -> ZOMBIE_LOOT
            "troll" -> TROLL_LOOT
            "ogre" -> OGRE_LOOT
            "harpy" -> HARPY_LOOT
            "minotaur" -> MINOTAUR_LOOT
            // Tier 4
            "skeleton" -> SKELETON_LOOT
            "mage" -> MAGE_LOOT
            "knight" -> KNIGHT_LOOT
            "wraith" -> WRAITH_LOOT
            "assassin" -> ASSASSIN_LOOT
            "warlord" -> WARLORD_LOOT
            // Tier 5
            "vampire" -> VAMPIRE_LOOT
            "golem" -> GOLEM_LOOT
            "demon" -> DEMON_LOOT
            "lich" -> LICH_LOOT
            "elemental" -> ELEMENTAL_LOOT
            "chimera" -> CHIMERA_LOOT
            // Tier 6
            "wyvern" -> WYVERN_LOOT
            "titan" -> TITAN_LOOT
            "phoenix" -> PHOENIX_LOOT
            "hydra" -> HYDRA_LOOT
            "angel" -> ANGEL_LOOT
            "archlich" -> ARCHLICH_LOOT
            // Tier 7
            "dragon" -> DRAGON_LOOT
            "ancient_dragon" -> ANCIENT_DRAGON_LOOT
            "void_walker" -> VOID_WALKER_LOOT
            "cosmic_horror" -> COSMIC_HORROR_LOOT
            "primordial" -> PRIMORDIAL_LOOT
            "world_eater" -> WORLD_EATER_LOOT
            "deity" -> DEITY_LOOT
            else -> emptyList()
        }
    }
}
