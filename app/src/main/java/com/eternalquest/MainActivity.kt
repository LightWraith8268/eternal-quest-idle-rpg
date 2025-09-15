package com.eternalquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.*
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import android.os.Build
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eternalquest.ui.components.GameBottomNavigation
import com.eternalquest.ui.components.OfflineProgressDialog
import com.eternalquest.ui.screens.BankScreen
import com.eternalquest.ui.screens.SkillsScreen
import com.eternalquest.ui.screens.CombatScreen
import com.eternalquest.ui.screens.StoreScreen
import com.eternalquest.ui.util.Sprites
import com.eternalquest.ui.theme.EternalQuestTheme
import com.eternalquest.ui.viewmodels.GameTab
import com.eternalquest.ui.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            EternalQuestTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val player by viewModel.player.collectAsStateWithLifecycle()
    val skills by viewModel.skills.collectAsStateWithLifecycle()
    val bankItems by viewModel.bankItems.collectAsStateWithLifecycle()
    val combatStats by viewModel.combatStats.collectAsStateWithLifecycle()
    val combatEvents by viewModel.combatEvents.collectAsStateWithLifecycle()
    val currentEnemyHp by viewModel.currentEnemyHp.collectAsStateWithLifecycle()
    
    val goldBalance by viewModel.goldBalance.collectAsStateWithLifecycle()
    val availableUpgrades by viewModel.availableUpgrades.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Eternal Quest")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            com.eternalquest.ui.components.GameBottomNavigation(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::selectTab
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (uiState.selectedTab) {
                GameTab.SKILLS -> {
                    SkillsScreen(
                        skills = skills,
                        currentActivity = player?.currentActivity,
                        activityProgress = uiState.activityProgress,
                        onStartActivity = viewModel::startActivity,
                        onStopActivity = viewModel::stopActivity,
                        onPrestigeSkill = viewModel::prestigeSkill
                    )
                }
                
                GameTab.BANK -> {
                    val totalTabs by viewModel.bankTabCount.collectAsStateWithLifecycle()
                    val slotsPerTab by viewModel.bankSlotsPerTab.collectAsStateWithLifecycle()
                    val upgrades by viewModel.playerUpgrades.collectAsStateWithLifecycle()
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val profileId = com.eternalquest.util.ProfileManager.getCurrentProfileId(context)
                    val persistedTab = remember { com.eternalquest.util.BankPrefs.getSelectedTab(context, profileId) }
                    val baseSelected = if (uiState.selectedBankTab == 0 && persistedTab in 0 until totalTabs) persistedTab else uiState.selectedBankTab
                    val safeSelected = baseSelected.coerceIn(0, (totalTabs - 1).coerceAtLeast(0))
                    if (safeSelected != uiState.selectedBankTab) {
                        // Ensure UI state stays within new bounds if tabs changed
                        LaunchedEffect(totalTabs) { viewModel.selectBankTab(safeSelected) }
                    }
                    BankScreen(
                        bankItems = bankItems,
                        selectedTab = safeSelected,
                        totalTabs = totalTabs,
                        slotsPerTab = slotsPerTab,
                        onTabSelected = viewModel::selectBankTab,
                        onOpenStore = { viewModel.selectTab(com.eternalquest.ui.viewmodels.GameTab.STORE) },
                        toolsEnabled = upgrades?.sortingToolsUnlocked == true,
                        onSellItem = viewModel::sellItem
                    )
                }
                
                GameTab.COMBAT -> {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    com.eternalquest.util.EnemyCatalog.load(context)
                    val currentEnemy = combatStats?.currentEnemyId?.let { enemyId ->
                        com.eternalquest.util.EnemyCatalog.get(enemyId) ?: com.eternalquest.data.entities.Enemies.ALL.find { it.id == enemyId }
                    }
                    CombatScreen(
                        combatStats = combatStats,
                        currentEnemy = currentEnemy,
                        currentEnemyHp = currentEnemyHp,
                        combatEvents = combatEvents,
                        
                        onStartCombat = viewModel::startCombat,
                        onEndCombat = viewModel::endCombat,
                        onEquipWeapon = viewModel::equipWeapon,
                        onEquipArmor = viewModel::equipArmor,
                        onSetAutoEat = viewModel::setAutoEat,
                        onSetAutoEatThreshold = viewModel::setAutoEatThreshold
                    )
                }
                
                GameTab.CHARACTER -> {
                    val canAscend by viewModel.canAscend.collectAsStateWithLifecycle()
                    val sigilPerks by viewModel.sigilPerks.collectAsStateWithLifecycle()
                    CharacterScreen(
                        player = player,
                        onAscend = { viewModel.ascend() },
                        canAscend = canAscend,
                        sigilPerks = sigilPerks,
                        onPurchaseXpPerk = { viewModel.purchaseXpPerk() },
                        onPurchaseSpeedPerk = { viewModel.purchaseSpeedPerk() },
                        onPurchaseLootPerk = { viewModel.purchaseLootPerk() },
                        onResetPerks = { viewModel.resetSigilPerks() }
                    )
                }
                
                GameTab.STORE -> {
                    StoreScreen(
                        goldBalance = goldBalance?.currentGold ?: 0L,
                        upgrades = availableUpgrades,
                        onPurchaseUpgrade = viewModel::purchaseUpgrade,
                        onSellItem = viewModel::sellItem
                    )
                }
                
                GameTab.SETTINGS -> {
                    SettingsScreen(
                        onSwitchProfile = { id -> viewModel.switchProfileNow(id) },
                        onDeleteProfile = { id -> viewModel.deleteProfile(id) },
                        onRename = { name -> viewModel.renamePlayer(name) }
                    )
                }
            }
        }
        
        // Offline Progress Dialog
        if (uiState.showOfflineProgress && uiState.offlineProgressData != null) {
            uiState.offlineProgressData?.let { data ->
                OfflineProgressDialog(
                    offlineData = data,
                    onDismiss = viewModel::dismissOfflineProgress
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterScreen(
    player: com.eternalquest.data.entities.Player?,
    onAscend: (() -> Unit)? = null,
    canAscend: Boolean = false,
    sigilPerks: com.eternalquest.data.entities.SigilPerks? = null,
    onPurchaseXpPerk: (() -> Unit)? = null,
    onPurchaseSpeedPerk: (() -> Unit)? = null,
    onPurchaseLootPerk: (() -> Unit)? = null,
    onResetPerks: (() -> Unit)? = null
) {
    if (player != null) {
        var showAscendConfirm by remember { mutableStateOf(false) }
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Character: ${player.name}",
                style = MaterialTheme.typography.headlineMedium
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Created: ${java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(player.createdAt))}",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = "Total Play Time: ${formatPlayTime(player.totalPlayTime)}",
                style = MaterialTheme.typography.bodyLarge
            )
            
            player.currentActivity?.let { activity ->
                Text(
                    text = "Current Activity: $activity",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Ascensions: ${player.ascensionCount} | Sigils: ${player.etherealSigils}",
                style = MaterialTheme.typography.bodyLarge
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { showAscendConfirm = true },
                enabled = canAscend && onAscend != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (canAscend) "Ascend: Reset character, gain Sigils" else "Ascend (requires all skills 100)")
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Sigil Perks",
                style = MaterialTheme.typography.titleMedium
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val level = sigilPerks?.xpBonusLevel ?: 0
                    Text("XP Bonus: +${level * 2}% (Level $level/5)")
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onPurchaseXpPerk?.invoke() },
                        enabled = (player.etherealSigils > 0) && (level < 5) && onPurchaseXpPerk != null
                    ) {
                        Text("Spend 1 Sigil: +2% XP")
                    }
                }
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val level = sigilPerks?.speedBonusLevel ?: 0
                    Text("Speed Bonus: -${level * 2}% time (Level $level/5)")
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onPurchaseSpeedPerk?.invoke() },
                        enabled = (player.etherealSigils > 0) && (level < 5) && onPurchaseSpeedPerk != null
                    ) { Text("Spend 1 Sigil: -2% Time") }
                }
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val level = sigilPerks?.lootBonusLevel ?: 0
                    Text("Loot Bonus: +${level * 2}% chance (Level $level/5)")
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onPurchaseLootPerk?.invoke() },
                        enabled = (player.etherealSigils > 0) && (level < 5) && onPurchaseLootPerk != null
                    ) { Text("Spend 1 Sigil: +2% Loot Chance") }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = { onResetPerks?.invoke() },
                enabled = onResetPerks != null
            ) { Text("Reset Perks (refund Sigils)") }
        }

        if (showAscendConfirm) {
            AlertDialog(
                onDismissRequest = { showAscendConfirm = false },
                title = { Text("Confirm Ascension") },
                text = { Text("Ascend now? This resets skills, bank, gold/upgrades, and combat state. You will gain Ethereal Sigils based on maxed skills. This cannot be undone.") },
                confirmButton = {
                    Button(onClick = {
                        onAscend?.invoke()
                        showAscendConfirm = false
                    }) { Text("Ascend") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showAscendConfirm = false }) { Text("Cancel") }
                }
            )
        }
    } else {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun SettingsScreen(
    onSwitchProfile: (Int) -> Unit = {},
    onDeleteProfile: (Int) -> Unit = {},
    onRename: (String) -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedProfile by remember { mutableStateOf(com.eternalquest.util.ProfileManager.getCurrentProfileId(context)) }
    var saved by remember { mutableStateOf(false) }
    val activity = context as? android.app.Activity
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Active Profile",
                    style = MaterialTheme.typography.titleMedium
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..3).forEach { id ->
                        Button(
                            onClick = { selectedProfile = id; saved = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedProfile == id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) { Text("Profile $id") }
                    }
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    com.eternalquest.util.ProfileManager.setCurrentProfileId(context, selectedProfile)
                    saved = true
                }) { Text("Set Active Profile") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onSwitchProfile(selectedProfile) }) { Text("Switch Now (no restart)") }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tip: Tap Restart App to apply immediately.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                var showDelete by remember { mutableStateOf(false) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        // Apply now via Activity recreate
                        activity?.let { it.recreate() }
                    }) { Text("Apply Now") }
                    Button(
                        onClick = { showDelete = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) { Text("Delete Profile Data") }
                }
                if (showDelete) {
                    AlertDialog(
                        onDismissRequest = { showDelete = false },
                        title = { Text("Delete Profile ${selectedProfile} Data?") },
                        text = { Text("This will permanently delete local data for the selected profile. This cannot be undone.") },
                        confirmButton = {
                            Button(onClick = {
                                onDeleteProfile(selectedProfile)
                                showDelete = false
                            }) { Text("Delete") }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = { showDelete = false }) { Text("Cancel") }
                        }
                    )
                }
                if (saved) {
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Profile saved. Restart app to switch.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
        // Character rename (current profile)
        val renameScope = rememberCoroutineScope()
        Card(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.foundation.layout.Column(modifier = Modifier.padding(16.dp)) {
                Text("Character Name", style = MaterialTheme.typography.titleMedium)
                var name by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Enter new name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { renameScope.launch { onRename(name.ifBlank { "Adventurer" }) } }) { Text("Save Name") }
            }
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
        // About
        var showChangelog by remember { mutableStateOf(false) }
        var showReleaseNotes by remember { mutableStateOf(false) }
        var showReadme by remember { mutableStateOf(false) }
        var showSpritePreview by remember { mutableStateOf(false) }
        val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
        Card(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.foundation.layout.Column(modifier = Modifier.padding(16.dp)) {
                Text("About", style = MaterialTheme.typography.titleMedium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                val pkgManager = context.packageManager
                val pkgName = context.packageName
                val (appVersionName, appVersionCode) = remember(context) {
                    try {
                        val info = pkgManager.getPackageInfo(pkgName, 0)
                        val vName = info.versionName ?: "dev"
                        val vCode = if (Build.VERSION.SDK_INT >= 28) info.longVersionCode else @Suppress("DEPRECATION") info.versionCode.toLong()
                        vName to vCode
                    } catch (e: Exception) {
                        "dev" to 0L
                    }
                }
                Text("Version: ${appVersionName}", style = MaterialTheme.typography.bodyMedium)
                Text("Code: ${appVersionCode}", style = MaterialTheme.typography.bodyMedium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Eternal Quest is an idle RPG featuring deterministic offline progression, Prestige/Ascension, and a JSON‑tunable QoL Store.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { clipboard.setText(androidx.compose.ui.text.AnnotatedString(appVersionName)) }) { Text("Copy Version") }
                    OutlinedButton(onClick = { showChangelog = true }) { Text("View Changelog") }
                    OutlinedButton(onClick = { showReleaseNotes = true }) { Text("Release Notes") }
                    OutlinedButton(onClick = { showReadme = true }) { Text("README") }
                    OutlinedButton(onClick = { showSpritePreview = true }) { Text("Sprite Preview") }
                }
            }
        }

        if (showChangelog) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val text = remember {
                try { context.assets.open("CHANGELOG.md").bufferedReader().use { it.readText() } } catch (e: Exception) { "No changelog available." }
            }
            AlertDialog(
                onDismissRequest = { showChangelog = false },
                title = { Text("Changelog") },
                text = {
                    LazyColumn(modifier = Modifier.heightIn(min = 100.dp, max = 400.dp)) {
                        item { Text(text, style = MaterialTheme.typography.bodySmall) }
                    }
                },
                confirmButton = { Button(onClick = { showChangelog = false }) { Text("Close") } }
            )
        }

        if (showReleaseNotes) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val text = remember {
                try { context.assets.open("RELEASE_NOTES.md").bufferedReader().use { it.readText() } } catch (e: Exception) { "No release notes available." }
            }
            AlertDialog(
                onDismissRequest = { showReleaseNotes = false },
                title = { Text("Release Notes") },
                text = {
                    LazyColumn(modifier = Modifier.heightIn(min = 100.dp, max = 400.dp)) {
                        item { Text(text, style = MaterialTheme.typography.bodySmall) }
                    }
                },
                confirmButton = { Button(onClick = { showReleaseNotes = false }) { Text("Close") } }
            )
        }

        if (showReadme) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val text = remember {
                try { context.assets.open("README.md").bufferedReader().use { it.readText() } } catch (e: Exception) { "No README available." }
            }
            AlertDialog(
                onDismissRequest = { showReadme = false },
                title = { Text("README") },
                text = {
                    LazyColumn(modifier = Modifier.heightIn(min = 100.dp, max = 400.dp)) {
                        item { Text(text, style = MaterialTheme.typography.bodySmall) }
                    }
                },
                confirmButton = { Button(onClick = { showReadme = false }) { Text("Close") } }
            )
        }

        if (showSpritePreview) {
            AlertDialog(
                onDismissRequest = { showSpritePreview = false },
                title = { Text("Sprite Preview") },
                text = {
                    val allItems = com.eternalquest.data.entities.GameItems.ALL
                    val allEnemies = com.eternalquest.data.entities.Enemies.ALL
                    LazyColumn(modifier = Modifier.heightIn(min = 200.dp, max = 500.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item { Text("Enemies", style = MaterialTheme.typography.titleMedium) }
                        items(allEnemies) { enemy ->
                            val sprite = com.eternalquest.ui.util.Sprites.forEnemyId(enemy.id)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                androidx.compose.foundation.Image(painter = androidx.compose.ui.res.painterResource(id = sprite.resId), contentDescription = enemy.name, modifier = Modifier.size(28.dp))
                                Text(enemy.name)
                            }
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                        item { Text("Items", style = MaterialTheme.typography.titleMedium) }
                        items(allItems) { itx ->
                            val sprite = com.eternalquest.ui.util.Sprites.forItemId(itx.id)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                androidx.compose.foundation.Image(painter = androidx.compose.ui.res.painterResource(id = sprite.resId), contentDescription = itx.name, modifier = Modifier.size(24.dp))
                                Text(itx.name)
                            }
                        }
                    }
                },
                confirmButton = { Button(onClick = { showSpritePreview = false }) { Text("Close") } }
            )
        }
    }
}

fun formatPlayTime(milliseconds: Long): String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    
    return when {
        days > 0 -> "${days}d ${hours % 24}h ${minutes % 60}m"
        hours > 0 -> "${hours}h ${minutes % 60}m"
        minutes > 0 -> "${minutes}m ${seconds % 60}s"
        else -> "${seconds}s"
    }
}

