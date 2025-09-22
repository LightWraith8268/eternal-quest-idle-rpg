package com.eternalquest.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_upgrades")
data class PlayerUpgrades(
    @PrimaryKey val playerId: Int = 1,
    val bankTabsPurchased: Int = 0, // Additional tabs beyond base 5
    val bankSlotsPerTab: Int = 15, // Slots per tab (base 15, up to 50)
    val activityQueueLength: Int = 1, // How many activities can be queued
    val offlineCapHours: Int = 168, // Offline cap in hours (base 168 = 7 days)
    val offlineEfficiencyLevel: Int = 0, // 0=33%, 1=40%, 2=50%, 3=60%
    val autoSellEnabled: Boolean = false, // Auto-sell common items
    val sortingToolsUnlocked: Boolean = false, // Bank sorting features
    val themeUnlocked: String = "default", // Current theme
    val totalGoldSpent: Long = 0L // Track total spending for achievements
)

@Entity(tableName = "gold_balance")
data class GoldBalance(
    @PrimaryKey val playerId: Int = 1,
    val currentGold: Long = 0L,
    val totalEarned: Long = 0L,
    val totalSpent: Long = 0L
)

data class QoLUpgrade(
    val id: String,
    val name: String,
    val description: String,
    val category: UpgradeCategory,
    val baseCost: Long,
    val maxLevel: Int,
    val costMultiplier: Float = 1.5f,
    val currentLevel: Int = 0,
    val isUnlocked: Boolean = true
)

enum class UpgradeCategory {
    BANK_EXPANSION,
    ACTIVITY_QUEUE,
    OFFLINE_TIME,
    CONVENIENCE,
    COSMETIC
}

object QoLUpgrades {
    // Bank Expansion Upgrades
    val BANK_TAB_6 = QoLUpgrade(
        "bank_tab_6", "6th Bank Tab", "Unlock a 6th bank tab for more storage",
        UpgradeCategory.BANK_EXPANSION, 1000L, 1
    )
    
    val BANK_TAB_7 = QoLUpgrade(
        "bank_tab_7", "7th Bank Tab", "Unlock a 7th bank tab",
        UpgradeCategory.BANK_EXPANSION, 2500L, 1
    )
    
    val BANK_TAB_8 = QoLUpgrade(
        "bank_tab_8", "8th Bank Tab", "Unlock an 8th bank tab",
        UpgradeCategory.BANK_EXPANSION, 5000L, 1
    )
    
    val BANK_TAB_9 = QoLUpgrade(
        "bank_tab_9", "9th Bank Tab", "Unlock a 9th bank tab",
        UpgradeCategory.BANK_EXPANSION, 10000L, 1
    )
    
    val BANK_TAB_10 = QoLUpgrade(
        "bank_tab_10", "10th Bank Tab", "Unlock a 10th bank tab",
        UpgradeCategory.BANK_EXPANSION, 25000L, 1
    )
    
    val BANK_SLOTS_20 = QoLUpgrade(
        "bank_slots_20", "20 Slots Per Tab", "Increase bank slots from 15 to 20 per tab",
        UpgradeCategory.BANK_EXPANSION, 3000L, 1
    )
    
    val BANK_SLOTS_25 = QoLUpgrade(
        "bank_slots_25", "25 Slots Per Tab", "Increase bank slots from 20 to 25 per tab",
        UpgradeCategory.BANK_EXPANSION, 7500L, 1
    )
    
    val BANK_SLOTS_30 = QoLUpgrade(
        "bank_slots_30", "30 Slots Per Tab", "Increase bank slots from 25 to 30 per tab",
        UpgradeCategory.BANK_EXPANSION, 15000L, 1
    )
    
    val BANK_SLOTS_40 = QoLUpgrade(
        "bank_slots_40", "40 Slots Per Tab", "Increase bank slots from 30 to 40 per tab",
        UpgradeCategory.BANK_EXPANSION, 35000L, 1
    )
    
    val BANK_SLOTS_50 = QoLUpgrade(
        "bank_slots_50", "50 Slots Per Tab", "Maximum bank slots: 50 per tab",
        UpgradeCategory.BANK_EXPANSION, 75000L, 1
    )
    
    // Activity Queue Upgrades
    val QUEUE_LENGTH_2 = QoLUpgrade(
        "queue_2", "Queue Length: 2", "Queue up to 2 activities in advance",
        UpgradeCategory.ACTIVITY_QUEUE, 2000L, 1
    )
    
    val QUEUE_LENGTH_3 = QoLUpgrade(
        "queue_3", "Queue Length: 3", "Queue up to 3 activities in advance",
        UpgradeCategory.ACTIVITY_QUEUE, 5000L, 1
    )
    
    val QUEUE_LENGTH_5 = QoLUpgrade(
        "queue_5", "Queue Length: 5", "Queue up to 5 activities in advance",
        UpgradeCategory.ACTIVITY_QUEUE, 12000L, 1
    )
    
    val QUEUE_LENGTH_10 = QoLUpgrade(
        "queue_10", "Queue Length: 10", "Queue up to 10 activities in advance",
        UpgradeCategory.ACTIVITY_QUEUE, 30000L, 1
    )
    
    // Offline Time Extensions (within 7-day max)
    val OFFLINE_RATE_40 = QoLUpgrade(
        "offline_40", "40% Offline Rate", "Increase offline efficiency from 33% to 40%",
        UpgradeCategory.OFFLINE_TIME, 8000L, 1
    )
    
    val OFFLINE_RATE_50 = QoLUpgrade(
        "offline_50", "50% Offline Rate", "Increase offline efficiency from 40% to 50%",
        UpgradeCategory.OFFLINE_TIME, 20000L, 1
    )
    
