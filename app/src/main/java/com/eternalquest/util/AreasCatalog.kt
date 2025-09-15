package com.eternalquest.util

import android.content.Context

data class AreaDef(
    val id: String,
    val name: String,
    val type: String,
    val minCombatLevel: Int,
    val maxCombatLevel: Int,
    val description: String
)

object AreasCatalog {
    @Volatile private var loaded = false
    @Volatile private var areas: List<AreaDef> = emptyList()

    fun load(context: Context) {
        if (loaded) return
        synchronized(this) {
            if (loaded) return
            try {
                val json = context.assets.open("config/areas.json").bufferedReader().use { it.readText() }
                val arr = org.json.JSONObject(json).optJSONArray("areas")
                val out = mutableListOf<AreaDef>()
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        out += AreaDef(
                            id = o.getString("id"),
                            name = o.optString("name", o.getString("id")),
                            type = o.optString("type", "OVERWORLD"),
                            minCombatLevel = o.optInt("minCombatLevel", 1),
                            maxCombatLevel = o.optInt("maxCombatLevel", 100),
                            description = o.optString("description", "")
                        )
                    }
                }
                val seen = mutableSetOf<String>()
                for (a in out) { if (!seen.add(a.id)) throw IllegalStateException("Duplicate area id detected: ${a.id}") }
                areas = out
            } catch (_: Exception) {
                // fallback to built-in
                areas = com.eternalquest.data.entities.Areas.ALL_AREAS.map { a ->
                    AreaDef(a.id, a.name, a.type.name, a.minCombatLevel, a.maxCombatLevel, a.description)
                }
            }
            loaded = true
        }
    }

    fun all(): List<AreaDef> = areas
}
