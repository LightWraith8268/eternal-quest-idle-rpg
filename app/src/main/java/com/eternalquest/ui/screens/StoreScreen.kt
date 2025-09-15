package com.eternalquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eternalquest.data.entities.*
import com.eternalquest.game.systems.UpgradeInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    goldBalance: Long,
    upgrades: List<UpgradeInfo>,
    onPurchaseUpgrade: (String) -> Unit,
    onSellItem: (String, Int) -> Unit
) {
    val context = LocalContext.current
    com.eternalquest.util.StoreConfig.load(context)
    val orderedCategories = com.eternalquest.util.StoreConfig.getOrderedCategories()
    var selectedCategory by remember { mutableStateOf(orderedCategories.firstOrNull() ?: UpgradeCategory.BANK_EXPANSION) }
    var query by remember { mutableStateOf("") }
    var affordableOnly by remember { mutableStateOf(false) }
    var ownedOnly by remember { mutableStateOf(false) }
    val profileId = com.eternalquest.util.ProfileManager.getCurrentProfileId(context)
    var sortMode by remember { mutableStateOf(com.eternalquest.util.StorePrefs.getSortMode(context, profileId)) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with gold balance
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QoL Store",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Gold",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = goldBalance.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Category tabs
        ScrollableTabRow(
            selectedTabIndex = UpgradeCategory.values().indexOf(selectedCategory),
            modifier = Modifier.fillMaxWidth()
        ) {
            orderedCategories.forEach { category ->
                Tab(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    text = { 
                        Text(
                            text = com.eternalquest.util.StoreConfig.getDisplayName(category),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    icon = {
                        val iconName = com.eternalquest.util.StoreConfig.getIconName(category)
                        val imageVector = when (iconName) {
                            "account_box" -> Icons.Default.AccountBox
                            "list" -> Icons.Default.List
                            "access_time" -> Icons.Default.AccessTime
                            "build" -> Icons.Default.Build
                            "palette" -> Icons.Default.Palette
                            else -> when (category) {
                                UpgradeCategory.BANK_EXPANSION -> Icons.Default.AccountBox
                                UpgradeCategory.ACTIVITY_QUEUE -> Icons.Default.List
                                UpgradeCategory.OFFLINE_TIME -> Icons.Default.AccessTime
                                UpgradeCategory.CONVENIENCE -> Icons.Default.Build
                                UpgradeCategory.COSMETIC -> Icons.Default.Palette
                            }
                        }
                        Icon(imageVector = imageVector, contentDescription = com.eternalquest.util.StoreConfig.getDisplayName(category))
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search upgrades") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Affordable")
                Spacer(modifier = Modifier.width(6.dp))
                Switch(checked = affordableOnly, onCheckedChange = { affordableOnly = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Hide owned")
                Spacer(modifier = Modifier.width(6.dp))
                Switch(checked = ownedOnly, onCheckedChange = { ownedOnly = it })
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sort")
                Spacer(modifier = Modifier.width(6.dp))
                AssistChip(onClick = { sortMode = "Name"; com.eternalquest.util.StorePrefs.setSortMode(context, profileId, sortMode) }, label = { Text("Name") })
                AssistChip(onClick = { sortMode = "Cost"; com.eternalquest.util.StorePrefs.setSortMode(context, profileId, sortMode) }, label = { Text("Cost") })
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Upgrades list for selected category
        val categoryUpgrades = upgrades
            .filter { it.upgrade.category == selectedCategory }
            .filter { if (affordableOnly) it.canAfford && !it.isPurchased else true }
            .filter { if (ownedOnly) it.isPurchased else true }
            .filter { it.upgrade.name.contains(query, ignoreCase = true) || it.upgrade.description.contains(query, ignoreCase = true) }
            .let { list ->
                when (sortMode) {
                    "Cost" -> list.sortedBy { it.cost }
                    else -> list.sortedBy { it.upgrade.name }
                }
            }
        
        // Category summary
        val totalInCategory = upgrades.count { it.upgrade.category == selectedCategory }
        val ownedInCategory = upgrades.count { it.upgrade.category == selectedCategory && it.isPurchased }
        val affordableInCategory = upgrades.count { it.upgrade.category == selectedCategory && it.canAfford && !it.isPurchased }
        Text(
            text = "${ownedInCategory}/${totalInCategory} owned • ${affordableInCategory} affordable",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categoryUpgrades) { upgradeInfo ->
                UpgradeCard(
                    upgradeInfo = upgradeInfo,
                    onPurchase = { onPurchaseUpgrade(upgradeInfo.upgrade.id) }
                )
                // Mark as seen for NEW badge persistence
                LaunchedEffect(upgradeInfo.upgrade.id) {
                    val seen = com.eternalquest.util.StorePrefs.getSeen(context, profileId)
                    if (upgradeInfo.meetsPrerequisites && !upgradeInfo.isPurchased && upgradeInfo.upgrade.id !in seen) {
                        com.eternalquest.util.StorePrefs.addSeen(context, profileId, upgradeInfo.upgrade.id)
                    }
                }
            }
            
            if (categoryUpgrades.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "No upgrades available",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                            
                            Text(
                                text = "Check back later for new upgrades!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradeCard(
    upgradeInfo: UpgradeInfo,
    onPurchase: () -> Unit
) {
    val upgrade = upgradeInfo.upgrade
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                upgradeInfo.isPurchased -> MaterialTheme.colorScheme.surfaceVariant
                !upgradeInfo.meetsPrerequisites -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                !upgradeInfo.canAfford -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = upgrade.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = upgrade.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Price and purchase button
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    // NEW badge for newly available upgrades
                    if (!upgradeInfo.isPurchased && upgradeInfo.meetsPrerequisites) {
                        AssistChip(onClick = {}, enabled = false, label = { Text("NEW") })
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    if (upgradeInfo.isPurchased) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "OWNED",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Gold",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = upgradeInfo.cost.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = onPurchase,
                            enabled = upgradeInfo.canAfford && upgradeInfo.meetsPrerequisites && !upgradeInfo.isPurchased,
                            modifier = Modifier.widthIn(min = 100.dp)
                        ) {
                            Text(
                                text = when {
                                    upgradeInfo.isPurchased -> "Owned"
                                    !upgradeInfo.meetsPrerequisites -> "Locked"
                                    !upgradeInfo.canAfford -> "Can't Afford"
                                    else -> "Purchase"
                                },
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
            
            // Prerequisites text
            if (!upgradeInfo.meetsPrerequisites && upgradeInfo.prerequisiteText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = upgradeInfo.prerequisiteText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            // Benefits preview for bank upgrades
            if (upgrade.category == UpgradeCategory.BANK_EXPANSION) {
                Spacer(modifier = Modifier.height(8.dp))
                BankUpgradeBenefits(upgrade.id)
            }
        }
    }
}

@Composable
fun BankUpgradeBenefits(upgradeId: String) {
    val benefitText = when (upgradeId) {
        "bank_tab_6" -> "Total capacity: 6 tabs × 15 slots = 90 slots"
        "bank_tab_7" -> "Total capacity: 7 tabs × 15 slots = 105 slots"
        "bank_tab_8" -> "Total capacity: 8 tabs × 15 slots = 120 slots"
        "bank_tab_9" -> "Total capacity: 9 tabs × 15 slots = 135 slots"
        "bank_tab_10" -> "Total capacity: 10 tabs × 15 slots = 150 slots"
        
        "bank_slots_20" -> "Total capacity: 5+ tabs × 20 slots = 100+ slots"
        "bank_slots_25" -> "Total capacity: 5+ tabs × 25 slots = 125+ slots"
        "bank_slots_30" -> "Total capacity: 5+ tabs × 30 slots = 150+ slots"
        "bank_slots_40" -> "Total capacity: 5+ tabs × 40 slots = 200+ slots"
        "bank_slots_50" -> "Total capacity: 5+ tabs × 50 slots = 250+ slots"
        
        else -> return
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = benefitText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Extension properties for UI
private val UpgradeCategory.displayName: String
    get() = when (this) {
        UpgradeCategory.BANK_EXPANSION -> "Bank"
        UpgradeCategory.ACTIVITY_QUEUE -> "Queue"
        UpgradeCategory.OFFLINE_TIME -> "Offline"
        UpgradeCategory.CONVENIENCE -> "Tools"
        UpgradeCategory.COSMETIC -> "Themes"
    }

private val UpgradeCategory.icon: ImageVector
    get() = when (this) {
        UpgradeCategory.BANK_EXPANSION -> Icons.Default.AccountBox
        UpgradeCategory.ACTIVITY_QUEUE -> Icons.Default.List
        UpgradeCategory.OFFLINE_TIME -> Icons.Default.AccessTime
        UpgradeCategory.CONVENIENCE -> Icons.Default.Build
        UpgradeCategory.COSMETIC -> Icons.Default.Palette
    }
