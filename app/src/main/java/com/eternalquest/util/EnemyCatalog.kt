package com.eternalquest.util

import android.content.Context
import com.eternalquest.data.entities.Enemy

object EnemyCatalog {
    @Volatile private var loaded: Boolean = false
    @Volatile private var enemiesById: Map<String, Enemy> = emptyMap()

    fun load(context: Context) {
        if (loaded) return
        synchronized(this) {
            if (loaded) return
            val merged = mutableListOf<Enemy>()
            try {
                // Prefer folder-based files under config/enemies/*.json
                val enemyDir = "config/enemies"
                val files = try { context.assets.list(enemyDir) } catch (_: Exception) { null }
                if (files != null && files.isNotEmpty()) {
                    for (name in files) {
                        if (!name.endsWith(".json")) continue
                        val json = context.assets.open("$enemyDir/$name").bufferedReader().use { it.readText() }
                        merged += parseEnemies(json)
                    }
                } else {
                    // Fallback single file monsters.json
                    val json = context.assets.open("config/monsters.json").bufferedReader().use { it.readText() }
                    merged += parseEnemies(json)
                }
            } catch (_: Exception) {
                // Fallback to built-ins
                val builtins = collectBuiltIn()
                enemiesById = builtins.associateBy { it.id }
                loaded = true
                return
            }
            // Enforce unique IDs (hard fail on duplicate)
            val ids = mutableSetOf<String>()
            for (e in merged) {
                if (!ids.add(e.id)) throw IllegalStateException("Duplicate enemy id detected: ${e.id}")
            }
            enemiesById = merged.associateBy { it.id }
            loaded = true
        }
    }

    private fun parseEnemies(json: String): List<Enemy> {
        val out = mutableListOf<Enemy>()
        val root = org.json.JSONObject(json)
        val arr = root.optJSONArray("monsters") ?: root.optJSONArray("enemies")
        if (arr != null) {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val id = o.getString("id")
                val name = o.optString("name", id)
                val level = o.optInt("level", 1)
                val hp = o.optInt("hitpoints", 10)
                val maxHp = o.optInt("maxHitpoints", hp)
                val attack = o.optInt("attack", 1)
                val strength = o.optInt("strength", 1)
                val defense = o.optInt("defense", 1)
                val speed = o.optLong("attackSpeed", 4000L)
                val accuracy = o.optDouble("accuracy", 0.8).toFloat()
                val xp = o.optLong("experienceReward", 10L)
                val req = o.optInt("combatLevelRequired", 1)
                val desc = o.optString("description", "")
                val areaStr = o.optString("area", "STARTING_MEADOW")
                val area = try { com.eternalquest.data.entities.EnemyArea.valueOf(areaStr) } catch (_: Exception) { com.eternalquest.data.entities.EnemyArea.STARTING_MEADOW }
                out += com.eternalquest.data.entities.Enemy(id, name, level, hp, maxHp, attack, strength, defense, speed, accuracy, xp, req, desc, area)
            }
        }
        return out
    }

    private fun collectBuiltIn(): List<Enemy> {
        return com.eternalquest.data.entities.Enemies.ALL
    }

    fun get(id: String): Enemy? = enemiesById[id]
    fun all(): List<Enemy> = enemiesById.values.toList()
}
