package com.eternalquest.data.dao

import androidx.room.*
import com.eternalquest.data.entities.Skill
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {
    @Query("SELECT * FROM skills")
    fun getAllSkills(): Flow<List<Skill>>
    
    @Query("SELECT * FROM skills WHERE name = :name")
    fun getSkill(name: String): Flow<Skill?>
    
    @Query("SELECT * FROM skills WHERE name = :name")
    suspend fun getSkillSync(name: String): Skill?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: Skill)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<Skill>)
    
    @Update
    suspend fun updateSkill(skill: Skill)
    
    @Query("UPDATE skills SET experience = experience + :xp WHERE name = :skillName")
    suspend fun addExperience(skillName: String, xp: Long)
    
    @Query("UPDATE skills SET level = :level, experience = :experience WHERE name = :skillName")
    suspend fun updateLevelAndXp(skillName: String, level: Int, experience: Long)
    
    @Query("UPDATE skills SET level = 1, experience = 0, prestigeCount = prestigeCount + 1 WHERE name = :skillName")
    suspend fun prestigeSkill(skillName: String)
    
    @Query("UPDATE skills SET isUnlocked = :unlocked WHERE name = :skillName")
    suspend fun setSkillUnlocked(skillName: String, unlocked: Boolean)

    @Query("UPDATE skills SET level = 1, experience = 0, prestigeCount = 0")
    suspend fun resetAllSkills()
}
