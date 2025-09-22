package com.eternalquest.game.systems

import com.eternalquest.data.entities.*
import kotlin.math.*
import kotlin.random.Random

class CombatSystem {
    
    companion object {
        const val BASE_CRIT_CHANCE = 0.05f // 5% base crit chance
        const val CRIT_DAMAGE_MULTIPLIER = 1.5f
        const val BASE_ACCURACY = 0.8f
        const val HEALING_AMOUNT = 15 // Default HP restored if food is unknown

        private val FOOD_HEALING: Map<String, Int> = mapOf(
            "cooked_trout" to 10,
            "cooked_salmon" to 14,
            "cooked_tuna" to 18,
            "cooked_swordfish" to 24,
            "cooked_shark" to 35,
            "cooked_manta_ray" to 45,
            "cooked_kraken_tentacle" to 60,
            "cooked_leviathan" to 80,
            "cooked_void_fish" to 100
        )

        fun healingAmountForFood(foodId: String): Int {
            val key = foodId.lowercase()
            return FOOD_HEALING[key] ?: HEALING_AMOUNT
        }
    }
    
    fun calculateDamage(
        attacker: CombatStats,
        weapon: Weapon?,
        target: CombatStats,
        armor: Armor?
    ): DamageResult {
        // Base damage calculation
        val baseAttack = attacker.attack
        val weaponBonus = weapon?.attackBonus ?: 0
        val strengthBonus = attacker.strength
        val weaponStrengthBonus = weapon?.strengthBonus ?: 0
        
        val totalAttack = baseAttack + weaponBonus + strengthBonus + weaponStrengthBonus
        
        // Defense calculation
        val baseDefense = target.defense
        val armorBonus = armor?.defenseBonus ?: 0
        val totalDefense = baseDefense + armorBonus
        
        // Accuracy check
        val baseAccuracy = weapon?.accuracy ?: BASE_ACCURACY
        val accuracyRoll = Random.nextFloat()
        val hit = accuracyRoll <= calculateAccuracy(totalAttack, totalDefense, baseAccuracy)
        
        if (!hit) {
            return DamageResult(0, false, false)
        }
        
        // Damage calculation with variance
        val baseDamage = maxOf(1, totalAttack - totalDefense/2)
        val variance = Random.nextFloat() * 0.3f - 0.15f // ±15% variance
        val variableDamage = (baseDamage * (1 + variance)).roundToInt()
        
        // Critical hit check
        val critChance = calculateCritChance(attacker.strength)
        val critRoll = Random.nextFloat()
        val critical = critRoll <= critChance
        
        val finalDamage = if (critical) {
            (variableDamage * CRIT_DAMAGE_MULTIPLIER).roundToInt()
        } else {
            variableDamage
        }
        
        return DamageResult(maxOf(1, finalDamage), true, critical)
    }
    
    private fun calculateAccuracy(attack: Int, defense: Int, baseAccuracy: Float): Float {
        // Higher attack vs defense improves accuracy
        val attackDefenseRatio = attack.toFloat() / maxOf(1, defense)
        val accuracyModifier = (attackDefenseRatio - 1) * 0.1f // 10% per point of advantage
        
        return (baseAccuracy + accuracyModifier).coerceIn(0.1f, 0.95f)
    }
    
    private fun calculateCritChance(strength: Int): Float {
        // Each point of strength adds 0.2% crit chance
        return BASE_CRIT_CHANCE + (strength * 0.002f)
    }
    
    fun calculateCombatLevel(combatXp: Long): Int {
        return XpSystem.calculateLevel(combatXp)
    }
    
    fun getCombatXpForLevel(level: Int): Long {
        return XpSystem.getXpForLevel(level)
    }
    
    fun calculateAttackSpeed(baseSpeed: Long, weapon: Weapon?): Long {
        return weapon?.attackSpeed ?: baseSpeed
    }
    
    fun canPerformAttack(lastAttackTime: Long?, attackSpeed: Long, currentTime: Long): Boolean {
        return lastAttackTime == null || (currentTime - lastAttackTime) >= attackSpeed
    }
    
    fun shouldAutoEat(currentHp: Int, maxHp: Int, threshold: Float = 0.5f): Boolean {
        return currentHp.toFloat() / maxHp.toFloat() <= threshold
    }
    
    fun calculateLootDrops(enemy: Enemy): List<ItemDrop> {
        val lootTable = com.eternalquest.util.LootTablesCatalog.getLootTable(enemy.id) ?: LootTables.getLootTable(enemy.id)
        val drops = mutableListOf<ItemDrop>()
        
        for (loot in lootTable) {
            val roll = Random.nextFloat()
            if (roll <= loot.chance) {
                drops.add(
                    ItemDrop(
                        itemId = loot.itemId,
                        quantity = loot.quantity,
                        isRare = loot.isRare
                    )
                )
            }
        }
        
        return drops
    }
    
    fun processPlayerAttack(
        playerStats: CombatStats,
        weapon: Weapon?,
        enemy: Enemy,
        currentEnemyHp: Int
    ): AttackResult {
        // Create temporary combat stats for enemy (enemies don't have persistent stats)
        val enemyStats = CombatStats(
            playerId = -1,
            hitpoints = currentEnemyHp,
            maxHitpoints = enemy.maxHitpoints,
            attack = enemy.attack,
            strength = enemy.strength,
            defense = enemy.defense
        )
        
        val damageResult = calculateDamage(playerStats, weapon, enemyStats, null)
        val newEnemyHp = maxOf(0, currentEnemyHp - damageResult.damage)
        val enemyDefeated = newEnemyHp <= 0
        
        return AttackResult(
            damage = damageResult.damage,
            hit = damageResult.hit,
            critical = damageResult.critical,
            newTargetHp = newEnemyHp,
            targetDefeated = enemyDefeated
        )
    }
    
    fun processEnemyAttack(
        enemy: Enemy,
        playerStats: CombatStats,
        armor: Armor?
    ): AttackResult {
        // Create temporary combat stats for enemy
        val enemyStats = CombatStats(
            playerId = -1,
            attack = enemy.attack,
            strength = enemy.strength,
            defense = enemy.defense
        )
        
        val damageResult = calculateDamage(enemyStats, null, playerStats, armor)
        val newPlayerHp = maxOf(0, playerStats.hitpoints - damageResult.damage)
        val playerDefeated = newPlayerHp <= 0
        
        return AttackResult(
            damage = damageResult.damage,
            hit = damageResult.hit,
            critical = damageResult.critical,
            newTargetHp = newPlayerHp,
            targetDefeated = playerDefeated
        )
    }
}

data class DamageResult(
    val damage: Int,
    val hit: Boolean,
    val critical: Boolean
)

data class AttackResult(
    val damage: Int,
    val hit: Boolean,
    val critical: Boolean,
    val newTargetHp: Int,
    val targetDefeated: Boolean
)

data class ItemDrop(
    val itemId: String,
    val quantity: Int,
    val isRare: Boolean = false
)

sealed class CombatResult {
    object InProgress : CombatResult()
    data class PlayerVictory(val drops: List<ItemDrop>, val xpGained: Long) : CombatResult()
    object PlayerDefeat : CombatResult()
    data class AutoEat(val foodUsed: String, val hpRestored: Int) : CombatResult()
}
