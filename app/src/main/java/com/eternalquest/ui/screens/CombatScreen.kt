package com.eternalquest.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eternalquest.data.entities.*
import com.eternalquest.game.systems.CombatEvent
import com.eternalquest.game.systems.CombatTickResult
import com.eternalquest.ui.util.Sprites
import androidx.compose.ui.res.painterResource
import com.eternalquest.data.entities.Areas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombatScreen(
    combatStats: CombatStats?,
    currentEnemy: Enemy?,
    currentEnemyHp: Int,
    combatEvents: List<CombatEvent>,
    onStartCombat: (String) -> Unit,
    onEndCombat: () -> Unit,
    onEquipWeapon: (String?) -> Unit,
    onEquipArmor: (String?) -> Unit,
    onSetAutoEat: (Boolean, String?) -> Unit,
    onSetAutoEatThreshold: (Float) -> Unit
) {
    if (combatStats?.isInCombat == true && currentEnemy != null) {
        // Active Combat UI
        ActiveCombatScreen(
            combatStats = combatStats,
            enemy = currentEnemy,
            enemyHp = currentEnemyHp,
            combatEvents = combatEvents,
            onEndCombat = onEndCombat
        )
    } else {
        // Combat preparation UI
        CombatPrepScreen(
            combatStats = combatStats,
            onStartCombat = onStartCombat,
            onEquipWeapon = onEquipWeapon,
            onEquipArmor = onEquipArmor,
            onSetAutoEat = onSetAutoEat,
            onSetAutoEatThreshold = onSetAutoEatThreshold,
            onSetUseBestAutoEat = {}
        )
    }
}

@Composable
fun ActiveCombatScreen(
    combatStats: CombatStats,
    enemy: Enemy,
    enemyHp: Int,
    combatEvents: List<CombatEvent>,
    onEndCombat: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Combat Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val sprite = Sprites.forEnemyId(enemy.id)
            Image(painter = painterResource(id = sprite.resId), contentDescription = enemy.name, modifier = Modifier.size(40.dp))
            Text(
                text = "Combat: ${enemy.name}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        // HP Bars
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Player HP
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Your HP",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                HealthBar(
                    current = combatStats.hitpoints,
                    maximum = combatStats.maxHitpoints,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "${combatStats.hitpoints} / ${combatStats.maxHitpoints}",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Enemy HP
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = enemy.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                HealthBar(
                    current = enemyHp,
                    maximum = enemy.maxHitpoints,
                    color = MaterialTheme.colorScheme.error
                )
                
                Text(
                    text = "$enemyHp / ${enemy.maxHitpoints}",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Combat Log
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Combat Log",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true
                ) {
                    items(combatEvents.takeLast(50)) { event ->
                        CombatLogEntry(event)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Combat Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onEndCombat,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Flee")
            }
        }
    }
}

@Composable
fun CombatPrepScreen(
    combatStats: CombatStats?,
    onStartCombat: (String) -> Unit,
    onEquipWeapon: (String?) -> Unit,
    onEquipArmor: (String?) -> Unit,
    onSetAutoEat: (Boolean, String?) -> Unit,
    onSetAutoEatThreshold: (Float) -> Unit,
    onSetUseBestAutoEat: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Combat",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            combatStats?.let { stats ->
                CombatStatsCard(stats)
            }
        }
        
        item {
            EquipmentCard(
                combatStats = combatStats,
                onEquipWeapon = onEquipWeapon,
                onEquipArmor = onEquipArmor
            )
        }
        
        item {
            AutoEatCard(
                combatStats = combatStats,
                onSetAutoEat = onSetAutoEat,
                onSetAutoEatThreshold = onSetAutoEatThreshold,
                onSetUseBestAutoEat = onSetUseBestAutoEat
            )
        }
        
        item {
            AreaOverviewCard()
        }
        
        item {
            EnemySelectionCard(
                combatStats = combatStats,
                onStartCombat = onStartCombat
            )
        }
    }
}

@Composable
fun HealthBar(
    current: Int,
    maximum: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (maximum > 0) current.toFloat() / maximum.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300)
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .background(color)
        )
    }
}

@Composable
fun CombatLogEntry(event: CombatEvent) {
    val (icon, text) = when (event) {
        is CombatEvent.Attack -> {
            val attacker = if (event.attacker == CombatantType.PLAYER) "You" else "Enemy"
            val target = if (event.attacker == CombatantType.PLAYER) "enemy" else "you"
            val critText = if (event.critical) " (Critical!)" else ""
            val missText = if (!event.hit) "$attacker missed!" else ""
            
            if (event.hit) {
                val icon = if (event.attacker == CombatantType.PLAYER) Icons.Default.Gavel else Icons.Default.LocalFireDepartment
                icon to "$attacker dealt ${event.damage} damage to $target$critText"
            } else {
                Icons.Default.Block to missText
            }
        }
        is CombatEvent.AutoEat -> {
            Icons.Default.Fastfood to "Auto-ate ${event.foodId}, restored ${event.hpRestored} HP"
        }
    }
    
    val textColor = when (event) {
        is CombatEvent.Attack -> {
            when {
                !event.hit -> MaterialTheme.colorScheme.outline
                event.critical -> MaterialTheme.colorScheme.error
                event.attacker == CombatantType.PLAYER -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.error
            }
        }
        is CombatEvent.AutoEat -> MaterialTheme.colorScheme.secondary
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            modifier = Modifier.padding(vertical = 2.dp)
        )
    }
}

