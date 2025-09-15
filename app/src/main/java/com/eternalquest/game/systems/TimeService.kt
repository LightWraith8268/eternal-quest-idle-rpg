package com.eternalquest.game.systems

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

class TimeService {
    companion object {
        const val TICK_INTERVAL_MS = 100L
        const val OFFLINE_RATE_MODIFIER = 0.33f
        const val MAX_OFFLINE_DAYS = 7
        const val MAX_OFFLINE_MS = MAX_OFFLINE_DAYS * 24 * 60 * 60 * 1000L
    }
    
    fun createGameTick(): Flow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(TICK_INTERVAL_MS)
        }
    }
    
    fun calculateOfflineProgress(
        lastActiveTime: Long,
        offlineRateModifier: Float = OFFLINE_RATE_MODIFIER,
        currentTime: Long = System.currentTimeMillis()
    ): OfflineProgress {
        val offlineTimeMs = currentTime - lastActiveTime
        val cappedOfflineMs = minOf(offlineTimeMs, MAX_OFFLINE_MS)
        val effectiveProgressMs = (cappedOfflineMs * offlineRateModifier).toLong()
        
        return OfflineProgress(
            totalOfflineMs = offlineTimeMs,
            cappedOfflineMs = cappedOfflineMs,
            effectiveProgressMs = effectiveProgressMs,
            wasOfflineCapped = offlineTimeMs > MAX_OFFLINE_MS
        )
    }
    
    fun formatDuration(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            days > 0 -> "${days}d ${hours % 24}h ${minutes % 60}m"
            hours > 0 -> "${hours}h ${minutes % 60}m ${seconds % 60}s"
            minutes > 0 -> "${minutes}m ${seconds % 60}s"
            else -> "${seconds}s"
        }
    }
}

data class OfflineProgress(
    val totalOfflineMs: Long,
    val cappedOfflineMs: Long,
    val effectiveProgressMs: Long,
    val wasOfflineCapped: Boolean
)