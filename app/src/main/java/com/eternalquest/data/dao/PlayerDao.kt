package com.eternalquest.data.dao

import androidx.room.*
import com.eternalquest.data.entities.Player
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE id = 1")
    fun getPlayer(): Flow<Player?>
    
    @Query("SELECT * FROM players WHERE id = 1")
    suspend fun getPlayerSync(): Player?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player)
    
    @Update
    suspend fun updatePlayer(player: Player)
    
    @Query("UPDATE players SET lastActiveAt = :timestamp WHERE id = 1")
    suspend fun updateLastActive(timestamp: Long)
    
    @Query("UPDATE players SET totalPlayTime = totalPlayTime + :additionalTime WHERE id = 1")
    suspend fun addPlayTime(additionalTime: Long)
    
    @Query("UPDATE players SET currentSkill = :skill, currentActivity = :activity, activityStartTime = :startTime, activityProgress = :progress WHERE id = 1")
    suspend fun updateActivity(skill: String?, activity: String?, startTime: Long?, progress: Float)
}