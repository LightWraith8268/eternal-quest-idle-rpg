package com.eternalquest.util

import android.content.Context

data class SkillDef(
    val name: String,
    val displayName: String,
    val description: String,
    val baseXpRate: Long,
    val category: String
)

object SkillsCatalog {
    @Volatile private var loaded = false
    @Volatile private var skills: List<SkillDef> = emptyList()

    fun load(context: Context) {
        if (loaded) return
        synchronized(this) {
            if (loaded) return
            try {
                val json = context.assets.open("config/skills.json").bufferedReader().use { it.readText() }
                val arr = org.json.JSONObject(json).optJSONArray("skills")
                val out = mutableListOf<SkillDef>()
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        out += SkillDef(
                            name = o.getString("name"),
                            displayName = o.optString("displayName", o.getString("name")),
                            description = o.optString("description", ""),
                            baseXpRate = o.optLong("baseXpRate", 10L),
                            category = o.optString("category", "GATHERING")
                        )
                    }
                }
                // uniqueness by name
                val seen = mutableSetOf<String>()
                for (s in out) { if (!seen.add(s.name)) throw IllegalStateException("Duplicate skill name detected: ${s.name}") }
                skills = out
            } catch (_: Exception) {
                // fallback to built-in
                skills = com.eternalquest.data.entities.Skills.ALL.map {
                    SkillDef(it.name, it.displayName, it.description, it.baseXpRate, it.category.name)
                }
            }
            loaded = true
        }
    }

    fun all(): List<SkillDef> = skills
}
