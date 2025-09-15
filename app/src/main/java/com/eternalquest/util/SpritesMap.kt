package com.eternalquest.util

import android.content.Context

object SpritesMap {
    @Volatile private var loaded = false
    @Volatile private var items: Map<String, String> = emptyMap()
    @Volatile private var enemies: Map<String, String> = emptyMap()
    @Volatile private var areas: Map<String, String> = emptyMap()
    @Volatile private var skills: Map<String, String> = emptyMap()

    fun load(context: Context) {
        if (loaded) return
        synchronized(this) {
            if (loaded) return
            try {
                val json = context.assets.open("config/sprites_map.json").bufferedReader().use { it.readText() }
                val root = org.json.JSONObject(json)
                items = root.optJSONObject("items")?.let { o -> toMap(o) } ?: emptyMap()
                enemies = root.optJSONObject("enemies")?.let { o -> toMap(o) } ?: emptyMap()
                areas = root.optJSONObject("areas")?.let { o -> toMap(o) } ?: emptyMap()
                skills = root.optJSONObject("skills")?.let { o -> toMap(o) } ?: emptyMap()
            } catch (_: Exception) {
                items = emptyMap(); enemies = emptyMap(); areas = emptyMap(); skills = emptyMap()
            }
            loaded = true
        }
    }

    private fun toMap(o: org.json.JSONObject): Map<String, String> {
        val m = mutableMapOf<String, String>()
        val it = o.keys()
        while (it.hasNext()) {
            val k = it.next()
            m[k] = o.optString(k, "")
        }
        return m
    }

    fun resolveDrawableId(context: Context, type: String, id: String): Int? {
        val name = when (type) {
            "item" -> items[id]
            "enemy" -> enemies[id]
            "area" -> areas[id]
            "skill" -> skills[id]
            else -> null
        } ?: return null
        val pkg = context.packageName
        val resId = context.resources.getIdentifier(name, "drawable", pkg)
        return if (resId != 0) resId else null
    }
}

