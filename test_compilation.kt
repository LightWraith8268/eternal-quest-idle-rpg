// Simple test to check core logic compilation
import kotlin.math.pow

fun main() {
    // Test XP system logic
    fun calculateLevel(experience: Long): Int {
        if (experience <= 0) return 1
        
        for (level in 1..100) {
            val requiredXp = getXpForLevel(level)
            if (experience < requiredXp) {
                return level - 1
            }
        }
        return 100
    }
    
    fun getXpForLevel(level: Int): Long {
        if (level <= 1) return 0L
        return (0 until level).sumOf { lvl ->
            if (lvl == 0) 0L else (lvl.toDouble().pow(2) * 100 + lvl * 50).toLong()
        }
    }
    
    // Test some XP calculations
    println("Level 1 requires: ${getXpForLevel(1)} XP")
    println("Level 2 requires: ${getXpForLevel(2)} XP") 
    println("Level 10 requires: ${getXpForLevel(10)} XP")
    
    println("With 0 XP: Level ${calculateLevel(0)}")
    println("With 150 XP: Level ${calculateLevel(150)}")
    println("With 1000 XP: Level ${calculateLevel(1000)}")
}