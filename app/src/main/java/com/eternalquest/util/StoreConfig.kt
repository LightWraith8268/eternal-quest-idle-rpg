package com.eternalquest.util

import android.content.Context
import com.eternalquest.data.entities.UpgradeCategory
import org.json.JSONObject

object StoreConfig {
    private const val PATH = "config/store.json"
    @Volatile private var displayNames: Map<String, String> = emptyMap()
    @Volatile private var order: List<String> = emptyList()
    @Volatile private var icons: Map<String, String> = emptyMap()

    fun load(context: Context) {
        try {
            val json = context.assets.open(PATH).bufferedReader().use { it.readText() }
            val root = JSONObject(json)
            val names = root.optJSONObject("categories") ?: JSONObject()
            val map = mutableMapOf<String, String>()
            names.keys().forEach { k -> map[k] = names.optString(k, k) }
            displayNames = map
            val iobj = root.optJSONObject("icons") ?: JSONObject()
            val imap = mutableMapOf<String, String>()
            iobj.keys().forEach { k -> imap[k] = iobj.optString(k, "") }
            icons = imap
            val arr = root.optJSONArray("order")
            order = if (arr != null) List(arr.length()) { i -> arr.getString(i) } else emptyList()
        } catch (e: Exception) {
            displayNames = emptyMap(); order = emptyList(); icons = emptyMap()
        }
    }

    fun getDisplayName(category: UpgradeCategory): String {
        return displayNames[category.name] ?: when (category) {
            UpgradeCategory.BANK_EXPANSION -> "Bank"
            UpgradeCategory.ACTIVITY_QUEUE -> "Queue"
            UpgradeCategory.OFFLINE_TIME -> "Offline"
            UpgradeCategory.CONVENIENCE -> "Tools"
            UpgradeCategory.COSMETIC -> "Themes"
        }
    }

    fun getOrderedCategories(): List<UpgradeCategory> {
        if (order.isEmpty()) return UpgradeCategory.values().toList()
        val map = UpgradeCategory.values().associateBy { it.name }
        val ordered = order.mapNotNull { map[it] }
        val remaining = UpgradeCategory.values().filter { it.name !in order }
        return ordered + remaining
    }

    fun getIconName(category: UpgradeCategory): String? = icons[category.name]
}
