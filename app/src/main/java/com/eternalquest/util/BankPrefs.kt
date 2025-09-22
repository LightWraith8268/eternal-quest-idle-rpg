package com.eternalquest.util

import android.content.Context

object BankPrefs {
    private const val PREFS = "eq_bank_prefs"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getQuery(context: Context, profileId: Int): String =
        prefs(context).getString("bank_query_$profileId", "") ?: ""

    fun setQuery(context: Context, profileId: Int, value: String) {
        prefs(context).edit().putString("bank_query_$profileId", value).apply()
    }

    fun getSortMode(context: Context, profileId: Int): String =
        prefs(context).getString("bank_sort_$profileId", "Name") ?: "Name"

    fun setSortMode(context: Context, profileId: Int, value: String) {
        prefs(context).edit().putString("bank_sort_$profileId", value).apply()
    }

    fun getCategory(context: Context, profileId: Int): String =
        prefs(context).getString("bank_category_$profileId", "All") ?: "All"

    fun setCategory(context: Context, profileId: Int, value: String) {
        prefs(context).edit().putString("bank_category_$profileId", value).apply()
    }

    fun getShowEmpty(context: Context, profileId: Int): Boolean =
        prefs(context).getBoolean("bank_show_empty_$profileId", true)

    fun setShowEmpty(context: Context, profileId: Int, value: Boolean) {
        prefs(context).edit().putBoolean("bank_show_empty_$profileId", value).apply()
    }

    fun getSelectedTab(context: Context, profileId: Int): Int =
        prefs(context).getInt("bank_tab_$profileId", 0)

    fun setSelectedTab(context: Context, profileId: Int, tab: Int) {
        prefs(context).edit().putInt("bank_tab_$profileId", tab).apply()
    }

    fun clearProfile(context: Context, profileId: Int) {
        prefs(context).edit().apply {
            remove("bank_query_$profileId")
            remove("bank_sort_$profileId")
            remove("bank_category_$profileId")
            remove("bank_show_empty_$profileId")
            remove("bank_tab_$profileId")
        }.apply()
    }
}
