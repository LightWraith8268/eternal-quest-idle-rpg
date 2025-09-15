package com.eternalquest.util

import android.content.Context
import com.eternalquest.data.entities.LootDrop

object LootTablesCatalog {
    @Volatile private var loaded = false
    @Volatile private var byEnemy: Map<String, List<LootDrop>> = emptyMap()

    fun load(context: Context) {
        if (loaded) return
        synchronized(this) {
            if (loaded) return
            try {
                val json = context.assets.open("config/loot_tables.json").bufferedReader().use { it.readText() }
                val root = org.json.JSONObject(json)
                val map = mutableMapOf<String, List<LootDrop>>()
                val loot = root.optJSONObject("loot") ?: org.json.JSONObject()
                val keys = loot.keys()
                while (keys.hasNext()) {
                    val id = keys.next()
                    val arr = loot.optJSONArray(id)
                    if (arr != null) {
                        val drops = mutableListOf<LootDrop>()
                        for (i in 0 until arr.length()) {
                            val o = arr.getJSONObject(i)
                            drops += LootDrop(
                                itemId = o.getString("itemId"),
                                quantity = o.optInt("quantity", 1),
                                chance = o.optDouble("chance", 1.0).toFloat(),
                                isRare = o.optBoolean("isRare", false)
                            )
                        }
                        map[id] = drops
                    }
                }
                byEnemy = map
            } catch (_: Exception) {
                byEnemy = emptyMap()
            }
            loaded = true
        }
    }

    fun getLootTable(enemyId: String): List<LootDrop>? = byEnemy[enemyId]
}

