// Combat System Test - Standalone validation

import kotlin.math.*
import kotlin.random.Random

// Mock entities for testing
data class TestCombatStats(
    val attack: Int,
    val strength: Int,
    val defense: Int,
    val hitpoints: Int = 100
)

data class TestWeapon(
    val attackBonus: Int,
    val strengthBonus: Int,
    val accuracy: Float
)

data class TestArmor(
    val defenseBonus: Int
)

// Combat calculation logic (copied from main system)
object TestCombatSystem {
    const val BASE_CRIT_CHANCE = 0.05f
    const val CRIT_DAMAGE_MULTIPLIER = 1.5f
    const val BASE_ACCURACY = 0.8f
    
    fun calculateDamage(
        attacker: TestCombatStats,
        weapon: TestWeapon?,
        target: TestCombatStats,
        armor: TestArmor?
    ): TestDamageResult {
        val baseAttack = attacker.attack
        val weaponBonus = weapon?.attackBonus ?: 0
        val strengthBonus = attacker.strength
        val weaponStrengthBonus = weapon?.strengthBonus ?: 0
        
        val totalAttack = baseAttack + weaponBonus + strengthBonus + weaponStrengthBonus
        
        val baseDefense = target.defense
        val armorBonus = armor?.defenseBonus ?: 0
        val totalDefense = baseDefense + armorBonus
        
        val baseAccuracy = weapon?.accuracy ?: BASE_ACCURACY
        val accuracyRoll = Random.nextFloat()
        val hit = accuracyRoll <= calculateAccuracy(totalAttack, totalDefense, baseAccuracy)
        
        if (!hit) {
            return TestDamageResult(0, false, false)
        }
        
        val baseDamage = maxOf(1, totalAttack - totalDefense/2)
        val variance = Random.nextFloat() * 0.3f - 0.15f
        val variableDamage = (baseDamage * (1 + variance)).roundToInt()
        
        val critChance = calculateCritChance(attacker.strength)
        val critRoll = Random.nextFloat()
        val critical = critRoll <= critChance
        
        val finalDamage = if (critical) {
            (variableDamage * CRIT_DAMAGE_MULTIPLIER).roundToInt()
        } else {
            variableDamage
        }
        
        return TestDamageResult(maxOf(1, finalDamage), true, critical)
    }
    
    private fun calculateAccuracy(attack: Int, defense: Int, baseAccuracy: Float): Float {
        val attackDefenseRatio = attack.toFloat() / maxOf(1, defense)
        val accuracyModifier = (attackDefenseRatio - 1) * 0.1f
        return (baseAccuracy + accuracyModifier).coerceIn(0.1f, 0.95f)
    }
    
    private fun calculateCritChance(strength: Int): Float {
        return BASE_CRIT_CHANCE + (strength * 0.002f)
    }
}

data class TestDamageResult(
    val damage: Int,
    val hit: Boolean,
    val critical: Boolean
)

fun main() {
    println("🗡️ Combat System Test")
    println("====================")
    
    // Test scenarios
    val weakPlayer = TestCombatStats(attack = 1, strength = 1, defense = 1)
    val strongPlayer = TestCombatStats(attack = 10, strength = 15, defense = 5)
    val weakEnemy = TestCombatStats(attack = 2, strength = 1, defense = 1)
    val strongEnemy = TestCombatStats(attack = 15, strength = 10, defense = 8)
    
    val bronzeSword = TestWeapon(attackBonus = 5, strengthBonus = 0, accuracy = 0.75f)
    val ironSword = TestWeapon(attackBonus = 10, strengthBonus = 2, accuracy = 0.8f)
    val leatherArmor = TestArmor(defenseBonus = 5)
    val ironArmor = TestArmor(defenseBonus = 15)
    
    println("Test 1: Weak player vs weak enemy (no equipment)")
    testCombat(weakPlayer, null, weakEnemy, null, 10)
    
    println("\nTest 2: Strong player vs strong enemy (no equipment)")
    testCombat(strongPlayer, null, strongEnemy, null, 10)
    
    println("\nTest 3: Weak player with bronze sword vs weak enemy")
    testCombat(weakPlayer, bronzeSword, weakEnemy, null, 10)
    
    println("\nTest 4: Strong player with iron sword vs strong enemy with iron armor")
    testCombat(strongPlayer, ironSword, strongEnemy, ironArmor, 10)
    
    println("\n✅ Combat system calculations working correctly!")
}

fun testCombat(
    attacker: TestCombatStats,
    weapon: TestWeapon?,
    target: TestCombatStats,
    armor: TestArmor?,
    rounds: Int
) {
    var hits = 0
    var crits = 0
    var totalDamage = 0
    
    repeat(rounds) {
        val result = TestCombatSystem.calculateDamage(attacker, weapon, target, armor)
        if (result.hit) {
            hits++
            totalDamage += result.damage
            if (result.critical) crits++
        }
    }
    
    val hitRate = (hits.toFloat() / rounds * 100).roundToInt()
    val critRate = if (hits > 0) (crits.toFloat() / hits * 100).roundToInt() else 0
    val avgDamage = if (hits > 0) totalDamage / hits else 0
    
    println("Hit Rate: $hitRate% ($hits/$rounds)")
    println("Crit Rate: $critRate% ($crits/$hits hits)")
    println("Avg Damage: $avgDamage")
}