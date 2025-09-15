package com.eternalquest.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.eternalquest.data.dao.*
import com.eternalquest.data.entities.*

@Database(
    entities = [
        Player::class,
        Skill::class,
        Item::class,
        BankItem::class,
        ActivityState::class,
        CombatStats::class,
        CurrentEnemy::class,
        PlayerUpgrades::class,
        GoldBalance::class,
        com.eternalquest.data.entities.SigilPerks::class
    ],
    version = 9,
    exportSchema = false
)
abstract class EternalQuestDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun skillDao(): SkillDao
    abstract fun itemDao(): ItemDao
    abstract fun bankDao(): BankDao
    abstract fun combatDao(): CombatDao
    abstract fun upgradesDao(): UpgradesDao
    abstract fun sigilPerksDao(): com.eternalquest.data.dao.SigilPerksDao
    
    companion object {
        @Volatile
        private var INSTANCE: EternalQuestDatabase? = null
        
        fun getDatabase(context: Context): EternalQuestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EternalQuestDatabase::class.java,
                    "eternal_quest_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create combat_stats table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS combat_stats (
                        playerId INTEGER PRIMARY KEY NOT NULL,
                        hitpoints INTEGER NOT NULL,
                        maxHitpoints INTEGER NOT NULL,
                        attack INTEGER NOT NULL,
                        strength INTEGER NOT NULL,
                        defense INTEGER NOT NULL,
                        combatLevel INTEGER NOT NULL,
                        combatXp INTEGER NOT NULL,
                        equippedWeapon TEXT,
                        equippedArmor TEXT,
                        autoEatEnabled INTEGER NOT NULL,
                        autoEatFoodId TEXT,
                        isInCombat INTEGER NOT NULL,
                        currentEnemyId TEXT,
                        combatStartTime INTEGER,
                        lastPlayerAttack INTEGER,
                        lastEnemyAttack INTEGER
                    )
                """.trimIndent())
                
                // Create current_enemy table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS current_enemy (
                        id INTEGER PRIMARY KEY NOT NULL,
                        enemyId TEXT NOT NULL,
                        currentHp INTEGER NOT NULL,
                        maxHp INTEGER NOT NULL,
                        nextAttackTime INTEGER NOT NULL,
                        combatStartTime INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }
        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create player_upgrades table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS player_upgrades (
                        playerId INTEGER PRIMARY KEY NOT NULL,
                        bankTabsPurchased INTEGER NOT NULL DEFAULT 0,
                        bankSlotsPerTab INTEGER NOT NULL DEFAULT 15,
                        activityQueueLength INTEGER NOT NULL DEFAULT 1,
                        offlineCapHours INTEGER NOT NULL DEFAULT 168,
                        autoSellEnabled INTEGER NOT NULL DEFAULT 0,
                        sortingToolsUnlocked INTEGER NOT NULL DEFAULT 0,
                        themeUnlocked TEXT NOT NULL DEFAULT 'default',
                        totalGoldSpent INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                
                // Create gold_balance table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS gold_balance (
                        playerId INTEGER PRIMARY KEY NOT NULL,
                        currentGold INTEGER NOT NULL DEFAULT 0,
                        totalEarned INTEGER NOT NULL DEFAULT 0,
                        totalSpent INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                
                // Initialize default records
                database.execSQL("INSERT OR IGNORE INTO player_upgrades (playerId) VALUES (1)")
                database.execSQL("INSERT OR IGNORE INTO gold_balance (playerId) VALUES (1)")
            }
        }
        
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add offlineEfficiencyLevel column to player_upgrades table
                database.execSQL("ALTER TABLE player_upgrades ADD COLUMN offlineEfficiencyLevel INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add ascension fields to players
                database.execSQL("ALTER TABLE players ADD COLUMN ascensionCount INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE players ADD COLUMN etherealSigils INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create sigil_perks table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS sigil_perks (
                        playerId INTEGER PRIMARY KEY NOT NULL,
                        xpBonusLevel INTEGER NOT NULL DEFAULT 0,
                        speedBonusLevel INTEGER NOT NULL DEFAULT 0,
                        lootBonusLevel INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                database.execSQL("INSERT OR IGNORE INTO sigil_perks (playerId) VALUES (1)")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to sigil_perks if migrating from earlier 6 schema
                database.execSQL("ALTER TABLE sigil_perks ADD COLUMN speedBonusLevel INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE sigil_perks ADD COLUMN lootBonusLevel INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add autoEatThreshold to combat_stats
                database.execSQL("ALTER TABLE combat_stats ADD COLUMN autoEatThreshold REAL NOT NULL DEFAULT 0.5")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add useBestAutoEat to combat_stats
                database.execSQL("ALTER TABLE combat_stats ADD COLUMN useBestAutoEat INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
