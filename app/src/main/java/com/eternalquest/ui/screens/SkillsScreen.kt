package com.eternalquest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eternalquest.data.entities.*
import com.eternalquest.game.systems.XpSystem
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.eternalquest.ui.util.Sprites

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsScreen(
    skills: List<Skill>,
    currentActivity: String?,
    activityProgress: Float,
    onStartActivity: (String, String) -> Unit,
    onStopActivity: () -> Unit,
    onPrestigeSkill: (String) -> Unit
) {
    var pendingPrestige by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(skills) { skill ->
            SkillCard(
                skill = skill,
                allSkills = skills,
                isActive = currentActivity != null && Activities.ALL.any { 
                    it.skill == skill.name && currentActivity.contains(it.id) 
                },
                activityProgress = if (currentActivity != null && Activities.ALL.any { 
                    it.skill == skill.name && currentActivity.contains(it.id) 
                }) activityProgress else 0f,
                onStartActivity = onStartActivity,
                onStopActivity = onStopActivity,
                onPrestigeSkill = { pendingPrestige = it }
            )
        }
    }

    if (pendingPrestige != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { pendingPrestige = null },
            title = { Text("Confirm Prestige") },
            text = { Text("Prestige ${pendingPrestige} to level 1 for +5% XP per prestige? This resets the skill's level and XP.") },
            confirmButton = {
                Button(onClick = {
                    onPrestigeSkill(pendingPrestige!!)
                    pendingPrestige = null
                }) { Text("Prestige") }
            },
            dismissButton = {
                OutlinedButton(onClick = { pendingPrestige = null }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillCard(
    skill: Skill,
    allSkills: List<Skill>,
    isActive: Boolean,
    activityProgress: Float,
    onStartActivity: (String, String) -> Unit,
    onStopActivity: () -> Unit,
    onPrestigeSkill: (String) -> Unit
) {
    val skillType = Skills.ALL.find { it.name == skill.name }
    val (availableActivities, lockedActivities) = Activities.ALL
        .filter { it.skill == skill.name }
        .partition { activity ->
            activity.requirements.all { (reqSkill, reqLevel) ->
                val s = allSkills.find { it.name == reqSkill }
                s != null && s.level >= reqLevel
            }
        }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer 
                            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val sprite = Sprites.forSkillName(skill.name)
                    Image(
                        painter = painterResource(id = sprite.resId),
                        contentDescription = skillType?.displayName ?: skill.name,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = skillType?.displayName ?: skill.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (skill.prestigeCount > 0) {
                        AssistChip(onClick = {}, enabled = false, label = { Text("Prestige ${skill.prestigeCount}") })
                    }
                    Text(
                        text = "Level ${skill.level}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // XP Progress Bar
            val progress = XpSystem.getProgressToNextLevel(skill.experience)
            val nextLevelXp = XpSystem.getXpForNextLevel(skill.level)
            val currentLevelXp = XpSystem.getXpForLevel(skill.level)
            val xpInLevel = skill.experience - currentLevelXp
            val xpNeededForLevel = nextLevelXp - currentLevelXp
            
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "XP: $xpInLevel / $xpNeededForLevel",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (skill.prestigeCount > 0) {
                        Text(
                            text = "Prestige: ${skill.prestigeCount}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
            
            if (skill.isUnlocked) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Activity Progress (if active)
                if (isActive) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Activity Progress",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${(activityProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        LinearProgressIndicator(
                            progress = activityProgress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        
                        Button(
                            onClick = onStopActivity,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Stop Activity")
                        }
                    }
                } else {
                    // Available Activities
                    Text(
                        text = "Available Activities:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    availableActivities.forEach { activity ->
                        OutlinedButton(
                            onClick = { onStartActivity(skill.name, activity.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            val rewardIcon = activity.itemRewards.firstOrNull()?.itemId
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                val painter = if (rewardIcon != null) Sprites.painterForItemId(rewardIcon) else painterResource(id = Sprites.forSkillName(activity.skill).resId)
                                Image(
                                    painter = painter,
                                    contentDescription = activity.name,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activity.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "XP: ${activity.xpReward} | ${activity.baseTimeMs/1000}s",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    if (activity.itemCosts.isNotEmpty()) {
                                        val costs = activity.itemCosts.joinToString { c -> "${c.quantity}x ${c.itemId}" }
                                        Text(
                                            text = "Materials: $costs",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                    if (activity.requirements.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            activity.requirements.forEach { (reqSkill, reqLevel) ->
                                                AssistChip(
                                                    onClick = {},
                                                    label = { Text("$reqSkill $reqLevel") },
                                                    enabled = true
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (lockedActivities.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Locked:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        lockedActivities.forEach { activity ->
                            val reqText = activity.requirements.entries.joinToString { (reqSkill, reqLevel) ->
                                "${reqSkill}: ${reqLevel}"
                            }
                            OutlinedButton(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                val rewardIcon = activity.itemRewards.firstOrNull()?.itemId
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    val painter = if (rewardIcon != null) Sprites.painterForItemId(rewardIcon) else painterResource(id = Sprites.forSkillName(activity.skill).resId)
                                    Image(
                                        painter = painter,
                                        contentDescription = activity.name,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = activity.name,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            activity.requirements.forEach { (reqSkill, reqLevel) ->
                                                AssistChip(
                                                    onClick = {},
                                                    label = { Text("$reqSkill $reqLevel") },
                                                    enabled = false
                                                )
                                            }
                                        }
                                        if (activity.itemCosts.isNotEmpty()) {
                                            val costs = activity.itemCosts.joinToString { c -> "${c.quantity}x ${c.itemId}" }
                                            Text(
                                                text = "Materials: $costs",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Prestige action when at max level
                    if (skill.level >= XpSystem.MAX_LEVEL) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onPrestigeSkill(skill.name) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Text("Prestige: Reset to 1, +5% XP")
                        }
                    }
                }
            } else {
                Text(
                    text = "Skill locked",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
