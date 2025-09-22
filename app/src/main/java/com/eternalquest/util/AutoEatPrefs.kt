package com.eternalquest.util

import android.content.Context

object AutoEatPrefs {
    private const val PREFS = "eq_auto_eat_prefs"
    private const val KEY_PREFIX = "priority_"

    private val DEFAULT = listOf("cooked_swordfish", "cooked_tuna", "cooked_salmon", "cooked_trout")

    fun getPriority(context: Context, profileId: Int): List<String> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val csv = prefs.getString(KEY_PREFIX + profileId, null)
        return csv?.split(',')?.filter { it.isNotBlank() } ?: DEFAULT
    }

    fun setPriority(context: Context, profileId: Int, priority: List<String>) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val csv = priority.joinToString(",")
        prefs.edit().putString(KEY_PREFIX + profileId, csv).apply()
    }

    fun clearProfile(context: Context, profileId: Int) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_PREFIX + profileId).apply()
    }

    fun defaultPriority(): List<String> = DEFAULT
}

