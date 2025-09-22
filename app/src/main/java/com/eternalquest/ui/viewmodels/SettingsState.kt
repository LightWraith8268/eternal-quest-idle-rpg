package com.eternalquest.ui.viewmodels

/**
 * Represents configurable presentation and gameplay toggles that live outside of the
 * database-backed profile data. These values are persisted via [com.eternalquest.util.SettingsPrefs].
 */
data class SettingsState(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val useDynamicColor: Boolean = true,
    val showCombatLog: Boolean = true
)

/**
 * App-wide theme modes exposed to the settings UI.
 */
enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK;

    val displayName: String
        get() = when (this) {
            SYSTEM -> "System default"
            LIGHT -> "Light"
            DARK -> "Dark"
        }

    val description: String
        get() = when (this) {
            SYSTEM -> "Follows your Android system theme settings."
            LIGHT -> "Uses the bright palette regardless of system settings."
            DARK -> "Uses the dark palette regardless of system settings."
        }
}
