package com.eternalquest.repository

import android.content.Context
import com.eternalquest.data.database.EternalQuestDatabase
import com.eternalquest.data.database.DatabaseProvider
import com.eternalquest.data.entities.*
import com.eternalquest.game.systems.*
import kotlinx.coroutines.flow.*
import com.eternalquest.util.ProfileManager

class GameRepository(private val appContext: Context) {
    @Volatile
    private var profileId: Int = ProfileManager.getCurrentProfileId(appContext)
    @Volatile
    private var database = DatabaseProvider.get(appContext, profileId)
    private var playerDao = database.playerDao()
    private var skillDao = database.skillDao()
    private var itemDao = database.itemDao()
    private var bankDao = database.bankDao()
    private var combatDao = database.combatDao()
    private var upgradesDao = database.upgradesDao()
    private var sigilPerksDao = database.sigilPerksDao()
    
    private val timeService = TimeService()
    init {
        com.eternalquest.util.PerkConfig.load(appContext)
        // Hard-fail on invalid or duplicate IDs to keep content consistent
        com.eternalquest.util.ItemCatalog.load(appContext)
        com.eternalquest.util.EnemyCatalog.load(appContext)
        com.eternalquest.util.AreasCatalog.load(appContext)
        com.eternalquest.util.SkillsCatalog.load(appContext)
        com.eternalquest.util.LootTablesCatalog.load(appContext)
    }
    private var upgradeSystem = UpgradeSystem(upgradesDao, bankDao)
    private var tickEngine = TickEngine(
        playerDao,
        skillDao,
        bankDao,
        timeService,
        onGoldEarned = { amount, source ->
            upgradeSystem.addGold(amount, source)
        },
        slotsPerTabProvider = {
            val upgrades = upgradesDao.getPlayerUpgradesSync() ?: PlayerUpgrades()
            QoLUpgrades.getBankSlotsPerTab(upgrades)
        },
        metaXpMultiplierProvider = {
            val perks = sigilPerksDao.getPerksSync() ?: SigilPerks()
            1.0 + (perks.xpBonusLevel * 0.02)
        },
        speedFactorProvider = {
            val perks = sigilPerksDao.getPerksSync() ?: SigilPerks()
            (1.0 - (perks.speedBonusLevel * 0.02)).coerceAtLeast(0.7)
        },
        lootChanceBonusProvider = {
            val perks = sigilPerksDao.getPerksSync() ?: SigilPerks()
            1.0 + (perks.lootBonusLevel * 0.02)
        },
        tryAutoSell = { itemId, quantity ->
            val upgrades = upgradesDao.getPlayerUpgradesSync() ?: PlayerUpgrades()
            if (upgrades.autoSellEnabled) {
                val goldValue = GoldSources.getItemSellValue(itemId, quantity)
                if (goldValue > 0) {
                    upgradeSystem.addGold(goldValue, GoldSource.ITEM_SALE)
                    true
                } else false
            } else false
        }
    )
    private var combatEngine = CombatEngine(
        combatDao,
        bankDao,
        onGoldEarned = { amount, source -> upgradeSystem.addGold(amount, source) },
        autoEatPriorityProvider = { com.eternalquest.util.AutoEatPrefs.getPriority(appContext, profileId) },
        slotsPerTabProvider = {
            val upgrades = upgradesDao.getPlayerUpgradesSync() ?: PlayerUpgrades()
            QoLUpgrades.getBankSlotsPerTab(upgrades)
        },
        totalTabsProvider = {
            val upgrades = upgradesDao.getPlayerUpgradesSync() ?: PlayerUpgrades()
            QoLUpgrades.getTotalBankTabs(upgrades)
        },
        enemyProvider = { id -> com.eternalquest.util.EnemyCatalog.get(id) ?: com.eternalquest.data.entities.Enemies.ALL.find { it.id == id } }
    )
    
    // Trigger for live data source switching
    private val dataSource = MutableStateFlow(0)

    // Player operations
    fun getPlayer(): Flow<Player?> = dataSource.flatMapLatest { playerDao.getPlayer() }
    suspend fun createPlayer(name: String = "Adventurer"): Player {
        val player = Player(name = name)
        playerDao.insertPlayer(player)
        return player
    }
    
    suspend fun updateLastActive() {
        playerDao.updateLastActive(System.currentTimeMillis())
    }
    
    // Skill operations
    fun getAllSkills(): Flow<List<Skill>> = dataSource.flatMapLatest { skillDao.getAllSkills() }
    fun getSkill(name: String): Flow<Skill?> = dataSource.flatMapLatest { skillDao.getSkill(name) }
    
