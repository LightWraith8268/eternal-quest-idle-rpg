package com.eternalquest.game.systems

import com.eternalquest.data.dao.PlayerDao
import com.eternalquest.data.dao.SkillDao
import com.eternalquest.data.dao.BankDao
import com.eternalquest.data.entities.ActivityDefinition
import com.eternalquest.data.entities.Activities
import com.eternalquest.data.entities.BankItem
import kotlinx.coroutines.flow.Flow
import com.eternalquest.data.entities.GoldSources
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

class TickEngine(
    private val playerDao: PlayerDao,
    private val skillDao: SkillDao,
    private val bankDao: BankDao,
    private val timeService: TimeService,
    private val onGoldEarned: suspend (Long, GoldSource) -> Unit = { _, _ -> },
    private val slotsPerTabProvider: suspend () -> Int = { 15 },
    private val metaXpMultiplierProvider: suspend () -> Double = { 1.0 },
    private val speedFactorProvider: suspend () -> Double = { 1.0 },
    private val lootChanceBonusProvider: suspend () -> Double = { 1.0 },
    private val tryAutoSell: suspend (String, Int) -> Boolean = { _, _ -> false }
) {
    fun processGameTick(currentTime: Long): Flow<TickResult> {
        return combine(
            playerDao.getPlayer(),
            skillDao.getAllSkills()
        ) { player, skills ->
            if (player == null || player.currentActivity == null || player.activityStartTime == null) {
                return@combine TickResult.Idle
            }
            
            val activity = Activities.ALL.find { it.id == player.currentActivity }
                ?: return@combine TickResult.Idle
            
            val skill = skills.find { it.name == player.currentSkill }
                ?: return@combine TickResult.Idle
            
            val elapsedTime = currentTime - player.activityStartTime
            val speedFactor = speedFactorProvider()
            val effectiveTime = (activity.baseTimeMs * speedFactor).toLong().coerceAtLeast(1L)
            val progress = (elapsedTime.toFloat() / effectiveTime.toFloat()).coerceIn(0f, 1f)
            
            if (progress >= 1.0f) {
                // Activity completed
                TickResult.ActivityCompleted(activity, skill.level, skill.prestigeCount)
            } else {
                // Activity in progress
                TickResult.ActivityProgress(activity, progress)
            }
        }.distinctUntilChanged()
    }
    
    suspend fun completeActivity(activity: ActivityDefinition, skillLevel: Int, prestigeCount: Int) {
        // Calculate XP gain
        val baseXp = activity.xpReward
        val metaMultiplier = metaXpMultiplierProvider()
        val xpGain = (XpSystem.calculateXpGain(baseXp, skillLevel, prestigeCount) * metaMultiplier).toLong()
        
        // Add XP to skill
        skillDao.addExperience(activity.skill, xpGain)
        
        // Check for level up
        val updatedSkill = skillDao.getSkillSync(activity.skill)
        if (updatedSkill != null) {
            val newLevel = XpSystem.calculateLevel(updatedSkill.experience)
            if (newLevel > updatedSkill.level && newLevel <= XpSystem.MAX_LEVEL) {
                skillDao.updateLevelAndXp(activity.skill, newLevel, updatedSkill.experience)
            }
        }
        
        // Add item rewards to bank
        val chanceBonus = lootChanceBonusProvider()
        for (reward in activity.itemRewards) {
            val finalChance = (reward.chance.toDouble() * chanceBonus).coerceAtMost(1.0)
            if (Math.random() <= finalChance) {
                addItemToBank(reward.itemId, reward.quantity)
            }
        }
        
        // Check for gold reward (5% chance)
        val goldReward = GoldSources.getActivityGoldReward(skillLevel)
        if (goldReward > 0) {
            onGoldEarned(goldReward, GoldSource.ACTIVITY_BONUS)
        }
        
        // Reset player activity
        playerDao.updateActivity(null, null, null, 0f)
    }
    
    private suspend fun addItemToBank(itemId: String, quantity: Int) {
        // Find existing stack or empty slot
        val existingItem = bankDao.findBankItemById(itemId)
        
        if (existingItem != null) {
            // Add to existing stack
            bankDao.addToStack(existingItem.tabIndex, existingItem.slotIndex, quantity)
        } else {
            // Find first available slot in tab 0
            val usedSlots = bankDao.getUsedSlotsInTab(0)
            val slotsPerTab = slotsPerTabProvider()
            if (usedSlots < slotsPerTab) {
                val nextSlot = bankDao.getNextAvailableSlot(0) ?: 0
                bankDao.insertBankItem(
                    BankItem(
                        itemId = itemId,
                        tabIndex = 0,
                        slotIndex = nextSlot,
                        quantity = quantity
                    )
                )
            } else {
                // Bank full - attempt auto-sell if enabled
                val sold = tryAutoSell(itemId, quantity)
                if (!sold) {
                    // item lost if not sold
                }
            }
        }
    }
    
    suspend fun processOfflineProgress(offlineProgress: OfflineProgress) {
        val player = playerDao.getPlayerSync() ?: return
        
        if (player.currentActivity != null && player.activityStartTime != null) {
            val activity = Activities.ALL.find { it.id == player.currentActivity } ?: return
            val skill = skillDao.getSkillSync(player.currentSkill ?: "") ?: return
            
            // Calculate how many completions occurred during offline time
            val speedFactor = speedFactorProvider()
            val timePerCompletion = (activity.baseTimeMs * speedFactor).toLong().coerceAtLeast(1L)
            val offlineCompletions = (offlineProgress.effectiveProgressMs / timePerCompletion).toInt()
            
            // Process each completion
            repeat(offlineCompletions) {
                completeActivity(activity, skill.level, skill.prestigeCount)
            }
            
            // Calculate remaining progress
            val remainingTime = offlineProgress.effectiveProgressMs % timePerCompletion
            val newProgress = remainingTime.toFloat() / timePerCompletion.toFloat()
            val newStartTime = System.currentTimeMillis() - remainingTime
            
            playerDao.updateActivity(
                player.currentSkill,
                player.currentActivity,
                newStartTime,
                newProgress
            )
        }
    }
}

sealed class TickResult {
    object Idle : TickResult()
    data class ActivityProgress(val activity: ActivityDefinition, val progress: Float) : TickResult()
    data class ActivityCompleted(val activity: ActivityDefinition, val skillLevel: Int, val prestigeCount: Int) : TickResult()
}
