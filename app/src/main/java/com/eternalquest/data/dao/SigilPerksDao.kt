package com.eternalquest.data.dao

import androidx.room.*
import com.eternalquest.data.entities.SigilPerks
import kotlinx.coroutines.flow.Flow

@Dao
interface SigilPerksDao {
    @Query("SELECT * FROM sigil_perks WHERE playerId = 1")
    fun getPerks(): Flow<SigilPerks?>

    @Query("SELECT * FROM sigil_perks WHERE playerId = 1")
    suspend fun getPerksSync(): SigilPerks?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerks(perks: SigilPerks)

    @Update
    suspend fun updatePerks(perks: SigilPerks)
}

