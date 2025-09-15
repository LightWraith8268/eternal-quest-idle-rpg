package com.eternalquest.util

import android.content.Context
import org.json.JSONObject

data class PerkDef(
    val id: String,
    val name: String,
    val maxLevel: Int,
    val costPerLevel: Int,
    val percentPerLevel: Int
)

object PerkConfig {
    private const val PATH = "config/perks.json"
    private val defaults = mapOf(
        "xp" to PerkDef("xp", "XP Bonus", 5, 1, 2),
        "speed" to PerkDef("speed", "Speed Bonus", 5, 1, 2),
        "loot" to PerkDef("loot", "Loot Chance", 5, 1, 2)
    )

    @Volatile
    private var cache: Map<String, PerkDef> = defaults

    fun load(context: Context) {
        try {
            val json = context.assets.open(PATH).bufferedReader().use { it.readText() }
            val root = JSONObject(json)
            val arr = root.getJSONArray("perks")
            val map = mutableMapOf<String, PerkDef>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val def = PerkDef(
                    id = o.getString("id"),
                    name = o.optString("name", o.getString("id")),
                    maxLevel = o.optInt("maxLevel", 5),
                    costPerLevel = o.optInt("costPerLevel", 1),
                    percentPerLevel = o.optInt("percentPerLevel", 2)
                )
                map[def.id] = def
            }
            cache = defaults + map
        } catch (e: Exception) {
            cache = defaults
        }
    }

    fun get(id: String): PerkDef = cache[id] ?: defaults[id]!!
}