    val OFFLINE_RATE_60 = QoLUpgrade(
        "offline_60", "60% Offline Rate", "Increase offline efficiency from 50% to 60%",
        UpgradeCategory.OFFLINE_TIME, 50000L, 1
    )
    
    // Convenience Upgrades
    val AUTO_SELL = QoLUpgrade(
        "auto_sell", "Auto-Sell", "Automatically sell common items when bank is full",
        UpgradeCategory.CONVENIENCE, 4000L, 1
    )
    
    val SORTING_TOOLS = QoLUpgrade(
        "sorting_tools", "Sorting Tools", "Sort bank by type, value, or quantity",
        UpgradeCategory.CONVENIENCE, 6000L, 1
    )
    
    val BANK_SEARCH = QoLUpgrade(
        "bank_search", "Bank Search", "Search for items across all bank tabs",
        UpgradeCategory.CONVENIENCE, 8000L, 1
    )
    
    val ACTIVITY_PRESETS = QoLUpgrade(
        "activity_presets", "Activity Presets", "Save and load activity sequences",
        UpgradeCategory.CONVENIENCE, 15000L, 1
    )
    
    // Cosmetic Upgrades
    val DARK_THEME = QoLUpgrade(
        "dark_theme", "Dark Theme", "Unlock dark theme for the UI",
        UpgradeCategory.COSMETIC, 1500L, 1
    )
    
    val COMPACT_THEME = QoLUpgrade(
        "compact_theme", "Compact Theme", "Compact UI for more information density",
        UpgradeCategory.COSMETIC, 2500L, 1
    )
    
    val ANIMATED_BACKGROUNDS = QoLUpgrade(
        "animated_bg", "Animated Backgrounds", "Subtle background animations",
        UpgradeCategory.COSMETIC, 5000L, 1
    )
    
    var ALL = listOf(
        // Bank expansion (ordered by dependency)
        BANK_TAB_6, BANK_TAB_7, BANK_TAB_8, BANK_TAB_9, BANK_TAB_10,
        BANK_SLOTS_20, BANK_SLOTS_25, BANK_SLOTS_30, BANK_SLOTS_40, BANK_SLOTS_50,
        
        // Activity queue
        QUEUE_LENGTH_2, QUEUE_LENGTH_3, QUEUE_LENGTH_5, QUEUE_LENGTH_10,
        
        // Offline improvements
        OFFLINE_RATE_40, OFFLINE_RATE_50, OFFLINE_RATE_60,
        
        // Convenience
        AUTO_SELL, SORTING_TOOLS, BANK_SEARCH, ACTIVITY_PRESETS,
        
        // Cosmetic
        DARK_THEME, COMPACT_THEME, ANIMATED_BACKGROUNDS
    )
    
    fun getUpgradesByCategory(category: UpgradeCategory): List<QoLUpgrade> {
        return ALL.filter { it.category == category }
    }
    
    fun calculateUpgradeCost(upgrade: QoLUpgrade, currentLevel: Int): Long {
        if (currentLevel >= upgrade.maxLevel) return 0L
        return (upgrade.baseCost * Math.pow(upgrade.costMultiplier.toDouble(), currentLevel.toDouble())).toLong()
    }
    
    fun getTotalBankTabs(upgrades: PlayerUpgrades): Int {
        return 5 + upgrades.bankTabsPurchased // Base 5 + purchased
    }
    
    fun getBankSlotsPerTab(upgrades: PlayerUpgrades): Int {
        return upgrades.bankSlotsPerTab
    }
    
    fun getOfflineEfficiencyRate(upgrades: PlayerUpgrades): Float {
        // Base rate is 33% (0.33f)
        return when (upgrades.offlineEfficiencyLevel) {
            3 -> 0.6f // offline_60 purchased
            2 -> 0.5f // offline_50 purchased  
            1 -> 0.4f // offline_40 purchased
            else -> 0.33f // base rate
        }
    }
    
    fun applyConfig(context: android.content.Context) {
        try {
            com.eternalquest.util.UpgradeConfig.load(context)
            val mapped = ALL.associateBy { it.id }.toMutableMap()
            val updated = mapped.mapValues { (id, up) ->
                val def = com.eternalquest.util.UpgradeConfig.get(id)
                if (def != null) {
                    up.copy(
                        baseCost = def.baseCost ?: up.baseCost,
                        maxLevel = def.maxLevel ?: up.maxLevel,
                        costMultiplier = def.costMultiplier ?: up.costMultiplier
                    )
                } else up
            }
            ALL = updated.values.toList()
        } catch (_: Exception) { /* ignore */ }
    }
    
    private fun hasUpgrade(upgrades: PlayerUpgrades, upgradeId: String): Boolean {
        return when (upgradeId) {
            "offline_40" -> upgrades.offlineEfficiencyLevel >= 1
            "offline_50" -> upgrades.offlineEfficiencyLevel >= 2
            "offline_60" -> upgrades.offlineEfficiencyLevel >= 3
            else -> false
        }
    }
}

// Gold earning sources
object GoldSources {
    fun getItemSellValue(itemId: String, quantity: Int): Long {
        val item = try {
            com.eternalquest.util.ItemCatalog.get(itemId)
        } catch (_: Exception) {
            null
        } ?: GameItems.ALL.find { it.id == itemId }
        return if (item != null) {
            (item.value * quantity * 0.8f).toLong() // 80% of item value
        } else {
            0L
        }
    }
    
    fun getCombatGoldReward(enemyLevel: Int): Long {
        return enemyLevel * 5L + (1..10).random() // 5 gold per level + 1-10 random
    }
    
    fun getActivityGoldReward(skillLevel: Int): Long {
        return if ((1..100).random() <= 5) { // 5% chance
            skillLevel * 2L + (1..5).random() // Bonus gold
        } else {
            0L
        }
    }
}
