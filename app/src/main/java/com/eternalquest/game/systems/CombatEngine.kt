package com.eternalquest.game.systems

import com.eternalquest.data.dao.*
import com.eternalquest.data.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged

class CombatEngine(
    private val combatDao: CombatDao,
    private val bankDao: BankDao,
    private val combatSystem: CombatSystem = CombatSystem(),
    private val onGoldEarned: suspend (Long, GoldSource) -> Unit = { _, _ -> },
    private val autoEatPriorityProvider: suspend () -> List<String> = { listOf("cooked_swordfish", "cooked_tuna", "cooked_salmon", "cooked_trout") },
    private val slotsPerTabProvider: suspend () -> Int = { 15 },
    private val totalTabsProvider: suspend () -> Int = { 5 }
) {
    
    suspend fun processCombatTick(currentTime: Long): CombatTickResult {
        val combatStats = combatDao.getCombatStatsSync() ?: return CombatTickResult.NotInCombat
        
        return if (combatStats.isInCombat && combatStats.currentEnemyId != null) {
            processCombatLogic(combatStats, currentTime)
        } else {
            CombatTickResult.NotInCombat
        }
    }
    
    private suspend fun processCombatLogic(
        playerStats: CombatStats,
        currentTime: Long
    ): CombatTickResult {
        val currentEnemy = combatDao.getCurrentEnemy() ?: return CombatTickResult.NotInCombat
        val enemy = Enemies.ALL.find { it.id == currentEnemy.enemyId } ?: return CombatTickResult.NotInCombat
        
        val weapon = playerStats.equippedWeapon?.let { weaponId ->
            Weapons.ALL.find { it.id == weaponId }
        }
        
        val armor = playerStats.equippedArmor?.let { armorId ->
            Armors.ALL.find { it.id == armorId }
        }
        
        val results = mutableListOf<CombatEvent>()
        
        // Check for auto-eat
        if (playerStats.autoEatEnabled && 
            combatSystem.shouldAutoEat(playerStats.hitpoints, playerStats.maxHitpoints, playerStats.autoEatThreshold)) {
            
            playerStats.autoEatFoodId?.let { foodId ->
                val autoEatResult = performAutoEat(playerStats, foodId)
                if (autoEatResult != null) {
                    results.add(autoEatResult)
                }
            }
        }
        
        // Process player attack
        val playerAttackSpeed = combatSystem.calculateAttackSpeed(4000L, weapon)
        if (combatSystem.canPerformAttack(playerStats.lastPlayerAttack, playerAttackSpeed, currentTime)) {
            val attackResult = combatSystem.processPlayerAttack(
                playerStats, weapon, enemy, currentEnemy.currentHp
            )
            
            results.add(
                CombatEvent.Attack(
                    attacker = CombatantType.PLAYER,
                    damage = attackResult.damage,
                    hit = attackResult.hit,
                    critical = attackResult.critical,
                    targetRemainingHp = attackResult.newTargetHp
                )
            )
            
            // Update enemy HP and player attack time
            combatDao.updateEnemyHp(attackResult.newTargetHp)
            combatDao.updateLastPlayerAttack(currentTime)
            
            // Check if enemy is defeated
            if (attackResult.targetDefeated) {
                return processCombatVictory(enemy, playerStats)
            }
        }
        
        // Process enemy attack
        if (combatSystem.canPerformAttack(playerStats.lastEnemyAttack, enemy.attackSpeed, currentTime)) {
            val attackResult = combatSystem.processEnemyAttack(enemy, playerStats, armor)
            
            results.add(
                CombatEvent.Attack(
                    attacker = CombatantType.ENEMY,
                    damage = attackResult.damage,
                    hit = attackResult.hit,
                    critical = attackResult.critical,
                    targetRemainingHp = attackResult.newTargetHp
                )
            )
            
            // Update player HP and enemy attack time
            combatDao.updateHitpoints(attackResult.newTargetHp)
            combatDao.updateLastEnemyAttack(currentTime)
            
            // Check if player is defeated
            if (attackResult.targetDefeated) {
                return processCombatDefeat(playerStats)
            }
        }
        
        return if (results.isNotEmpty()) {
            CombatTickResult.CombatEvents(results)
        } else {
            CombatTickResult.InProgress
        }
    }
    
    private suspend fun performAutoEat(playerStats: CombatStats, foodId: String): CombatEvent.AutoEat? {
        val chosenFoodId = if (playerStats.useBestAutoEat) {
            val priority = autoEatPriorityProvider()
            priority.firstOrNull { id ->
                val item = bankDao.findBankItemById(id)
                item != null && item.quantity > 0
            } ?: foodId
        } else foodId

        val bankItem = bankDao.findBankItemById(chosenFoodId)
        if (bankItem != null && bankItem.quantity > 0) {
            val newQuantity = bankItem.quantity - 1
            if (newQuantity <= 0) {
                bankDao.deleteBankItem(bankItem)
            } else {
                bankDao.updateBankItem(bankItem.copy(quantity = newQuantity))
            }

            val newHp = minOf(
                playerStats.maxHitpoints,
                playerStats.hitpoints + CombatSystem.HEALING_AMOUNT
            )
            combatDao.updateHitpoints(newHp)

            return CombatEvent.AutoEat(chosenFoodId, CombatSystem.HEALING_AMOUNT, newHp)
        }
        return null
    }
    
    private suspend fun processCombatVictory(enemy: Enemy, playerStats: CombatStats): CombatTickResult {
        // Calculate loot drops
        val drops = combatSystem.calculateLootDrops(enemy)
        
        // Add items to bank
        for (drop in drops) {
            addItemToBank(drop.itemId, drop.quantity)
        }
        
        // Add combat XP
        combatDao.addCombatXp(enemy.experienceReward)
        
        // Check for level up
        val newCombatXp = (combatDao.getCombatStatsSync()?.combatXp ?: 0L) + enemy.experienceReward
        val newLevel = combatSystem.calculateCombatLevel(newCombatXp)
        val currentLevel = combatSystem.calculateCombatLevel(newCombatXp - enemy.experienceReward)
        
        // Calculate and award gold
        val goldReward = GoldSources.getCombatGoldReward(enemy.combatLevelRequired)
        onGoldEarned(goldReward, GoldSource.COMBAT_VICTORY)
        
        // End combat
        combatDao.updateCombatState(false, null, null)
        combatDao.clearCurrentEnemy()
        
        return CombatTickResult.Victory(
            enemy = enemy,
            drops = drops,
            xpGained = enemy.experienceReward,
            leveledUp = newLevel > currentLevel,
            newLevel = newLevel,
            goldEarned = goldReward
        )
    }
    
    private suspend fun processCombatDefeat(playerStats: CombatStats): CombatTickResult {
        // End combat and reset to safe state
        combatDao.updateCombatState(false, null, null)
        combatDao.clearCurrentEnemy()
        combatDao.fullHeal() // Heal player after death
        
        return CombatTickResult.Defeat
    }
    
    suspend fun startCombat(enemyId: String, playerStats: CombatStats, currentTime: Long): Boolean {
        val enemy = Enemies.ALL.find { it.id == enemyId } ?: return false
        
        // Check combat level requirement
        val playerCombatLevel = combatSystem.calculateCombatLevel(playerStats.combatXp)
        if (playerCombatLevel < enemy.combatLevelRequired) {
            return false
        }
        
        // Create current enemy instance
        val currentEnemy = CurrentEnemy(
            enemyId = enemyId,
            currentHp = enemy.maxHitpoints,
            maxHp = enemy.maxHitpoints,
            nextAttackTime = currentTime + enemy.attackSpeed,
            combatStartTime = currentTime
        )
        
        // Update combat state
        combatDao.insertCurrentEnemy(currentEnemy)
        combatDao.updateCombatState(true, enemyId, currentTime)
        
        return true
    }
    
    suspend fun endCombat() {
        combatDao.updateCombatState(false, null, null)
        combatDao.clearCurrentEnemy()
    }
    
    suspend fun equipWeapon(weaponId: String?): Boolean {
        weaponId?.let { id ->
            val weapon = Weapons.ALL.find { it.id == id } ?: return false
            val bankItem = bankDao.findBankItemById(id)
            if (bankItem == null || bankItem.quantity <= 0) return false
        }
        
        combatDao.equipWeapon(weaponId)
        return true
    }
    
    suspend fun equipArmor(armorId: String?): Boolean {
        armorId?.let { id ->
            val armor = Armors.ALL.find { it.id == id } ?: return false
            val bankItem = bankDao.findBankItemById(id)
            if (bankItem == null || bankItem.quantity <= 0) return false
        }
        
        combatDao.equipArmor(armorId)
        return true
    }
    
    suspend fun setAutoEat(enabled: Boolean, foodId: String?) {
        combatDao.updateAutoEat(enabled, foodId)
    }
    
    private suspend fun addItemToBank(itemId: String, quantity: Int) {
        val existingItem = bankDao.findBankItemById(itemId)
        if (existingItem != null) {
            bankDao.addToStack(existingItem.tabIndex, existingItem.slotIndex, quantity)
            return
        }

        val totalTabs = totalTabsProvider().coerceAtLeast(1)
        val slotsPerTab = slotsPerTabProvider().coerceAtLeast(1)

        for (tabIndex in 0 until totalTabs) {
            val usedSlots = bankDao.getUsedSlotsInTab(tabIndex)
            if (usedSlots < slotsPerTab) {
                val nextSlot = bankDao.getNextAvailableSlot(tabIndex) ?: 0
                bankDao.insertBankItem(
                    BankItem(
                        itemId = itemId,
                        tabIndex = tabIndex,
                        slotIndex = nextSlot,
                        quantity = quantity
                    )
                )
                return
            }
        }
        }
}

sealed class CombatTickResult {
    object NotInCombat : CombatTickResult()
    object InProgress : CombatTickResult()
    data class CombatEvents(val events: List<CombatEvent>) : CombatTickResult()
    data class Victory(
        val enemy: Enemy,
        val drops: List<ItemDrop>,
        val xpGained: Long,
        val leveledUp: Boolean,
        val newLevel: Int,
        val goldEarned: Long
    ) : CombatTickResult()
    object Defeat : CombatTickResult()
}

sealed class CombatEvent {
    data class Attack(
        val attacker: CombatantType,
        val damage: Int,
        val hit: Boolean,
        val critical: Boolean,
        val targetRemainingHp: Int
    ) : CombatEvent()
    
    data class AutoEat(
        val foodId: String,
        val hpRestored: Int,
        val newHp: Int
    ) : CombatEvent()
}
