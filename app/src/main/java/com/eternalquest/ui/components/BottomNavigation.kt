package com.eternalquest.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.eternalquest.ui.viewmodels.GameTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameBottomNavigation(
    selectedTab: GameTab,
    onTabSelected: (GameTab) -> Unit
) {
    NavigationBar {
        GameTab.entries.forEach { tab ->
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title
                    )
                },
                label = { Text(tab.title) },
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

private val GameTab.icon: ImageVector
    get() = when (this) {
        GameTab.SKILLS -> Icons.Default.Star
        GameTab.BANK -> Icons.Default.AccountBox
        GameTab.COMBAT -> Icons.Default.LocalFireDepartment
        GameTab.CHARACTER -> Icons.Default.Build
        GameTab.STORE -> Icons.Default.ShoppingCart
        GameTab.SETTINGS -> Icons.Default.Settings
    }

private val GameTab.title: String
    get() = when (this) {
        GameTab.SKILLS -> "Skills"
        GameTab.BANK -> "Bank"
        GameTab.COMBAT -> "Combat"
        GameTab.CHARACTER -> "Character"
        GameTab.STORE -> "Store"
        GameTab.SETTINGS -> "Settings"
    }