@Composable
fun CombatStatsCard(stats: CombatStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Combat Stats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    StatRow("Level", stats.calculateCombatLevel().toString())
                    StatRow("HP", "${stats.hitpoints}/${stats.maxHitpoints}")
                    StatRow("Attack", stats.attack.toString())
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    StatRow("XP", stats.combatXp.toString())
                    StatRow("Strength", stats.strength.toString()) 
                    StatRow("Defense", stats.defense.toString())
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AreaOverviewCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Areas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Areas.ALL_AREAS.forEach { area ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sprite = Sprites.forAreaId(area.id)
                    Image(painter = painterResource(id = sprite.resId), contentDescription = area.name, modifier = Modifier.size(28.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(area.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(
                                text = when (area.type) {
                                    AreaType.OVERWORLD -> "Overworld"
                                    AreaType.DUNGEON -> "Dungeon"
                                    AreaType.RAID -> "Raid"
                                    AreaType.BOSS_ROOM -> "Boss"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = "Lv ${area.minCombatLevel}-${area.maxCombatLevel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentCard(
    combatStats: CombatStats?,
    onEquipWeapon: (String?) -> Unit,
    onEquipArmor: (String?) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Equipment",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Current Equipment
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onEquipWeapon(null) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(combatStats?.equippedWeapon ?: "None")
                }
                
                Button(
                    onClick = { onEquipArmor(null) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(combatStats?.equippedArmor ?: "None")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoEatCard(
    combatStats: CombatStats?,
    onSetAutoEat: (Boolean, String?) -> Unit,
    onSetAutoEatThreshold: (Float) -> Unit,
    onSetUseBestAutoEat: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Auto-Eat Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Auto-eat enabled:")
                Switch(
                    checked = combatStats?.autoEatEnabled ?: false,
                    onCheckedChange = { enabled ->
                        onSetAutoEat(enabled, combatStats?.autoEatFoodId)
                    }
                )
            }
            
            if (combatStats?.autoEatEnabled == true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Use best available food:")
                    Switch(
                        checked = combatStats.useBestAutoEat,
                        onCheckedChange = { onSetUseBestAutoEat(it) }
                    )
                }
                var expanded by remember { mutableStateOf(false) }
                val foods = listOf("cooked_trout", "cooked_salmon", "cooked_tuna", "cooked_swordfish")
                val foodNames = mapOf(
                    "cooked_trout" to "Cooked Trout",
                    "cooked_salmon" to "Cooked Salmon",
                    "cooked_tuna" to "Cooked Tuna",
                    "cooked_swordfish" to "Cooked Swordfish"
                )
                val currentId = combatStats.autoEatFoodId ?: foods.first()
                Text(
                    text = "Food: ${foodNames[currentId] ?: currentId}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = foodNames[currentId] ?: currentId,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Food") }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        foods.forEach { id ->
                            DropdownMenuItem(
                                text = { Text(foodNames[id] ?: id) },
                                onClick = {
                                    expanded = false
                                    onSetAutoEat(true, id)
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Auto-eat threshold: ${(combatStats.autoEatThreshold * 100).toInt()}%")
                Slider(
                    value = combatStats.autoEatThreshold,
                    onValueChange = { onSetAutoEatThreshold(it) },
                    valueRange = 0.1f..0.9f,
                    steps = 7
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Food Priority:", style = MaterialTheme.typography.bodyMedium)
                val context = androidx.compose.ui.platform.LocalContext.current
                val profileId = com.eternalquest.util.ProfileManager.getCurrentProfileId(context)
                var localPriority by remember {
                    mutableStateOf(com.eternalquest.util.AutoEatPrefs.getPriority(context, profileId))
                }
                // reuse foodNames defined above
                localPriority.forEachIndexed { index, id ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(foodNames[id] ?: id)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(onClick = {
                                if (index > 0) {
                                    val list = localPriority.toMutableList()
                                    val tmp = list[index-1]
                                    list[index-1] = list[index]
                                    list[index] = tmp
                                    localPriority = list
                                }
                            }) { Text("Up") }
                            OutlinedButton(onClick = {
                                if (index < localPriority.size - 1) {
                                    val list = localPriority.toMutableList()
                                    val tmp = list[index+1]
                                    list[index+1] = list[index]
                                    list[index] = tmp
                                    localPriority = list
                                }
                            }) { Text("Down") }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Button(onClick = { com.eternalquest.util.AutoEatPrefs.setPriority(context, profileId, localPriority) }) { Text("Save Priority") }
            }
        }
    }
}

@Composable
fun EnemySelectionCard(
    combatStats: CombatStats?,
    onStartCombat: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Enemies",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            val playerLevel = combatStats?.calculateCombatLevel() ?: 1
            
            Enemies.ALL.forEach { enemy ->
                val canFight = playerLevel >= enemy.combatLevelRequired
                
                OutlinedButton(
                    onClick = { onStartCombat(enemy.id) },
                    enabled = canFight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        val sprite = Sprites.forEnemyId(enemy.id)
                        Image(painter = painterResource(id = sprite.resId), contentDescription = enemy.name, modifier = Modifier.size(28.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = enemy.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Level ${enemy.level}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            if (!canFight) {
                                Text(
                                    text = "Requires Combat Level ${enemy.combatLevelRequired}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text(
                                    text = "${enemy.experienceReward} XP | ${enemy.hitpoints} HP",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