    suspend fun initializeSkills() {
        val existingSkills = skillDao.getAllSkills().first()
        val existingNames = existingSkills.map { it.name }.toSet()
        val missing = Skills.ALL.filter { it.name !in existingNames }
        if (existingSkills.isEmpty()) {
            val initialSkills = Skills.ALL.map { skillType ->
                Skill(
                    name = skillType.name,
                    level = 1,
                    experience = 0L,
                    prestigeCount = 0,
                    isUnlocked = when (skillType.name) {
                        "mining", "woodcutting", "fishing" -> true
                        else -> true // unlock crafting skills by default for Phase 5
                    }
                )
            }
            skillDao.insertSkills(initialSkills)
        } else if (missing.isNotEmpty()) {
            val newSkills = missing.map { skillType ->
                Skill(
                    name = skillType.name,
                    level = 1,
                    experience = 0L,
                    prestigeCount = 0,
                    isUnlocked = true
                )
            }
            skillDao.insertSkills(newSkills)
        }
    }
    
    suspend fun startActivity(skillName: String, activityId: String) {
        // Check if activity exists and player meets requirements
        val activity = Activities.ALL.find { it.id == activityId } ?: return
        val skill = skillDao.getSkillSync(skillName) ?: return
        
        // Check level requirements
        for ((reqSkill, reqLevel) in activity.requirements) {
            val requiredSkill = skillDao.getSkillSync(reqSkill)
            if (requiredSkill == null || requiredSkill.level < reqLevel) {
                return // Requirements not met
            }
        }
        
        // Check and consume item costs for crafting/smithing
        if (activity.itemCosts.isNotEmpty()) {
            // Verify availability
            for (cost in activity.itemCosts) {
                val bankItem = bankDao.findBankItemById(cost.itemId)
                if (bankItem == null || bankItem.quantity < cost.quantity) {
                    return // Not enough materials
                }
            }
            // Consume materials
            for (cost in activity.itemCosts) {
                val bankItem = bankDao.findBankItemById(cost.itemId)
                if (bankItem != null) {
                    val newQty = bankItem.quantity - cost.quantity
                    if (newQty <= 0) {
                        bankDao.deleteBankItem(bankItem)
                    } else {
                        bankDao.updateBankItem(bankItem.copy(quantity = newQty))
                    }
                }
            }
        }
        
        // Start the activity
        val startTime = System.currentTimeMillis()
        playerDao.updateActivity(skillName, activityId, startTime, 0f)
    }
    
    suspend fun stopActivity() {
        playerDao.updateActivity(null, null, null, 0f)
    }
    
    // Bank operations
    fun getAllBankItems(): Flow<List<BankItem>> = dataSource.flatMapLatest { bankDao.getAllBankItems() }
    fun getBankItemsForTab(tabIndex: Int): Flow<List<BankItem>> = dataSource.flatMapLatest { bankDao.getBankItemsForTab(tabIndex) }
    
