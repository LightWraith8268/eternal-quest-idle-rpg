package com.eternalquest.game.systems

import com.eternalquest.data.dao.UpgradesDao
import com.eternalquest.data.dao.BankDao
import com.eternalquest.data.entities.*

class UpgradeSystem(
    private val upgradesDao: UpgradesDao,
    private val bankDao: BankDao
) {
    
    suspend fun initializeUpgrades() {
        val existingUpgrades = upgradesDao.getPlayerUpgradesSync()
        if (existingUpgrades == null) {
            upgradesDao.insertPlayerUpgrades(PlayerUpgrades())
        }
        
        val existingGold = upgradesDao.getGoldBalanceSync()
        if (existingGold == null) {
            upgradesDao.insertGoldBalance(GoldBalance())
        }
    }
    
    suspend fun canAffordUpgrade(upgradeId: String): Boolean {
        val upgrade = QoLUpgrades.ALL.find { it.id == upgradeId } ?: return false
        val currentGold = upgradesDao.getCurrentGold() ?: 0L
        val cost = QoLUpgrades.calculateUpgradeCost(upgrade, upgrade.currentLevel)
        return currentGold >= cost
    }
    
    suspend fun purchaseUpgrade(upgradeId: String): UpgradeResult {
        val upgrade = QoLUpgrades.ALL.find { it.id == upgradeId }
            ?: return UpgradeResult.NotFound
        
        val currentGold = upgradesDao.getCurrentGold() ?: 0L
        val cost = QoLUpgrades.calculateUpgradeCost(upgrade, upgrade.currentLevel)
        
        if (currentGold < cost) {
            return UpgradeResult.InsufficientFunds(cost, currentGold)
        }
        
        val playerUpgrades = upgradesDao.getPlayerUpgradesSync()
            ?: return UpgradeResult.Error("Player upgrades not found")
        
        // Check prerequisites
        val prerequisiteCheck = checkPrerequisites(upgradeId, playerUpgrades)
        if (!prerequisiteCheck.first) {
            return UpgradeResult.PrerequisiteNotMet(prerequisiteCheck.second)
        }
        
        // Apply the upgrade
        return try {
            upgradesDao.purchaseUpgrade(cost) {
                applyUpgrade(upgradeId, playerUpgrades)
            }
            UpgradeResult.Success(upgradeId, cost)
        } catch (e: Exception) {
            UpgradeResult.Error("Failed to apply upgrade: ${e.message}")
        }
    }
    
    private suspend fun applyUpgrade(upgradeId: String, currentUpgrades: PlayerUpgrades) {
        when (upgradeId) {
            // Bank Tab Upgrades
            "bank_tab_6", "bank_tab_7", "bank_tab_8", "bank_tab_9", "bank_tab_10" -> {
                upgradesDao.purchaseBankTab()
            }
            
            // Bank Slot Upgrades
            "bank_slots_20" -> upgradesDao.upgradeBankSlots(20)
            "bank_slots_25" -> upgradesDao.upgradeBankSlots(25)
            "bank_slots_30" -> upgradesDao.upgradeBankSlots(30)
            "bank_slots_40" -> upgradesDao.upgradeBankSlots(40)
            "bank_slots_50" -> upgradesDao.upgradeBankSlots(50)
            
            // Queue Upgrades
            "queue_2" -> upgradesDao.upgradeQueueLength(2)
            "queue_3" -> upgradesDao.upgradeQueueLength(3)
            "queue_5" -> upgradesDao.upgradeQueueLength(5)
            "queue_10" -> upgradesDao.upgradeQueueLength(10)
            
            // Offline Rate Upgrades
            "offline_40" -> upgradesDao.upgradeOfflineEfficiency(1)
            "offline_50" -> upgradesDao.upgradeOfflineEfficiency(2)
            "offline_60" -> upgradesDao.upgradeOfflineEfficiency(3)
            
            // Convenience Upgrades
            "auto_sell" -> upgradesDao.setAutoSell(true)
            "sorting_tools" -> upgradesDao.unlockSortingTools(true)
            
            // Theme Upgrades
            "dark_theme" -> upgradesDao.unlockTheme("dark")
            "compact_theme" -> upgradesDao.unlockTheme("compact")
            "animated_bg" -> upgradesDao.unlockTheme("animated")
        }
    }
    
    private fun checkPrerequisites(upgradeId: String, upgrades: PlayerUpgrades): Pair<Boolean, String> {
        return when (upgradeId) {
            "bank_tab_7" -> {
                val hasTab6 = upgrades.bankTabsPurchased >= 1
                Pair(hasTab6, "Requires 6th Bank Tab")
            }
            "bank_tab_8" -> {
                val hasTab7 = upgrades.bankTabsPurchased >= 2
                Pair(hasTab7, "Requires 7th Bank Tab")
            }
            "bank_tab_9" -> {
                val hasTab8 = upgrades.bankTabsPurchased >= 3
                Pair(hasTab8, "Requires 8th Bank Tab")
            }
            "bank_tab_10" -> {
                val hasTab9 = upgrades.bankTabsPurchased >= 4
                Pair(hasTab9, "Requires 9th Bank Tab")
            }
            
            "bank_slots_25" -> {
                val has20Slots = upgrades.bankSlotsPerTab >= 20
                Pair(has20Slots, "Requires 20 Slots Per Tab")
            }
            "bank_slots_30" -> {
                val has25Slots = upgrades.bankSlotsPerTab >= 25
                Pair(has25Slots, "Requires 25 Slots Per Tab")
            }
            "bank_slots_40" -> {
                val has30Slots = upgrades.bankSlotsPerTab >= 30
                Pair(has30Slots, "Requires 30 Slots Per Tab")
            }
            "bank_slots_50" -> {
                val has40Slots = upgrades.bankSlotsPerTab >= 40
                Pair(has40Slots, "Requires 40 Slots Per Tab")
            }
            
            "queue_3" -> {
                val hasQueue2 = upgrades.activityQueueLength >= 2
                Pair(hasQueue2, "Requires Queue Length: 2")
            }
            "queue_5" -> {
                val hasQueue3 = upgrades.activityQueueLength >= 3
                Pair(hasQueue3, "Requires Queue Length: 3")
            }
            "queue_10" -> {
                val hasQueue5 = upgrades.activityQueueLength >= 5
                Pair(hasQueue5, "Requires Queue Length: 5")
            }
            
            "offline_50" -> {
                val hasOffline40 = upgrades.offlineEfficiencyLevel >= 1
                Pair(hasOffline40, "Requires 40% Offline Rate")
            }
            "offline_60" -> {
                val hasOffline50 = upgrades.offlineEfficiencyLevel >= 2
                Pair(hasOffline50, "Requires 50% Offline Rate")
            }
            
            else -> Pair(true, "") // No prerequisites
        }
    }
    
    suspend fun addGold(amount: Long, source: GoldSource) {
        upgradesDao.addGold(amount)
    }
    
    suspend fun sellItem(itemId: String, quantity: Int): Long {
        val goldValue = GoldSources.getItemSellValue(itemId, quantity)
        if (goldValue > 0) {
            addGold(goldValue, GoldSource.ITEM_SALE)
            
            // Remove items from bank
            val bankItem = bankDao.findBankItemById(itemId)
            if (bankItem != null) {
                val newQuantity = bankItem.quantity - quantity
                if (newQuantity <= 0) {
                    bankDao.deleteBankItem(bankItem)
                } else {
                    bankDao.updateBankItem(bankItem.copy(quantity = newQuantity))
                }
            }
        }
        return goldValue
    }
    
    suspend fun getAvailableUpgrades(): List<UpgradeInfo> {
        val playerUpgrades = upgradesDao.getPlayerUpgradesSync() ?: PlayerUpgrades()
        val currentGold = upgradesDao.getCurrentGold() ?: 0L
        
        return QoLUpgrades.ALL.map { upgrade ->
            val cost = QoLUpgrades.calculateUpgradeCost(upgrade, upgrade.currentLevel)
            val canAfford = currentGold >= cost
            val (meetsPrereq, prereqText) = checkPrerequisites(upgrade.id, playerUpgrades)
            val isPurchased = isUpgradePurchased(upgrade.id, playerUpgrades)
            
            UpgradeInfo(
                upgrade = upgrade,
                cost = cost,
                canAfford = canAfford,
                meetsPrerequisites = meetsPrereq,
                prerequisiteText = prereqText,
                isPurchased = isPurchased
            )
        }
    }
    
    private fun isUpgradePurchased(upgradeId: String, upgrades: PlayerUpgrades): Boolean {
        return when (upgradeId) {
            "bank_tab_6" -> upgrades.bankTabsPurchased >= 1
            "bank_tab_7" -> upgrades.bankTabsPurchased >= 2
            "bank_tab_8" -> upgrades.bankTabsPurchased >= 3
            "bank_tab_9" -> upgrades.bankTabsPurchased >= 4
            "bank_tab_10" -> upgrades.bankTabsPurchased >= 5
            
            "bank_slots_20" -> upgrades.bankSlotsPerTab >= 20
            "bank_slots_25" -> upgrades.bankSlotsPerTab >= 25
            "bank_slots_30" -> upgrades.bankSlotsPerTab >= 30
            "bank_slots_40" -> upgrades.bankSlotsPerTab >= 40
            "bank_slots_50" -> upgrades.bankSlotsPerTab >= 50
            
            "queue_2" -> upgrades.activityQueueLength >= 2
            "queue_3" -> upgrades.activityQueueLength >= 3
            "queue_5" -> upgrades.activityQueueLength >= 5
            "queue_10" -> upgrades.activityQueueLength >= 10
            
            "offline_40" -> upgrades.offlineEfficiencyLevel >= 1
            "offline_50" -> upgrades.offlineEfficiencyLevel >= 2
            "offline_60" -> upgrades.offlineEfficiencyLevel >= 3
            
            "auto_sell" -> upgrades.autoSellEnabled
            "sorting_tools" -> upgrades.sortingToolsUnlocked
            
            "dark_theme" -> upgrades.themeUnlocked == "dark"
            "compact_theme" -> upgrades.themeUnlocked == "compact"
            "animated_bg" -> upgrades.themeUnlocked == "animated"
            
            else -> false
        }
    }
}

sealed class UpgradeResult {
    data class Success(val upgradeId: String, val costPaid: Long) : UpgradeResult()
    data class InsufficientFunds(val required: Long, val current: Long) : UpgradeResult()
    data class PrerequisiteNotMet(val requirement: String) : UpgradeResult()
    data class Error(val message: String) : UpgradeResult()
    object NotFound : UpgradeResult()
}

data class UpgradeInfo(
    val upgrade: QoLUpgrade,
    val cost: Long,
    val canAfford: Boolean,
    val meetsPrerequisites: Boolean,
    val prerequisiteText: String,
    val isPurchased: Boolean
)

enum class GoldSource {
    ITEM_SALE,
    COMBAT_VICTORY,
    ACTIVITY_BONUS,
    ACHIEVEMENT
}