package com.eternalquest.util

import android.content.Context
import org.json.JSONObject

data class UpgradeDef(
    val id: String,
    val baseCost: Long? = null,
    val maxLevel: Int? = null,
    val costMultiplier: Float? = null
)

object UpgradeConfig {
    private const val PATH = "config/upgrades.json"
    @Volatile private var cache: Map<String, UpgradeDef> = emptyMap()

    fun load(context: Context) {
        try {
            val json = context.assets.open(PATH).bufferedReader().use { it.readText() }
            val root = JSONObject(json)
            val arr = root.getJSONArray("upgrades")
            val map = mutableMapOf<String, UpgradeDef>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val def = UpgradeDef(
                    id = o.getString("id"),
                    baseCost = if (o.has("baseCost")) o.getLong("baseCost") else null,
                    maxLevel = if (o.has("maxLevel")) o.getInt("maxLevel") else null,
                    costMultiplier = if (o.has("costMultiplier")) o.getDouble("costMultiplier").toFloat() else null
                )
                map[def.id] = def
            }
            cache = map
        } catch (e: Exception) {
            cache = emptyMap()
        }
    }

    fun get(id: String): UpgradeDef? = cache[id]
}

