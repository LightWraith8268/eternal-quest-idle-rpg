package com.eternalquest.util

import android.content.Context

object ProfileManager {
    private const val PREFS = "eq_prefs"
    private const val KEY_PROFILE = "current_profile_id"

    fun getCurrentProfileId(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_PROFILE, 1).coerceIn(1, 3)
    }

    fun setCurrentProfileId(context: Context, id: Int) {
        val safe = id.coerceIn(1, 3)
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_PROFILE, safe).apply()
    }
}

