package com.eternalquest.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skills")
data class Skill(
    @PrimaryKey val name: String,
    val level: Int = 1,
    val experience: Long = 0L,
    val prestigeCount: Int = 0,
    val isUnlocked: Boolean = false
)

data class SkillType(
    val name: String,
    val displayName: String,
    val description: String,
    val baseXpRate: Long,
    val category: SkillCategory
)

enum class SkillCategory {
    GATHERING,
    CRAFTING,
    COMBAT
}

object Skills {
    val MINING = SkillType("mining", "Mining", "Extract ores and gems from rocks", 10L, SkillCategory.GATHERING)
    val WOODCUTTING = SkillType("woodcutting", "Woodcutting", "Chop trees for logs and materials", 10L, SkillCategory.GATHERING)
    val FISHING = SkillType("fishing", "Fishing", "Catch fish from various waters", 10L, SkillCategory.GATHERING)
    val COOKING = SkillType("cooking", "Cooking", "Prepare food from raw materials", 15L, SkillCategory.CRAFTING)
    val SMITHING = SkillType("smithing", "Smithing", "Smelt bars and forge gear", 20L, SkillCategory.CRAFTING)
    
    val ALL = listOf(MINING, WOODCUTTING, FISHING, COOKING, SMITHING)
}
