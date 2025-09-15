package com.eternalquest.util

import android.content.Context
import com.eternalquest.data.entities.Item

object ItemCatalog {
    @Volatile private var loaded: Boolean = false
    @Volatile private var itemsById: Map<String, Item> = emptyMap()

    fun load(context: Context) {
        if (loaded) return
        synchronized(this) {
            if (loaded) return
            val merged = mutableListOf<Item>()
            // Prefer folder-based items
            val dir = "config/items"
            val files = try { context.assets.list(dir) } catch (_: Exception) { null }
            if (files != null && files.isNotEmpty()) {
                for (name in files) {
                    if (!name.endsWith(".json")) continue
                    val json = context.assets.open("$dir/$name").bufferedReader().use { it.readText() }
                    merged += parseItems(json)
                }
            } else {
                // Fallback single file
                try {
                    val json = context.assets.open("config/items.json").bufferedReader().use { it.readText() }
                    merged += parseItems(json)
                } catch (_: Exception) {
                    // no JSON present
                }
            }
            val list = if (merged.isNotEmpty()) merged else com.eternalquest.data.entities.GameItems.ALL
            // Enforce uniqueness
            val seen = mutableSetOf<String>()
            for (it in list) {
                if (!seen.add(it.id)) {
                    throw IllegalStateException("Duplicate item id detected: ${it.id}")
                }
            }
            itemsById = list.associateBy { it.id }
            loaded = true
        }
    }

    private fun parseItems(json: String): List<Item> {
        val arr = org.json.JSONObject(json).optJSONArray("items")
        val out = mutableListOf<Item>()
        if (arr != null) {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val id = o.getString("id")
                val name = o.optString("name", id)
                val desc = o.optString("description", "")
                val category = try {
                    com.eternalquest.data.entities.ItemCategory.valueOf(o.optString("category", "MISC"))
                } catch (_: Exception) { com.eternalquest.data.entities.ItemCategory.MISC }
                val stack = o.optInt("stackSize", 1)
                val value = o.optInt("value", 0)
                val icon = if (o.has("iconResource")) o.optString("iconResource", null) else null
                out.add(Item(id, name, desc, category, stack, value, icon))
            }
        }
        return out
    }

    fun get(id: String): Item? = itemsById[id]
    fun all(): List<Item> = itemsById.values.toList()
}
