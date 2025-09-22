package com.eternalquest.util

import android.content.Context
import com.eternalquest.data.entities.SkillCategory
import com.eternalquest.data.entities.SkillType
import com.eternalquest.data.entities.Skills
import java.util.Locale

object SkillsCatalog {
    @Volatile private var loaded = false
    @Volatile private var skills: Map<String, SkillType> = emptyMap()

    fun load(context: Context) {
        if (loaded) return
        synchronized(this) {
            if (loaded) return
            val map = linkedMapOf<String, SkillType>()
            // Seed with built-in definitions so assets can override/extend them.
            for (skill in Skills.ALL) {
                map[skill.name] = skill
            }
            try {
                val json = context.assets.open("config/skills.json").bufferedReader().use { it.readText() }
                val arr = org.json.JSONObject(json).optJSONArray("skills")
                if (arr != null) {
                    val seen = mutableSetOf<String>()
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        val name = o.getString("name")
                        if (!seen.add(name)) {
                            throw IllegalStateException("Duplicate skill name detected: $name")
                        }
                        val displayName = o.optString("displayName", name)
                        val description = o.optString("description", "")
                        val baseXpRate = o.optLong("baseXpRate", 10L)
                        val categoryStr = o.optString("category", "GATHERING")
                        val category = try {
                            SkillCategory.valueOf(categoryStr.trim().uppercase(Locale.getDefault()))
                        } catch (_: Exception) {
                            SkillCategory.GATHERING
                        }
                        map[name] = SkillType(name, displayName, description, baseXpRate, category)
                    }
                }
            } catch (_: Exception) {
                // Keep seed data if assets are unavailable or malformed.
            }
            skills = map
            loaded = true
        }
    }

    fun all(): List<SkillType> = skills.values.toList()

    fun get(name: String): SkillType? = skills[name]
}
