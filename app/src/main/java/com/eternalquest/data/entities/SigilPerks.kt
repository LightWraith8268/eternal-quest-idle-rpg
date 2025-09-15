package com.eternalquest.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sigil_perks")
data class SigilPerks(
    @PrimaryKey val playerId: Int = 1,
    val xpBonusLevel: Int = 0, // 0..5, each level +2% XP
    val speedBonusLevel: Int = 0, // 0..5, each level reduces time by 2%
    val lootBonusLevel: Int = 0 // 0..5, each level +2% reward chance
)
