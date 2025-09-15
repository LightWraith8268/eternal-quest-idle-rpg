package com.eternalquest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eternalquest.data.entities.BankItem
import com.eternalquest.data.entities.GameItems
import com.eternalquest.data.entities.GoldSources
import com.eternalquest.ui.util.Sprites
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.ColorFilter
import com.eternalquest.util.BankPrefs
import com.eternalquest.util.ProfileManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankScreen(
    bankItems: List<BankItem>,
    selectedTab: Int,
    totalTabs: Int,
    slotsPerTab: Int,
    onTabSelected: (Int) -> Unit,
    onOpenStore: () -> Unit,
    toolsEnabled: Boolean = false,
    onSellItem: ((String, Int) -> Unit)? = null
) {
    val context = LocalContext.current
    val profileId = remember { ProfileManager.getCurrentProfileId(context) }
    var query by rememberSaveable(key = "bank_query_$profileId") { mutableStateOf(BankPrefs.getQuery(context, profileId)) }
    var sortMode by rememberSaveable(key = "bank_sort_$profileId") { mutableStateOf(BankPrefs.getSortMode(context, profileId)) }
    var selectedCategory by rememberSaveable(key = "bank_category_$profileId") { mutableStateOf(BankPrefs.getCategory(context, profileId)) }
    var showEmpty by rememberSaveable(key = "bank_show_$profileId") { mutableStateOf(BankPrefs.getShowEmpty(context, profileId)) }

    LaunchedEffect(query) { BankPrefs.setQuery(context, profileId, query) }
    LaunchedEffect(sortMode) { BankPrefs.setSortMode(context, profileId, sortMode) }
    LaunchedEffect(selectedCategory) { BankPrefs.setCategory(context, profileId, selectedCategory) }
    LaunchedEffect(showEmpty) { BankPrefs.setShowEmpty(context, profileId, showEmpty) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Bank",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Bank Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            val unlockedTabs = if (totalTabs > 0) totalTabs else 5
            val maxTabs = 10
            repeat(maxTabs) { tabIndex ->
                Tab(
                    selected = selectedTab == tabIndex,
                    onClick = {
                        if (tabIndex < unlockedTabs) onTabSelected(tabIndex) else onOpenStore()
                    },
                    enabled = tabIndex < unlockedTabs,
                    text = {
                        if (tabIndex < unlockedTabs) {
                            Text("Tab ${tabIndex + 1}")
                        } else {
                            Text("Locked ${tabIndex + 1}")
                        }
                    }
                )
            }
        }

        // Optional tools: search/sort (uses persisted state above)
        if (toolsEnabled) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(onClick = { sortMode = "Name" }, label = { Text("Sort: Name") })
                AssistChip(onClick = { sortMode = "Qty" }, label = { Text("Sort: Qty") })
            }

            // Category filter and show-empty toggle
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val categories = listOf(
                    "All", "ORE", "LOG", "FISH", "FOOD", "WEAPON", "ARMOR", "CONSUMABLE", "COMBAT_DROP", "MISC"
                )
                AssistChip(onClick = { selectedCategory = "All" }, label = { Text("All") })
                AssistChip(onClick = { selectedCategory = "FOOD" }, label = { Text("Food") })
                AssistChip(onClick = { selectedCategory = "ORE" }, label = { Text("Ore") })
                AssistChip(onClick = { selectedCategory = "WEAPON" }, label = { Text("Weapon") })
                AssistChip(onClick = { selectedCategory = "ARMOR" }, label = { Text("Armor") })
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show empty slots")
                Switch(checked = showEmpty, onCheckedChange = { showEmpty = it })
            }
            // Persist local UI states via remember in this composable scope
        }

        // Bank Grid
        val tabItems = bankItems.filter { it.tabIndex == selectedTab }
        val filteredByQuery = if (toolsEnabled && query.isNotBlank()) {
            tabItems.filter { item ->
                val meta = GameItems.ALL.find { it.id == item.itemId }
                (meta?.name ?: item.itemId).contains(query, ignoreCase = true)
            }
        } else tabItems
        val filtered = if (toolsEnabled) {
            if (selectedCategory == "All") filteredByQuery else filteredByQuery.filter { item ->
                val meta = GameItems.ALL.find { it.id == item.itemId }
                meta?.category?.name == selectedCategory
            }
        } else filteredByQuery
        val sorted = when (sortMode) {
            "Qty" -> filtered.sortedByDescending { it.quantity }
            else -> filtered.sortedBy { GameItems.ALL.find { meta -> meta.id == it.itemId }?.name ?: it.itemId }
        }
        val gridItems = if (toolsEnabled) {
            // If show empty disabled, pack only existing items; else fill grid by slot index
            if (!showEmpty) {
                sorted
            } else {
                (0 until slotsPerTab).map { slotIndex ->
                    sorted.find { it.slotIndex == slotIndex }
                }
            }
        } else {
            (0 until slotsPerTab).map { slotIndex ->
                sorted.find { it.slotIndex == slotIndex }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(gridItems) { bankItem ->
                BankSlot(bankItem = bankItem, onSellItem = onSellItem)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        
        // Bank Stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Bank Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Used Slots:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${tabItems.size} / $slotsPerTab",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Capacity:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${totalTabs} tabs x ${slotsPerTab} slots = ${totalTabs * slotsPerTab}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onOpenStore) {
                    Text("Need more space? Open Store")
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Items:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${tabItems.sumOf { it.quantity }}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        if (tabItems.isEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("This tab is empty.", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Earn items through activities or buy more capacity in the Store.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankSlot(bankItem: BankItem?, onSellItem: ((String, Int) -> Unit)? = null) {
    val item = if (bankItem != null) {
        GameItems.ALL.find { it.id == bankItem.itemId }
    } else null
    
    var showSell by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .size(64.dp)
            .clickable(enabled = bankItem != null && onSellItem != null) { showSell = true }
            .then(
                if (bankItem != null) {
                    Modifier.border(
                        1.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier.border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)
                    )
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (bankItem != null) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else 
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (item != null && bankItem != null) {
                val sprite = Sprites.forItemId(item.id)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = sprite.resId),
                        contentDescription = item.name,
                        modifier = Modifier.size(32.dp),
                        colorFilter = sprite.tint?.let { ColorFilter.tint(it) }
                    )
                    if (bankItem.quantity > 1) {
                        Text(
                            text = "${bankItem.quantity}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    if (showSell && bankItem != null && onSellItem != null) {
        val meta = GameItems.ALL.find { it.id == bankItem.itemId }
        val oneGold = GoldSources.getItemSellValue(bankItem.itemId, 1)
        val allGold = GoldSources.getItemSellValue(bankItem.itemId, bankItem.quantity)
        AlertDialog(
            onDismissRequest = { showSell = false },
            title = { Text("Sell ${meta?.name ?: bankItem.itemId}") },
            text = {
                Column {
                    Text("Sell 1 for ${oneGold} gold")
                    Text("Sell ${bankItem.quantity} for ${allGold} gold")
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        onSellItem(bankItem.itemId, 1)
                        showSell = false
                    }) { Text("Sell 1") }
                    TextButton(onClick = {
                        onSellItem(bankItem.itemId, bankItem.quantity)
                        showSell = false
                    }) { Text("Sell All") }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showSell = false }) { Text("Cancel") }
            }
        )
    }
}
