package com.eternalquest.data.dao

import androidx.room.*
import com.eternalquest.data.entities.PlayerUpgrades
import com.eternalquest.data.entities.GoldBalance
import kotlinx.coroutines.flow.Flow

@Dao
interface UpgradesDao {
    // Player Upgrades
    @Query("SELECT * FROM player_upgrades WHERE playerId = 1")
    fun getPlayerUpgrades(): Flow<PlayerUpgrades?>
    
    @Query("SELECT * FROM player_upgrades WHERE playerId = 1")
    suspend fun getPlayerUpgradesSync(): PlayerUpgrades?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerUpgrades(upgrades: PlayerUpgrades)
    
    @Update
    suspend fun updatePlayerUpgrades(upgrades: PlayerUpgrades)
    
    // Bank expansion operations
    @Query("UPDATE player_upgrades SET bankTabsPurchased = bankTabsPurchased + 1 WHERE playerId = 1")
    suspend fun purchaseBankTab()
    
    @Query("UPDATE player_upgrades SET bankSlotsPerTab = :slots WHERE playerId = 1")
    suspend fun upgradeBankSlots(slots: Int)
    
    // Queue operations
    @Query("UPDATE player_upgrades SET activityQueueLength = :length WHERE playerId = 1")
    suspend fun upgradeQueueLength(length: Int)
    
    // Offline operations
    @Query("UPDATE player_upgrades SET offlineCapHours = :hours WHERE playerId = 1")
    suspend fun upgradeOfflineCap(hours: Int)
    
    @Query("UPDATE player_upgrades SET offlineEfficiencyLevel = :level WHERE playerId = 1")
    suspend fun upgradeOfflineEfficiency(level: Int)
    
    // Convenience features
    @Query("UPDATE player_upgrades SET autoSellEnabled = :enabled WHERE playerId = 1")
    suspend fun setAutoSell(enabled: Boolean)
    
    @Query("UPDATE player_upgrades SET sortingToolsUnlocked = :unlocked WHERE playerId = 1")
    suspend fun unlockSortingTools(unlocked: Boolean)
    
    @Query("UPDATE player_upgrades SET themeUnlocked = :theme WHERE playerId = 1")
    suspend fun unlockTheme(theme: String)
    
    @Query("UPDATE player_upgrades SET totalGoldSpent = totalGoldSpent + :amount WHERE playerId = 1")
    suspend fun addGoldSpent(amount: Long)
    
    // Gold Balance operations
    @Query("SELECT * FROM gold_balance WHERE playerId = 1")
    fun getGoldBalance(): Flow<GoldBalance?>
    
    @Query("SELECT * FROM gold_balance WHERE playerId = 1")
    suspend fun getGoldBalanceSync(): GoldBalance?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoldBalance(balance: GoldBalance)
    
    @Update
    suspend fun updateGoldBalance(balance: GoldBalance)
    
    @Query("UPDATE gold_balance SET currentGold = currentGold + :amount, totalEarned = totalEarned + :amount WHERE playerId = 1")
    suspend fun addGold(amount: Long)
    
    @Query("UPDATE gold_balance SET currentGold = currentGold - :amount, totalSpent = totalSpent + :amount WHERE playerId = 1")
    suspend fun spendGold(amount: Long)
    
    @Query("SELECT currentGold FROM gold_balance WHERE playerId = 1")
    suspend fun getCurrentGold(): Long?
    
    @Query("UPDATE gold_balance SET currentGold = :amount WHERE playerId = 1")
    suspend fun setCurrentGold(amount: Long)
    
    // Transaction for purchasing upgrades
    @Transaction
    suspend fun purchaseUpgrade(goldCost: Long, upgradeAction: suspend () -> Unit) {
        val currentGold = getCurrentGold() ?: 0L
        if (currentGold >= goldCost) {
            spendGold(goldCost)
            addGoldSpent(goldCost)
            upgradeAction()
        }
    }
}