    suspend fun addItemToBank(itemId: String, quantity: Int, tabIndex: Int = 0): Boolean {
        // Check if item already exists in bank
        val existingItem = bankDao.findBankItemById(itemId)
        
        if (existingItem != null) {
            // Add to existing stack
            bankDao.addToStack(existingItem.tabIndex, existingItem.slotIndex, quantity)
            return true
        } else {
            // Find available slot using upgraded bank capacity
            val playerUpgrades = upgradesDao.getPlayerUpgradesSync() ?: PlayerUpgrades()
            val slotsPerTab = playerUpgrades.bankSlotsPerTab
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
                return true
            }
        }
        return false // Bank full
    }
    
    // Item operations
    suspend fun initializeItems() {
        // Load item catalog from JSON if available (fallback to built-ins)
        try { com.eternalquest.util.ItemCatalog.load(appContext) } catch (_: Exception) {}
        val catalog = com.eternalquest.util.ItemCatalog.all().ifEmpty { GameItems.ALL }
        val existingItems = itemDao.getAllItems().first()
        if (existingItems.isEmpty()) {
            itemDao.insertItems(catalog)
        } else {
            val existingIds = existingItems.map { it.id }.toSet()
            val missing = catalog.filter { it.id !in existingIds }
            if (missing.isNotEmpty()) itemDao.insertItems(missing)
        }
        // Load any extra activities from assets
        try {
            Activities.loadExtrasFromAssets(appContext)
        } catch (_: Exception) {}
    }
    
    suspend fun getItem(itemId: String): Item? = itemDao.getItem(itemId)
    
    // Time and progression
    fun createGameTick() = timeService.createGameTick()
    fun processGameTick(currentTime: Long) = tickEngine.processGameTick(currentTime)
    
    suspend fun handleOfflineProgress() {
        val player = playerDao.getPlayerSync() ?: return
        val playerUpgrades = upgradesDao.getPlayerUpgradesSync() ?: PlayerUpgrades()
        val offlineRate = QoLUpgrades.getOfflineEfficiencyRate(playerUpgrades)
        val offlineProgress = timeService.calculateOfflineProgress(player.lastActiveAt, offlineRate)
        
        if (offlineProgress.totalOfflineMs > 30000) { // More than 30 seconds offline
            tickEngine.processOfflineProgress(offlineProgress)
        }
        
        updateLastActive()
    }
    
    suspend fun completeActivity(activity: ActivityDefinition, skillLevel: Int, prestigeCount: Int) {
        tickEngine.completeActivity(activity, skillLevel, prestigeCount)
    }
    
    // Combat operations
    fun getCombatStats(): Flow<CombatStats?> = dataSource.flatMapLatest { combatDao.getCombatStats() }
    
    suspend fun initializeCombatStats() {
        val existingStats = combatDao.getCombatStatsSync()
        if (existingStats == null) {
            val defaultStats = CombatStats(
                playerId = 1,
                hitpoints = 100,
                maxHitpoints = 100,
                attack = 1,
                strength = 1,
                defense = 1,
                combatXp = 0L,
                autoEatEnabled = true,
                autoEatFoodId = "cooked_trout"
            )
            combatDao.insertCombatStats(defaultStats)
        }
    }
    
    // Upgrade system operations
    suspend fun initializeUpgrades() {
        QoLUpgrades.applyConfig(appContext)
        upgradeSystem.initializeUpgrades()
        if (sigilPerksDao.getPerksSync() == null) {
            sigilPerksDao.insertPerks(SigilPerks())
        }
    }
    
    fun getPlayerUpgrades(): Flow<PlayerUpgrades?> = dataSource.flatMapLatest { upgradesDao.getPlayerUpgrades() }
    fun getGoldBalance(): Flow<GoldBalance?> = dataSource.flatMapLatest { upgradesDao.getGoldBalance() }
    fun getSigilPerks(): Flow<SigilPerks?> = dataSource.flatMapLatest { sigilPerksDao.getPerks() }
    
    suspend fun getAvailableUpgrades(): List<UpgradeInfo> = upgradeSystem.getAvailableUpgrades()
    
    suspend fun purchaseUpgrade(upgradeId: String): UpgradeResult {
        return upgradeSystem.purchaseUpgrade(upgradeId)
    }
    
    suspend fun addGold(amount: Long, source: GoldSource) {
        upgradeSystem.addGold(amount, source)
    }
    
    suspend fun sellItem(itemId: String, quantity: Int): Long {
        return upgradeSystem.sellItem(itemId, quantity)
    }
    
    suspend fun getBankTabs(): Int {
        val playerUpgrades = upgradesDao.getPlayerUpgradesSync() ?: PlayerUpgrades()
        return QoLUpgrades.getTotalBankTabs(playerUpgrades)
    }
    
    suspend fun getBankSlotsPerTab(): Int {
        val playerUpgrades = upgradesDao.getPlayerUpgradesSync() ?: PlayerUpgrades()
        return QoLUpgrades.getBankSlotsPerTab(playerUpgrades)
    }
    
    suspend fun startCombat(enemyId: String): Boolean {
        val combatStats = combatDao.getCombatStatsSync() ?: return false
        return combatEngine.startCombat(enemyId, combatStats, System.currentTimeMillis())
    }
    
    suspend fun endCombat() {
        combatEngine.endCombat()
    }
    
    suspend fun equipWeapon(weaponId: String?): Boolean {
        return combatEngine.equipWeapon(weaponId)
    }
    
    suspend fun equipArmor(armorId: String?): Boolean {
        return combatEngine.equipArmor(armorId)
    }
    
    suspend fun setAutoEat(enabled: Boolean, foodId: String?) {
        combatEngine.setAutoEat(enabled, foodId)
    }
    
    suspend fun setAutoEatThreshold(threshold: Float) {
        database.combatDao().updateAutoEatThreshold(threshold.coerceIn(0.1f, 0.9f))
    }

    suspend fun setUseBestAutoEat(useBest: Boolean) {
        database.combatDao().updateUseBestAutoEat(useBest)
    }
    
    fun getAutoEatPriority(): List<String> = com.eternalquest.util.AutoEatPrefs.getPriority(appContext, profileId)
    fun setAutoEatPriority(priority: List<String>) {
        com.eternalquest.util.AutoEatPrefs.setPriority(appContext, profileId, priority)
    }
    
    suspend fun processCombatTick(currentTime: Long) = combatEngine.processCombatTick(currentTime)
    
    // Utility functions
    fun calculateLevel(experience: Long): Int = XpSystem.calculateLevel(experience)
    fun getXpForNextLevel(currentLevel: Int): Long = XpSystem.getXpForNextLevel(currentLevel)
    fun getProgressToNextLevel(currentXp: Long): Float = XpSystem.getProgressToNextLevel(currentXp)
    fun formatDuration(milliseconds: Long): String = timeService.formatDuration(milliseconds)

    // Prestige operations
    suspend fun prestigeSkill(skillName: String): Boolean {
        val skill = skillDao.getSkillSync(skillName) ?: return false
        if (skill.level >= XpSystem.MAX_LEVEL) {
            skillDao.prestigeSkill(skillName)
            return true
        }
        return false
    }

    // Ascension operations
    suspend fun canAscend(): Boolean {
        val all = skillDao.getAllSkills().first()
        if (all.isEmpty()) return false
        return all.all { it.level >= XpSystem.MAX_LEVEL }
    }

    @androidx.room.Transaction
    suspend fun ascend(): Boolean {
        if (!canAscend()) return false
        val player = playerDao.getPlayerSync() ?: return false

        // Award sigils: 1 per maxed skill
        val skills = skillDao.getAllSkills().first()
        val sigilsAwarded = skills.count { it.level >= XpSystem.MAX_LEVEL }

        // Reset systems
        // Stop any activity
        playerDao.updateActivity(null, null, null, 0f)

        // Reset skills
        skillDao.resetAllSkills()

        // Clear bank
        bankDao.clearAll()

        // Reset gold and upgrades to defaults
        upgradesDao.insertPlayerUpgrades(PlayerUpgrades())
        upgradesDao.insertGoldBalance(GoldBalance())

        // Reset combat state
        val defaultStats = CombatStats(
            playerId = 1,
            hitpoints = 100,
            maxHitpoints = 100,
            attack = 1,
            strength = 1,
            defense = 1,
            combatXp = 0L,
            autoEatEnabled = true,
            autoEatFoodId = "cooked_trout"
        )
        combatDao.insertCombatStats(defaultStats)
        combatDao.clearCurrentEnemy()

        // Update player meta
        playerDao.updatePlayer(
            player.copy(
                ascensionCount = player.ascensionCount + 1,
                etherealSigils = player.etherealSigils + sigilsAwarded,
                currentSkill = null,
                currentActivity = null,
                activityStartTime = null,
                activityProgress = 0f
            )
        )
        return true
    }

    // Profile utilities
    suspend fun updatePlayerName(newName: String) {
        val player = playerDao.getPlayerSync() ?: return
        playerDao.updatePlayer(player.copy(name = newName))
    }

    fun deleteProfileData(profileId: Int) {
        val safe = profileId.coerceIn(1, 3)
        val dbName = "eternal_quest_database_profile_${safe}"
        appContext.deleteDatabase(dbName)
    }

    // Sigil Perks
    suspend fun purchaseXpPerk(): Boolean {
        val player = playerDao.getPlayerSync() ?: return false
        val perks = sigilPerksDao.getPerksSync() ?: SigilPerks()
        val def = com.eternalquest.util.PerkConfig.get("xp")
        if (perks.xpBonusLevel >= def.maxLevel) return false
        if (player.etherealSigils < def.costPerLevel) return false
        sigilPerksDao.updatePerks(perks.copy(xpBonusLevel = perks.xpBonusLevel + 1))
        playerDao.updatePlayer(player.copy(etherealSigils = player.etherealSigils - def.costPerLevel))
        return true
    }

    suspend fun purchaseSpeedPerk(): Boolean {
        val player = playerDao.getPlayerSync() ?: return false
        val perks = sigilPerksDao.getPerksSync() ?: SigilPerks()
        val def = com.eternalquest.util.PerkConfig.get("speed")
        if (perks.speedBonusLevel >= def.maxLevel) return false
        if (player.etherealSigils < def.costPerLevel) return false
        sigilPerksDao.updatePerks(perks.copy(speedBonusLevel = perks.speedBonusLevel + 1))
        playerDao.updatePlayer(player.copy(etherealSigils = player.etherealSigils - def.costPerLevel))
        return true
    }

    suspend fun purchaseLootPerk(): Boolean {
        val player = playerDao.getPlayerSync() ?: return false
        val perks = sigilPerksDao.getPerksSync() ?: SigilPerks()
        val def = com.eternalquest.util.PerkConfig.get("loot")
        if (perks.lootBonusLevel >= def.maxLevel) return false
        if (player.etherealSigils < def.costPerLevel) return false
        sigilPerksDao.updatePerks(perks.copy(lootBonusLevel = perks.lootBonusLevel + 1))
        playerDao.updatePlayer(player.copy(etherealSigils = player.etherealSigils - def.costPerLevel))
        return true
    }

    suspend fun resetSigilPerks(): Boolean {
        val player = playerDao.getPlayerSync() ?: return false
        val perks = sigilPerksDao.getPerksSync() ?: SigilPerks()
        val refund = perks.xpBonusLevel + perks.speedBonusLevel + perks.lootBonusLevel
        if (refund <= 0) return false
        sigilPerksDao.updatePerks(perks.copy(xpBonusLevel = 0, speedBonusLevel = 0, lootBonusLevel = 0))
        playerDao.updatePlayer(player.copy(etherealSigils = player.etherealSigils + refund))
        return true
    }

    // Multi-profile support
    suspend fun switchProfile(newProfileId: Int) {
        val safe = newProfileId.coerceIn(1, 3)
        ProfileManager.setCurrentProfileId(appContext, safe)
        profileId = safe
        database = DatabaseProvider.get(appContext, profileId)
        // Rewire DAOs and engines
        val newPlayerDao = database.playerDao()
        val newSkillDao = database.skillDao()
        val newItemDao = database.itemDao()
        val newBankDao = database.bankDao()
        val newCombatDao = database.combatDao()
        val newUpgradesDao = database.upgradesDao()
        val newSigilPerksDao = database.sigilPerksDao()

        // Reassign DAO references
        playerDao = newPlayerDao
        skillDao = newSkillDao
        itemDao = newItemDao
        bankDao = newBankDao
        combatDao = newCombatDao
        upgradesDao = newUpgradesDao
        sigilPerksDao = newSigilPerksDao
        // Recreate systems
        upgradeSystem = UpgradeSystem(newUpgradesDao, newBankDao)
        tickEngine = TickEngine(
            newPlayerDao,
            newSkillDao,
            newBankDao,
            timeService,
            onGoldEarned = { amount, source -> upgradeSystem.addGold(amount, source) },
            slotsPerTabProvider = {
                val upgrades = newUpgradesDao.getPlayerUpgradesSync() ?: PlayerUpgrades()
                QoLUpgrades.getBankSlotsPerTab(upgrades)
            },
            metaXpMultiplierProvider = {
                val perks = newSigilPerksDao.getPerksSync() ?: SigilPerks()
                1.0 + (perks.xpBonusLevel * 0.02)
            },
            speedFactorProvider = {
                val perks = newSigilPerksDao.getPerksSync() ?: SigilPerks()
                (1.0 - (perks.speedBonusLevel * 0.02)).coerceAtLeast(0.7)
            },
            lootChanceBonusProvider = {
                val perks = newSigilPerksDao.getPerksSync() ?: SigilPerks()
                1.0 + (perks.lootBonusLevel * 0.02)
            }
        )
        combatEngine = CombatEngine(
            newCombatDao,
            newBankDao,
            onGoldEarned = { amount, source -> upgradeSystem.addGold(amount, source) },
            autoEatPriorityProvider = { com.eternalquest.util.AutoEatPrefs.getPriority(appContext, profileId) },
            slotsPerTabProvider = {
                val upgrades = newUpgradesDao.getPlayerUpgradesSync() ?: PlayerUpgrades()
                QoLUpgrades.getBankSlotsPerTab(upgrades)
            },
            totalTabsProvider = {
                val upgrades = newUpgradesDao.getPlayerUpgradesSync() ?: PlayerUpgrades()
                QoLUpgrades.getTotalBankTabs(upgrades)
            },
            enemyProvider = { id -> com.eternalquest.util.EnemyCatalog.get(id) ?: com.eternalquest.data.entities.Enemies.ALL.find { it.id == id } }
        )

        // Initialize baseline data for the new profile
        initializeItems()
        initializeSkills()
        initializeCombatStats()
        initializeUpgrades()
        // Signal data source change so UI flows resubscribe live
        dataSource.value = dataSource.value + 1
    }
}
