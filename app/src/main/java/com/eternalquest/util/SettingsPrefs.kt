package com.eternalquest.util

import android.content.Context
import com.eternalquest.ui.viewmodels.ThemePreference

/**
 * Lightweight wrapper around [android.content.SharedPreferences] for storing UI settings
 * that apply to the whole application rather than an individual save profile.
 */
object SettingsPrefs {
    private const val PREFS = "eq_settings_prefs"
    private const val KEY_THEME = "theme"
    private const val KEY_DYNAMIC_COLOR = "dynamic_color"
    private const val KEY_SHOW_COMBAT_LOG = "show_combat_log"

    fun getTheme(context: Context): ThemePreference {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val stored = prefs.getString(KEY_THEME, ThemePreference.SYSTEM.name)
        return stored?.let { value ->
            runCatching { ThemePreference.valueOf(value) }.getOrDefault(ThemePreference.SYSTEM)
        } ?: ThemePreference.SYSTEM
    }

    fun setTheme(context: Context, theme: ThemePreference) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME, theme.name).apply()
    }

    fun getUseDynamicColor(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DYNAMIC_COLOR, true)
    }

    fun setUseDynamicColor(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
    }

    fun getShowCombatLog(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SHOW_COMBAT_LOG, true)
    }

    fun setShowCombatLog(context: Context, show: Boolean) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SHOW_COMBAT_LOG, show).apply()
    }
}
