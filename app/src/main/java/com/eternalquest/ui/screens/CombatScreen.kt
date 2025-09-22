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
import com.eternalquest.game.systems.CombatSystem
import com.eternalquest.game.systems.CombatTickResult
import com.eternalquest.ui.util.Sprites
import androidx.compose.ui.res.painterResource
import com.eternalquest.util.AutoEatPrefs
import java.util.Locale

data class AutoEatFoodOption(
    val id: String,
    val name: String,
    val quantity: Int,
    val healAmount: Int,
    val value: Int
) {
    val owned: Boolean get() = quantity > 0
}

data class WeaponInventoryOption(
    val id: String,
    val name: String,
    val category: WeaponCategory,
    val levelRequired: Int,
    val attackBonus: Int,
    val strengthBonus: Int,
    val attackSpeed: Long,
    val accuracy: Float,
    val quantity: Int
)

data class ArmorInventoryOption(
    val id: String,
    val name: String,
    val slot: ArmorSlot,
    val levelRequired: Int,
    val defenseBonus: Int,
    val hitpointBonus: Int,
    val quantity: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombatScreen(
    combatStats: CombatStats?,
    currentEnemy: Enemy?,
    currentEnemyHp: Int,
    combatEvents: List<CombatEvent>,
    showCombatLog: Boolean,
    availableFoods: List<AutoEatFoodOption>,
    availableWeapons: List<WeaponInventoryOption>,
    availableArmor: List<ArmorInventoryOption>,
    autoEatPriority: List<String>,
    onStartCombat: (String) -> Unit,
    onEndCombat: () -> Unit,
    onEquipWeapon: (String?) -> Unit,
    onEquipArmor: (String?) -> Unit,
    onSetAutoEat: (Boolean, String?) -> Unit,
    onSetAutoEatThreshold: (Float) -> Unit,
    onSetUseBestAutoEat: (Boolean) -> Unit,
    onSetAutoEatPriority: (List<String>) -> Unit
) {
    if (combatStats?.isInCombat == true && currentEnemy != null) {
        // Active Combat UI
        ActiveCombatScreen(
            combatStats = combatStats,
            enemy = currentEnemy,
            enemyHp = currentEnemyHp,
            combatEvents = combatEvents,
            showCombatLog = showCombatLog,
            onEndCombat = onEndCombat
        )
    } else {
        // Combat preparation UI
        CombatPrepScreen(
            combatStats = combatStats,
            availableFoods = availableFoods,
            availableWeapons = availableWeapons,
            availableArmor = availableArmor,
            autoEatPriority = autoEatPriority,
            onStartCombat = onStartCombat,
            onEquipWeapon = onEquipWeapon,
            onEquipArmor = onEquipArmor,
            onSetAutoEat = onSetAutoEat,
            onSetAutoEatThreshold = onSetAutoEatThreshold,
            onSetUseBestAutoEat = onSetUseBestAutoEat,
            onSetAutoEatPriority = onSetAutoEatPriority
        )
    }
}

@Composable
fun ActiveCombatScreen(
    combatStats: CombatStats,
    enemy: Enemy,
    enemyHp: Int,
    combatEvents: List<CombatEvent>,
    showCombatLog: Boolean,
    onEndCombat: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Combat Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val painter = Sprites.painterForEnemyId(enemy.id)
            Image(painter = painter, contentDescription = enemy.name, modifier = Modifier.size(40.dp))
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
            if (showCombatLog) {
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
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Combat log hidden",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enable the log in Settings to review attacks, healing, and loot rolls during battle.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
    availableFoods: List<AutoEatFoodOption>,
    availableWeapons: List<WeaponInventoryOption>,
    availableArmor: List<ArmorInventoryOption>,
    autoEatPriority: List<String>,
    onStartCombat: (String) -> Unit,
    onEquipWeapon: (String?) -> Unit,
    onEquipArmor: (String?) -> Unit,
    onSetAutoEat: (Boolean, String?) -> Unit,
    onSetAutoEatThreshold: (Float) -> Unit,
    onSetUseBestAutoEat: (Boolean) -> Unit,
    onSetAutoEatPriority: (List<String>) -> Unit
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
                availableWeapons = availableWeapons,
                availableArmor = availableArmor,
                onEquipWeapon = onEquipWeapon,
                onEquipArmor = onEquipArmor
            )
        }
        
        item {
            AutoEatCard(
                combatStats = combatStats,
                availableFoods = availableFoods,
                autoEatPriority = autoEatPriority,
                onSetAutoEat = onSetAutoEat,
                onSetAutoEatThreshold = onSetAutoEatThreshold,
                onSetUseBestAutoEat = onSetUseBestAutoEat,
                onSetAutoEatPriority = onSetAutoEatPriority
            )
        }
        
        item {
            val context = androidx.compose.ui.platform.LocalContext.current
            com.eternalquest.util.AreasCatalog.load(context)
            AreaOverviewCard(areas = com.eternalquest.util.AreasCatalog.all())
        }
        
        item {
            val context2 = androidx.compose.ui.platform.LocalContext.current
            com.eternalquest.util.EnemyCatalog.load(context2)
            val enemiesLocal = com.eternalquest.util.EnemyCatalog.all()
            EnemySelectionCard(
                combatStats = combatStats,
                enemies = enemiesLocal,
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
fun AreaOverviewCard(areas: List<com.eternalquest.util.AreaDef>) {
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
            
            areas.forEach { area ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val painter = Sprites.painterForAreaId(area.id)
                    Image(painter = painter, contentDescription = area.name, modifier = Modifier.size(28.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(area.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(
                                text = area.type,
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
    availableWeapons: List<WeaponInventoryOption>,
    availableArmor: List<ArmorInventoryOption>,
    onEquipWeapon: (String?) -> Unit,
    onEquipArmor: (String?) -> Unit
) {
    val playerAttack = combatStats?.attack ?: 1
    val playerDefense = combatStats?.defense ?: 1

    val sortedWeapons = remember(availableWeapons) {
        availableWeapons.sortedWith(
            compareBy<WeaponInventoryOption> { it.levelRequired }
                .thenBy { it.name }
        )
    }
    val sortedArmor = remember(availableArmor) {
        availableArmor.sortedWith(
            compareBy<ArmorInventoryOption> { it.levelRequired }
                .thenBy { it.name }
        )
    }

    val currentWeapon = sortedWeapons.find { it.id == combatStats?.equippedWeapon }
    val currentArmor = sortedArmor.find { it.id == combatStats?.equippedArmor }

    val recommendedWeapon = remember(sortedWeapons, playerAttack) {
        sortedWeapons
            .filter { it.quantity > 0 && playerAttack >= it.levelRequired }
            .maxWithOrNull(
                compareBy<WeaponInventoryOption> { it.attackBonus + it.strengthBonus }
                    .thenByDescending { it.accuracy }
            )
    }
    val recommendedArmor = remember(sortedArmor, playerDefense) {
        sortedArmor
            .filter { it.quantity > 0 && playerDefense >= it.levelRequired }
            .maxWithOrNull(compareBy<ArmorInventoryOption> { it.defenseBonus + it.hitpointBonus })
    }

    var weaponMenuExpanded by remember { mutableStateOf(false) }
    var armorMenuExpanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Equipment",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Weapon",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                if (sortedWeapons.isEmpty()) {
                    Text(
                        text = "Craft or loot a weapon to equip it.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    ExposedDropdownMenuBox(
                        expanded = weaponMenuExpanded,
                        onExpandedChange = {
                            weaponMenuExpanded = if (sortedWeapons.isNotEmpty()) !weaponMenuExpanded else false
                        }
                    ) {
                        OutlinedTextField(
                            value = currentWeapon?.name ?: "Select weapon",
                            onValueChange = {},
                            readOnly = true,
                            enabled = sortedWeapons.isNotEmpty(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            label = { Text("Equipped weapon") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = weaponMenuExpanded)
                            },
                            supportingText = {
                                if (currentWeapon != null) {
                                    Column {
                                        Text(
                                            text = "Atk +${currentWeapon.attackBonus} • Str +${currentWeapon.strengthBonus} • ${formatAttackSpeed(currentWeapon.attackSpeed)} • ${formatAccuracy(currentWeapon.accuracy)}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "${currentWeapon.quantity} owned • Requires Attack ${currentWeapon.levelRequired}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (playerAttack >= currentWeapon.levelRequired) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Choose a weapon from your bank",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = weaponMenuExpanded,
                            onDismissRequest = { weaponMenuExpanded = false }
                        ) {
                            sortedWeapons.forEach { option ->
                                val meetsLevel = playerAttack >= option.levelRequired
                                val sprite = Sprites.painterForItemId(option.id)
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(option.name, fontWeight = FontWeight.Medium)
                                            Text(
                                                text = "Atk +${option.attackBonus} • Str +${option.strengthBonus} • ${formatAttackSpeed(option.attackSpeed)} • ${formatAccuracy(option.accuracy)}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                text = "${option.quantity} owned • Requires Attack ${option.levelRequired}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (meetsLevel) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                                            )
                                        }
                                    },
                                    onClick = {
                                        weaponMenuExpanded = false
                                        if (option.quantity > 0 && meetsLevel) {
                                            onEquipWeapon(option.id)
                                        }
                                    },
                                    enabled = option.quantity > 0 && meetsLevel,
                                    leadingIcon = {
                                        Image(
                                            painter = sprite,
                                            contentDescription = option.name,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onEquipWeapon(null) },
                            enabled = combatStats?.equippedWeapon != null
                        ) {
                            Text("Unequip")
                        }
                        if (recommendedWeapon != null && recommendedWeapon.id != combatStats?.equippedWeapon) {
                            AssistChip(
                                onClick = { onEquipWeapon(recommendedWeapon.id) },
                                enabled = playerAttack >= recommendedWeapon.levelRequired && recommendedWeapon.quantity > 0,
                                label = { Text("Equip ${recommendedWeapon.name}") }
                            )
                        }
                    }

                    Text(
                        text = recommendedWeapon?.let { "Best owned: ${it.name}" } ?: "No usable weapon meets your Attack level",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Armor",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                if (sortedArmor.isEmpty()) {
                    Text(
                        text = "Forge or loot armor to protect yourself.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    ExposedDropdownMenuBox(
                        expanded = armorMenuExpanded,
                        onExpandedChange = {
                            armorMenuExpanded = if (sortedArmor.isNotEmpty()) !armorMenuExpanded else false
                        }
                    ) {
                        OutlinedTextField(
                            value = currentArmor?.name ?: "Select armor",
                            onValueChange = {},
                            readOnly = true,
                            enabled = sortedArmor.isNotEmpty(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            label = { Text("Equipped armor") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = armorMenuExpanded)
                            },
                            supportingText = {
                                if (currentArmor != null) {
                                    Column {
                                        Text(
                                            text = "Def +${currentArmor.defenseBonus} • HP +${currentArmor.hitpointBonus}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "${currentArmor.quantity} owned • Requires Defense ${currentArmor.levelRequired}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (playerDefense >= currentArmor.levelRequired) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                                        )
                                    }
                                } else {
                                    Text("Choose armor from your bank", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = armorMenuExpanded,
                            onDismissRequest = { armorMenuExpanded = false }
                        ) {
                            sortedArmor.forEach { option ->
                                val meetsLevel = playerDefense >= option.levelRequired
                                val sprite = Sprites.painterForItemId(option.id)
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(option.name, fontWeight = FontWeight.Medium)
                                            Text(
                                                text = "Def +${option.defenseBonus} • HP +${option.hitpointBonus}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                text = "${option.quantity} owned • Requires Defense ${option.levelRequired}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (meetsLevel) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                                            )
                                        }
                                    },
                                    onClick = {
                                        armorMenuExpanded = false
                                        if (option.quantity > 0 && meetsLevel) {
                                            onEquipArmor(option.id)
                                        }
                                    },
                                    enabled = option.quantity > 0 && meetsLevel,
                                    leadingIcon = {
                                        Image(
                                            painter = sprite,
                                            contentDescription = option.name,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onEquipArmor(null) },
                            enabled = combatStats?.equippedArmor != null
                        ) {
                            Text("Unequip")
                        }
                        if (recommendedArmor != null && recommendedArmor.id != combatStats?.equippedArmor) {
                            AssistChip(
                                onClick = { onEquipArmor(recommendedArmor.id) },
                                enabled = playerDefense >= recommendedArmor.levelRequired && recommendedArmor.quantity > 0,
                                label = { Text("Equip ${recommendedArmor.name}") }
                            )
                        }
                    }

                    Text(
                        text = recommendedArmor?.let { "Best owned: ${it.name}" } ?: "No usable armor meets your Defense level",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoEatCard(
    combatStats: CombatStats?,
    availableFoods: List<AutoEatFoodOption>,
    autoEatPriority: List<String>,
    onSetAutoEat: (Boolean, String?) -> Unit,
    onSetAutoEatThreshold: (Float) -> Unit,
    onSetUseBestAutoEat: (Boolean) -> Unit,
    onSetAutoEatPriority: (List<String>) -> Unit
) {
    val optionsById = remember(availableFoods) { availableFoods.associateBy { it.id } }

    fun optionFor(id: String): AutoEatFoodOption {
        return optionsById[id] ?: AutoEatFoodOption(
            id = id,
            name = formatFoodName(id),
            quantity = availableFoods.firstOrNull { it.id == id }?.quantity ?: 0,
            healAmount = CombatSystem.healingAmountForFood(id),
            value = 0
        )
    }

    val normalizedPriority = remember(autoEatPriority, availableFoods) {
        val unique = LinkedHashSet<String>()
        autoEatPriority.forEach { id ->
            if (id.isNotBlank()) {
                unique.add(id)
            }
        }
        if (unique.isNotEmpty()) {
            unique.toList()
        } else {
            val owned = availableFoods.filter { it.quantity > 0 }.sortedByDescending { it.healAmount }
            when {
                owned.isNotEmpty() -> owned.map { it.id }
                availableFoods.isNotEmpty() -> availableFoods.sortedByDescending { it.healAmount }.map { it.id }
                else -> AutoEatPrefs.defaultPriority()
            }
        }
    }

    var localPriority by remember { mutableStateOf(normalizedPriority) }
    LaunchedEffect(normalizedPriority) {
        localPriority = normalizedPriority
    }

    val selectionOptions = remember(localPriority, availableFoods) {
        val seen = LinkedHashSet<String>()
        val combined = mutableListOf<AutoEatFoodOption>()
        localPriority.forEach { id ->
            if (seen.add(id)) {
                combined += optionFor(id)
            }
        }
        availableFoods.forEach { option ->
            if (seen.add(option.id)) {
                combined += option
            }
        }
        AutoEatPrefs.defaultPriority().forEach { id ->
            if (seen.add(id)) {
                combined += optionFor(id)
            }
        }
        combined
    }

    val bestOwnedFoodId = localPriority.firstOrNull { optionFor(it).owned }
    val selectedFoodId = combatStats?.autoEatFoodId
        ?: bestOwnedFoodId
        ?: localPriority.firstOrNull()
        ?: availableFoods.firstOrNull()?.id
    val selectedOption = selectedFoodId?.let { optionFor(it) }

    val threshold = combatStats?.autoEatThreshold ?: 0.5f
    val isAutoEatEnabled = combatStats?.autoEatEnabled ?: false
    val useBest = combatStats?.useBestAutoEat ?: false

    var foodMenuExpanded by remember { mutableStateOf(false) }
    var addMenuExpanded by remember { mutableStateOf(false) }

    val addCandidates = selectionOptions.filter { it.id !in localPriority }
    val canSave = localPriority.isNotEmpty()

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
                    checked = isAutoEatEnabled,
                    onCheckedChange = { enabled ->
                        val fallbackId = if (enabled) {
                            selectedFoodId ?: selectionOptions.firstOrNull()?.id
                        } else {
                            null
                        }
                        onSetAutoEat(enabled, fallbackId)
                    }
                )
            }

            if (isAutoEatEnabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Use best available food:")
                    Switch(
                        checked = useBest,
                        onCheckedChange = { onSetUseBestAutoEat(it) },
                        enabled = localPriority.isNotEmpty()
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = foodMenuExpanded,
                    onExpandedChange = { foodMenuExpanded = !foodMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedOption?.name ?: "Select Food",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Manual fallback food") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = foodMenuExpanded)
                        },
                        supportingText = {
                            selectedOption?.let { option ->
                                val qtyText = if (option.quantity > 0) {
                                    "${option.quantity} in bank"
                                } else {
                                    "Not in bank"
                                }
                                Text("$qtyText • Restores ${option.healAmount} HP")
                            }
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = foodMenuExpanded,
                        onDismissRequest = { foodMenuExpanded = false }
                    ) {
                        selectionOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        buildString {
                                            append(option.name)
                                            if (option.quantity > 0) {
                                                append(" (${option.quantity})")
                                            }
                                        }
                                    )
                                },
                                onClick = {
                                    foodMenuExpanded = false
                                    if (option.id !in localPriority) {
                                        localPriority = (localPriority + option.id).distinct()
                                    }
                                    onSetAutoEat(true, option.id)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Auto-eat threshold: ${(threshold * 100).toInt()}%")
                Slider(
                    value = threshold,
                    onValueChange = { onSetAutoEatThreshold(it) },
                    valueRange = 0.1f..0.9f,
                    steps = 7,
                    enabled = combatStats != null
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Detected cooked food:", style = MaterialTheme.typography.bodyMedium)
                if (availableFoods.isEmpty()) {
                    Text(
                        text = "Cook food in the Cooking skill to stock your bank.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                        availableFoods.sortedWith(
                            compareByDescending<AutoEatFoodOption> { it.owned }
                                .thenByDescending { it.healAmount }
                                .thenBy { it.name }
                        ).take(6).forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Fastfood, contentDescription = null, tint = if (option.owned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(option.name, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = "${if (option.owned) option.quantity else 0} in bank • Restores ${option.healAmount} HP",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Food Priority:", style = MaterialTheme.typography.bodyMedium)
                if (localPriority.isEmpty()) {
                    Text(
                        text = "Add at least one cooked food below to enable the priority list.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                        localPriority.forEachIndexed { index, id ->
                            val option = optionFor(id)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(option.name, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = "${if (option.owned) option.quantity else 0} in bank • Restores ${option.healAmount} HP",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = {
                                        if (index > 0) {
                                            val list = localPriority.toMutableList()
                                            val removed = list.removeAt(index)
                                            list.add(index - 1, removed)
                                            localPriority = list
                                        }
                                    }, enabled = index > 0) {
                                        Icon(Icons.Default.ArrowUpward, contentDescription = "Move up")
                                    }
                                    IconButton(onClick = {
                                        if (index < localPriority.lastIndex) {
                                            val list = localPriority.toMutableList()
                                            val removed = list.removeAt(index)
                                            list.add(index + 1, removed)
                                            localPriority = list
                                        }
                                    }, enabled = index < localPriority.lastIndex) {
                                        Icon(Icons.Default.ArrowDownward, contentDescription = "Move down")
                                    }
                                    IconButton(onClick = {
                                        val list = localPriority.toMutableList()
                                        list.removeAt(index)
                                        localPriority = list
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove")
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        OutlinedButton(onClick = { addMenuExpanded = true }, enabled = addCandidates.isNotEmpty()) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Food")
                        }
                        DropdownMenu(
                            expanded = addMenuExpanded,
                            onDismissRequest = { addMenuExpanded = false }
                        ) {
                            addCandidates.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            buildString {
                                                append(option.name)
                                                if (option.quantity > 0) {
                                                    append(" (${option.quantity})")
                                                }
                                            }
                                        )
                                    },
                                    onClick = {
                                        addMenuExpanded = false
                                        localPriority = (localPriority + option.id).distinct()
                                    }
                                )
                            }
                        }
                    }

                    OutlinedButton(onClick = {
                        val ownedFirst = availableFoods.filter { it.quantity > 0 }
                            .sortedByDescending { it.healAmount }
                            .map { it.id }
                        localPriority = if (ownedFirst.isNotEmpty()) {
                            ownedFirst
                        } else {
                            AutoEatPrefs.defaultPriority()
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Use Best Owned")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val sanitized = localPriority.filter { it.isNotBlank() }
                            onSetAutoEatPriority(sanitized)
                            if (combatStats?.useBestAutoEat == true) {
                                val best = sanitized.firstOrNull { optionFor(it).owned }
                                if (best != null) {
                                    onSetAutoEat(true, best)
                                }
                            }
                        },
                        enabled = canSave
                    ) {
                        Text("Save Priority")
                    }
                    OutlinedButton(onClick = { localPriority = AutoEatPrefs.defaultPriority() }) {
                        Text("Reset to Default")
                    }
                }
            }
        }
    }
}

@Composable
fun EnemySelectionCard(
    combatStats: CombatStats?,
    enemies: List<Enemy>,
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
            
            enemies.forEach { enemy ->
                val canFight = playerLevel >= enemy.combatLevelRequired
                
                OutlinedButton(
                    onClick = { onStartCombat(enemy.id) },
                    enabled = canFight,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        val painter = Sprites.painterForEnemyId(enemy.id)
                        Image(painter = painter, contentDescription = enemy.name, modifier = Modifier.size(28.dp))
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

private fun formatAttackSpeed(speedMs: Long): String {
    val seconds = speedMs / 1000.0
    return String.format(Locale.getDefault(), "%.1fs", seconds)
}

private fun formatAccuracy(accuracy: Float): String {
    return String.format(Locale.getDefault(), "%.0f%% hit", accuracy * 100f)
}

private fun formatFoodName(id: String): String {
    return id.split('_').joinToString(" ") { part ->
        part.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
        }
    }
}
