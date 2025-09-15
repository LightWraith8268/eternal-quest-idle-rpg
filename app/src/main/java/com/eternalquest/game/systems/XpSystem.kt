package com.eternalquest.game.systems

import kotlin.math.floor
import kotlin.math.pow

object XpSystem {
    const val MAX_LEVEL = 100
    
    fun calculateLevel(experience: Long): Int {
        if (experience <= 0) return 1
        
        // XP formula: XP = level^2 * 100 + level * 50
        // This creates a smooth curve from level 1 to 100
        for (level in 1..MAX_LEVEL) {
            val requiredXp = getXpForLevel(level)
            if (experience < requiredXp) {
                return level - 1
            }
        }
        return MAX_LEVEL
    }
    
    fun getXpForLevel(level: Int): Long {
        if (level <= 1) return 0L
        // Cumulative XP required to reach this level
        return (0 until level).sumOf { lvl ->
            if (lvl == 0) 0L else (lvl.toDouble().pow(2) * 100 + lvl * 50).toLong()
        }
    }
    
    fun getXpForNextLevel(currentLevel: Int): Long {
        if (currentLevel >= MAX_LEVEL) return Long.MAX_VALUE
        return getXpForLevel(currentLevel + 1)
    }
    
    fun getXpToNextLevel(currentXp: Long): Long {
        val currentLevel = calculateLevel(currentXp)
        if (currentLevel >= MAX_LEVEL) return 0L
        
        val nextLevelXp = getXpForNextLevel(currentLevel)
        return nextLevelXp - currentXp
    }
    
    fun getProgressToNextLevel(currentXp: Long): Float {
        val currentLevel = calculateLevel(currentXp)
        if (currentLevel >= MAX_LEVEL) return 1.0f
        
        val currentLevelXp = getXpForLevel(currentLevel)
        val nextLevelXp = getXpForNextLevel(currentLevel)
        val xpInCurrentLevel = currentXp - currentLevelXp
        val xpNeededForLevel = nextLevelXp - currentLevelXp
        
        return if (xpNeededForLevel > 0) {
            (xpInCurrentLevel.toFloat() / xpNeededForLevel.toFloat()).coerceIn(0f, 1f)
        } else {
            1.0f
        }
    }
    
    fun canLevelUp(currentXp: Long): Boolean {
        val currentLevel = calculateLevel(currentXp)
        return currentLevel < MAX_LEVEL && currentXp >= getXpForNextLevel(currentLevel)
    }
    
    fun calculateXpGain(baseXp: Long, skillLevel: Int, prestigeCount: Int = 0): Long {
        // Base XP with slight level scaling
        val levelMultiplier = 1.0 + (skillLevel - 1) * 0.01 // 1% increase per level
        val prestigeMultiplier = 1.0 + (prestigeCount * 0.05) // 5% increase per prestige
        
        return (baseXp * levelMultiplier * prestigeMultiplier).toLong()
    }
}