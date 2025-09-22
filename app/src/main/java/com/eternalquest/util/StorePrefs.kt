package com.eternalquest.util

import android.content.Context

object StorePrefs {
    private const val PREFS = "eq_store_prefs"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getSeen(context: Context, profileId: Int): MutableSet<String> {
        return prefs(context).getStringSet("store_seen_$profileId", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    }

    fun addSeen(context: Context, profileId: Int, id: String) {
        val p = prefs(context)
        val set = getSeen(context, profileId)
        set.add(id)
        p.edit().putStringSet("store_seen_$profileId", set).apply()
    }

    fun getSortMode(context: Context, profileId: Int): String =
        prefs(context).getString("store_sort_$profileId", "Name") ?: "Name"

    fun setSortMode(context: Context, profileId: Int, mode: String) {
        prefs(context).edit().putString("store_sort_$profileId", mode).apply()
    }

    fun clearProfile(context: Context, profileId: Int) {
        prefs(context).edit()
            .remove("store_seen_$profileId")
            .remove("store_sort_$profileId")
            .apply()
    }
}

