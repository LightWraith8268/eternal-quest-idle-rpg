package com.eternalquest.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eternalquest.data.entities.*
import com.eternalquest.repository.GameRepository
import com.eternalquest.game.systems.*
import com.eternalquest.ui.components.OfflineProgressData
import com.eternalquest.ui.components.SkillProgress
import com.eternalquest.ui.components.ItemGain
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GameRepository(application)
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    private val _combatEvents = MutableStateFlow<List<CombatEvent>>(emptyList())
    val combatEvents: StateFlow<List<CombatEvent>> = _combatEvents.asStateFlow()
    
    private val _currentEnemyHp = MutableStateFlow(0)
    val currentEnemyHp: StateFlow<Int> = _currentEnemyHp.asStateFlow()
    
    val player = repository.getPlayer().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    val skills = repository.getAllSkills().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val bankItems = repository.getAllBankItems().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val combatStats = repository.getCombatStats().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    val playerUpgrades = repository.getPlayerUpgrades().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    val goldBalance = repository.getGoldBalance().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val sigilPerks = repository.getSigilPerks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _autoEatPriority = MutableStateFlow<List<String>>(emptyList())
    val autoEatPriority: StateFlow<List<String>> = _autoEatPriority.asStateFlow()

    private val _availableUpgrades = MutableStateFlow<List<UpgradeInfo>>(emptyList())
    val availableUpgrades: StateFlow<List<UpgradeInfo>> = _availableUpgrades.asStateFlow()

    // Derived upgrade-based UI state
    val bankTabCount: StateFlow<Int> = playerUpgrades
        .map { upgrades -> QoLUpgrades.getTotalBankTabs(upgrades ?: PlayerUpgrades()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QoLUpgrades.getTotalBankTabs(PlayerUpgrades())
        )

    val bankSlotsPerTab: StateFlow<Int> = playerUpgrades
        .map { upgrades -> QoLUpgrades.getBankSlotsPerTab(upgrades ?: PlayerUpgrades()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QoLUpgrades.getBankSlotsPerTab(PlayerUpgrades())
        )

    // Ascension eligibility
    val canAscend: StateFlow<Boolean> = skills
        .map { it.isNotEmpty() && it.all { s -> s.level >= XpSystem.MAX_LEVEL } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    init {
        initializeGame()
        startGameTick()
        refreshAutoEatPriority()
    }
    
    private fun initializeGame() {
        viewModelScope.launch {
            // Initialize database
            repository.initializeItems()
            repository.initializeSkills()
            repository.initializeCombatStats()
            repository.initializeUpgrades()
            
            // Create player if doesn't exist
            if (player.value == null) {
                repository.createPlayer()
            }
            
            // Handle offline progress
            repository.handleOfflineProgress()
            
            // Load available upgrades
            refreshUpgrades()
        }
    }
    
    private fun startGameTick() {
        viewModelScope.launch {
            repository.createGameTick().collect { currentTime ->
                // Process skill activities
                repository.processGameTick(currentTime).collect { tickResult ->
                    when (tickResult) {
                        is TickResult.ActivityProgress -> {
                            _uiState.update { it.copy(activityProgress = tickResult.progress) }
                        }
                        is TickResult.ActivityCompleted -> {
                            repository.completeActivity(
                                tickResult.activity,
                                tickResult.skillLevel,
                                tickResult.prestigeCount
                            )
                            _uiState.update { it.copy(activityProgress = 0f) }
                        }
                        is TickResult.Idle -> {
                            _uiState.update { it.copy(activityProgress = 0f) }
                        }
                    }
                }
                
                // Process combat
                val combatResult = repository.processCombatTick(currentTime)
                when (combatResult) {
                    is CombatTickResult.CombatEvents -> {
                        _combatEvents.update { currentEvents ->
                            (currentEvents + combatResult.events).takeLast(100)
                        }
                        // Update enemy HP from combat events
                        combatResult.events.forEach { event ->
                            if (event is CombatEvent.Attack && event.attacker == CombatantType.PLAYER) {
                                _currentEnemyHp.value = event.targetRemainingHp
                            }
                        }
                    }
                    is CombatTickResult.Victory -> {
                        _combatEvents.update { emptyList() }
                        _currentEnemyHp.value = 0
                        // Victory handled by UI state change
                    }
                    is CombatTickResult.Defeat -> {
                        _combatEvents.update { emptyList() }
                        _currentEnemyHp.value = 0
                        // Defeat handled by UI state change - player safely respawned
                    }
                    else -> { /* No action needed */ }
                }
            }
        }
    }
    
    fun startActivity(skillName: String, activityId: String) {
        viewModelScope.launch {
            repository.startActivity(skillName, activityId)
        }
    }
    
    fun stopActivity() {
        viewModelScope.launch {
            repository.stopActivity()
        }
    }
    
    fun selectTab(tab: GameTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
    
    fun selectBankTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedBankTab = tabIndex) }
        // Persist per profile
        try {
            val ctx = getApplication<Application>().applicationContext
            val profileId = com.eternalquest.util.ProfileManager.getCurrentProfileId(ctx)
            com.eternalquest.util.BankPrefs.setSelectedTab(ctx, profileId, tabIndex)
        } catch (_: Exception) {}
    }
    
    fun showOfflineProgress(data: OfflineProgressData) {
        _uiState.update { 
            it.copy(
                showOfflineProgress = true,
                offlineProgressData = data
            )
        }
    }
    
    fun dismissOfflineProgress() {
        _uiState.update { 
            it.copy(
                showOfflineProgress = false,
                offlineProgressData = null
            )
        }
    }
    
    // Combat functions
    fun startCombat(enemyId: String) {
        viewModelScope.launch {
            val success = repository.startCombat(enemyId)
            if (success) {
                _combatEvents.update { emptyList() }
                val enemy = Enemies.ALL.find { it.id == enemyId }
                _currentEnemyHp.value = enemy?.maxHitpoints ?: 0
            }
        }
    }
    
    fun endCombat() {
        viewModelScope.launch {
            repository.endCombat()
            _combatEvents.update { emptyList() }
            _currentEnemyHp.value = 0
        }
    }
    
    fun equipWeapon(weaponId: String?) {
        viewModelScope.launch {
            repository.equipWeapon(weaponId)
        }
    }
    
    fun equipArmor(armorId: String?) {
        viewModelScope.launch {
            repository.equipArmor(armorId)
        }
    }
    
    fun setAutoEat(enabled: Boolean, foodId: String?) {
        viewModelScope.launch {
            repository.setAutoEat(enabled, foodId)
        }
    }
    
    fun setAutoEatThreshold(threshold: Float) {
        viewModelScope.launch { repository.setAutoEatThreshold(threshold) }
    }

    fun setUseBestAutoEat(useBest: Boolean) {
        viewModelScope.launch { repository.setUseBestAutoEat(useBest) }
    }

    fun refreshAutoEatPriority() {
        viewModelScope.launch { _autoEatPriority.value = repository.getAutoEatPriority() }
    }

    fun setAutoEatPriority(priority: List<String>) {
        repository.setAutoEatPriority(priority)
        _autoEatPriority.value = priority
    }
    
    // Upgrade system functions
    fun refreshUpgrades() {
        viewModelScope.launch {
            _availableUpgrades.value = repository.getAvailableUpgrades()
        }
    }
    
    fun purchaseUpgrade(upgradeId: String) {
        viewModelScope.launch {
            val result = repository.purchaseUpgrade(upgradeId)
            when (result) {
                is UpgradeResult.Success -> {
                    refreshUpgrades() // Refresh to show updated state
                }
                else -> {
                    // Handle error cases if needed
                }
            }
        }
    }
    
    fun sellItem(itemId: String, quantity: Int) {
        viewModelScope.launch {
            val goldEarned = repository.sellItem(itemId, quantity)
            if (goldEarned > 0) {
                refreshUpgrades() // Refresh to show updated affordability
            }
        }
    }
    
    suspend fun getBankTabs(): Int = repository.getBankTabs()
    suspend fun getBankSlotsPerTab(): Int = repository.getBankSlotsPerTab()

    // Prestige
    fun prestigeSkill(skillName: String) {
        viewModelScope.launch {
            repository.prestigeSkill(skillName)
        }
    }

    // Ascension
    fun ascend() {
        viewModelScope.launch {
            repository.ascend()
        }
    }

    fun purchaseXpPerk() {
        viewModelScope.launch {
            repository.purchaseXpPerk()
        }
    }

    fun purchaseSpeedPerk() {
        viewModelScope.launch {
            repository.purchaseSpeedPerk()
        }
    }

    fun purchaseLootPerk() {
        viewModelScope.launch {
            repository.purchaseLootPerk()
        }
    }

    fun resetSigilPerks() {
        viewModelScope.launch { repository.resetSigilPerks() }
    }

    // Profile utilities
    fun renamePlayer(newName: String) {
        viewModelScope.launch { repository.updatePlayerName(newName) }
    }

    fun switchProfileNow(profileId: Int) {
        viewModelScope.launch { repository.switchProfile(profileId) }
    }

    fun deleteProfile(profileId: Int) {
        repository.deleteProfileData(profileId)
    }
}

data class MainUiState(
    val selectedTab: GameTab = GameTab.SKILLS,
    val selectedBankTab: Int = 0,
    val activityProgress: Float = 0f,
    val showOfflineProgress: Boolean = false,
    val offlineProgressData: OfflineProgressData? = null
)

enum class GameTab {
    SKILLS,
    BANK,
    COMBAT,
    CHARACTER,
    STORE,
    SETTINGS
}
