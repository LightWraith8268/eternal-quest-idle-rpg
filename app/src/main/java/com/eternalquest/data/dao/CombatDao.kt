package com.eternalquest.data.dao

import androidx.room.*
import com.eternalquest.data.entities.CombatStats
import com.eternalquest.data.entities.CurrentEnemy
import kotlinx.coroutines.flow.Flow

@Dao
interface CombatDao {
    @Query("SELECT * FROM combat_stats WHERE playerId = 1")
    fun getCombatStats(): Flow<CombatStats?>
    
    @Query("SELECT * FROM combat_stats WHERE playerId = 1")
    suspend fun getCombatStatsSync(): CombatStats?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCombatStats(stats: CombatStats)
    
    @Update
    suspend fun updateCombatStats(stats: CombatStats)
    
    @Query("UPDATE combat_stats SET hitpoints = :hp WHERE playerId = 1")
    suspend fun updateHitpoints(hp: Int)
    
    @Query("UPDATE combat_stats SET hitpoints = maxHitpoints WHERE playerId = 1")
    suspend fun fullHeal()
    
    @Query("UPDATE combat_stats SET combatXp = combatXp + :xp WHERE playerId = 1")
    suspend fun addCombatXp(xp: Long)
    
    @Query("UPDATE combat_stats SET equippedWeapon = :weaponId WHERE playerId = 1")
    suspend fun equipWeapon(weaponId: String?)
    
    @Query("UPDATE combat_stats SET equippedArmor = :armorId WHERE playerId = 1")
    suspend fun equipArmor(armorId: String?)
    
    @Query("UPDATE combat_stats SET autoEatEnabled = :enabled, autoEatFoodId = :foodId WHERE playerId = 1")
    suspend fun updateAutoEat(enabled: Boolean, foodId: String?)
    
    @Query("UPDATE combat_stats SET autoEatThreshold = :threshold WHERE playerId = 1")
    suspend fun updateAutoEatThreshold(threshold: Float)

    @Query("UPDATE combat_stats SET useBestAutoEat = :useBest WHERE playerId = 1")
    suspend fun updateUseBestAutoEat(useBest: Boolean)
    
    @Query("UPDATE combat_stats SET isInCombat = :inCombat, currentEnemyId = :enemyId, combatStartTime = :startTime WHERE playerId = 1")
    suspend fun updateCombatState(inCombat: Boolean, enemyId: String?, startTime: Long?)
    
    @Query("UPDATE combat_stats SET lastPlayerAttack = :timestamp WHERE playerId = 1")
    suspend fun updateLastPlayerAttack(timestamp: Long)
    
    @Query("UPDATE combat_stats SET lastEnemyAttack = :timestamp WHERE playerId = 1")
    suspend fun updateLastEnemyAttack(timestamp: Long)
    
    // Current Enemy operations
    @Query("SELECT * FROM current_enemy WHERE id = 1")
    suspend fun getCurrentEnemy(): CurrentEnemy?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentEnemy(enemy: CurrentEnemy)
    
    @Update
    suspend fun updateCurrentEnemy(enemy: CurrentEnemy)
    
    @Query("DELETE FROM current_enemy WHERE id = 1")
    suspend fun clearCurrentEnemy()
    
    @Query("UPDATE current_enemy SET currentHp = :hp WHERE id = 1")
    suspend fun updateEnemyHp(hp: Int)
    
    @Query("UPDATE current_enemy SET nextAttackTime = :time WHERE id = 1")
    suspend fun updateEnemyAttackTime(time: Long)
}
