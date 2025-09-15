package com.eternalquest.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey val id: Int = 1,
    val name: String = "Adventurer",
    val createdAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis(),
    val totalPlayTime: Long = 0L,
    val currentSkill: String? = null,
    val currentActivity: String? = null,
    val activityStartTime: Long? = null,
    val activityProgress: Float = 0f,
    val ascensionCount: Int = 0,
    val etherealSigils: Int = 0
)
