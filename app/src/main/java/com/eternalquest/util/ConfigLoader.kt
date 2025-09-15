package com.eternalquest.util

import android.content.Context
import com.eternalquest.data.entities.ActivityDefinition
import com.eternalquest.data.entities.ItemCost
import com.eternalquest.data.entities.ItemReward
import org.json.JSONArray
import org.json.JSONObject

object ConfigLoader {
    fun loadActivitiesFromAssets(context: Context, path: String = "config/recipes.json"): List<ActivityDefinition> {
        return try {
            val json = context.assets.open(path).bufferedReader().use { it.readText() }
            parseActivities(JSONObject(json))
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseActivities(root: JSONObject): List<ActivityDefinition> {
        val list = mutableListOf<ActivityDefinition>()
        val arr = root.optJSONArray("activities") ?: JSONArray()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val id = obj.getString("id")
            val name = obj.getString("name")
            val skill = obj.getString("skill")
            val baseTimeMs = obj.optLong("baseTimeMs", 3000L)
            val xpReward = obj.optLong("xpReward", 10L)
            val rewards = parseItemRewards(obj.optJSONArray("itemRewards"))
            val costs = parseItemCosts(obj.optJSONArray("itemCosts"))
            val reqs = parseRequirements(obj.optJSONObject("requirements"))
            list.add(
                ActivityDefinition(
                    id = id,
                    name = name,
                    skill = skill,
                    baseTimeMs = baseTimeMs,
                    xpReward = xpReward,
                    itemRewards = rewards,
                    itemCosts = costs,
                    requirements = reqs
                )
            )
        }
        return list
    }

    private fun parseItemRewards(arr: JSONArray?): List<ItemReward> {
        if (arr == null) return emptyList()
        val list = mutableListOf<ItemReward>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(
                ItemReward(
                    itemId = o.getString("itemId"),
                    quantity = o.optInt("quantity", 1),
                    chance = o.optDouble("chance", 1.0).toFloat()
                )
            )
        }
        return list
    }

    private fun parseItemCosts(arr: JSONArray?): List<ItemCost> {
        if (arr == null) return emptyList()
        val list = mutableListOf<ItemCost>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(
                ItemCost(
                    itemId = o.getString("itemId"),
                    quantity = o.optInt("quantity", 1)
                )
            )
        }
        return list
    }

    private fun parseRequirements(obj: JSONObject?): Map<String, Int> {
        if (obj == null) return emptyMap()
        val map = mutableMapOf<String, Int>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            map[k] = obj.optInt(k, 1)
        }
        return map
    }
}

