package com.eternalquest.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_states")
data class ActivityState(
    @PrimaryKey val id: Int = 1,
    val activeSkill: String? = null,
    val activityType: String? = null,
    val startTime: Long? = null,
    val progress: Float = 0f,
    val targetItemId: String? = null,
    val isActive: Boolean = false
)

data class ActivityDefinition(
    val id: String,
    val name: String,
    val skill: String,
    val baseTimeMs: Long,
    val xpReward: Long,
    val itemRewards: List<ItemReward>,
    val itemCosts: List<ItemCost> = emptyList(),
    val requirements: Map<String, Int> = emptyMap()
)

data class ItemReward(
    val itemId: String,
    val quantity: Int,
    val chance: Float = 1.0f
)

data class ItemCost(
    val itemId: String,
    val quantity: Int
)

object Activities {
    val MINE_COPPER = ActivityDefinition(
        "mine_copper",
        "Mine Copper Ore",
        "mining",
        3000L,
        10L,
        listOf(ItemReward("copper_ore", 1))
    )
    
    val MINE_IRON = ActivityDefinition(
        "mine_iron",
        "Mine Iron Ore", 
        "mining",
        4000L,
        15L,
        listOf(ItemReward("iron_ore", 1)),
        requirements = mapOf("mining" to 15)
    )
    
    val CUT_OAK = ActivityDefinition(
        "cut_oak",
        "Cut Oak Trees",
        "woodcutting",
        2500L,
        8L,
        listOf(ItemReward("oak_log", 1))
    )
    
    val CUT_WILLOW = ActivityDefinition(
        "cut_willow",
        "Cut Willow Trees",
        "woodcutting",
        3500L,
        12L,
        listOf(ItemReward("willow_log", 1)),
        requirements = mapOf("woodcutting" to 20)
    )
    
    val FISH_TROUT = ActivityDefinition(
        "fish_trout",
        "Fish for Trout",
        "fishing",
        3000L,
        10L,
        listOf(ItemReward("raw_trout", 1))
    )
    
    val FISH_SALMON = ActivityDefinition(
        "fish_salmon", 
        "Fish for Salmon",
        "fishing",
        4500L,
        18L,
        listOf(ItemReward("raw_salmon", 1)),
        requirements = mapOf("fishing" to 25)
    )
    
    // Phase 4: Extended gathering/cooking
    val FISH_TUNA = ActivityDefinition(
        "fish_tuna",
        "Fish for Tuna",
        "fishing",
        6000L,
        28L,
        listOf(ItemReward("raw_tuna", 1)),
        requirements = mapOf("fishing" to 35)
    )
    
    val COOK_TROUT = ActivityDefinition(
        "cook_trout",
        "Cook Trout",
        "cooking",
        2000L,
        12L,
        listOf(ItemReward("cooked_trout", 1)),
        requirements = mapOf("cooking" to 1)
    )
    
    val COOK_SALMON = ActivityDefinition(
        "cook_salmon",
        "Cook Salmon", 
        "cooking",
        3000L,
        20L,
        listOf(ItemReward("cooked_salmon", 1)),
        requirements = mapOf("cooking" to 15)
    )
    
    val COOK_TUNA = ActivityDefinition(
        "cook_tuna",
        "Cook Tuna",
        "cooking",
        3500L,
        32L,
        listOf(ItemReward("cooked_tuna", 1)),
        requirements = mapOf("cooking" to 25)
    )
    
    val MINE_COAL = ActivityDefinition(
        "mine_coal",
        "Mine Coal Ore",
        "mining",
        5000L,
        22L,
        listOf(ItemReward("coal_ore", 1)),
        requirements = mapOf("mining" to 30)
    )
    
    val CUT_MAPLE = ActivityDefinition(
        "cut_maple",
        "Cut Maple Trees",
        "woodcutting",
        4500L,
        20L,
        listOf(ItemReward("maple_log", 1)),
        requirements = mapOf("woodcutting" to 35)
    )
    
    // Extended Phase 4 activities
    val MINE_MITHRIL = ActivityDefinition(
        "mine_mithril",
        "Mine Mithril Ore",
        "mining",
        7000L,
        40L,
        listOf(ItemReward("mithril_ore", 1)),
        requirements = mapOf("mining" to 50)
    )

    val CUT_YEW = ActivityDefinition(
        "cut_yew",
        "Cut Yew Trees",
        "woodcutting",
        6000L,
        28L,
        listOf(ItemReward("yew_log", 1)),
        requirements = mapOf("woodcutting" to 50)
    )

    val FISH_SWORDFISH = ActivityDefinition(
        "fish_swordfish",
        "Fish for Swordfish",
        "fishing",
        8000L,
        40L,
        listOf(ItemReward("raw_swordfish", 1)),
        requirements = mapOf("fishing" to 55)
    )

    val COOK_SWORDFISH = ActivityDefinition(
        "cook_swordfish",
        "Cook Swordfish",
        "cooking",
        4000L,
        45L,
        listOf(ItemReward("cooked_swordfish", 1)),
        requirements = mapOf("cooking" to 45)
    )

    // Phase 5: Smithing (smelting)
    val SMELT_IRON_BAR = ActivityDefinition(
        "smelt_iron_bar",
        "Smelt Iron Bar",
        "smithing",
        5000L,
        30L,
        listOf(ItemReward("iron_bar", 1)),
        itemCosts = listOf(ItemCost("iron_ore", 1), ItemCost("coal_ore", 1)),
        requirements = mapOf("smithing" to 1)
    )

    val SMELT_GOLD_BAR = ActivityDefinition(
        "smelt_gold_bar",
        "Smelt Gold Bar",
        "smithing",
        6000L,
        45L,
        listOf(ItemReward("gold_bar", 1)),
        itemCosts = listOf(ItemCost("gold_ore", 1), ItemCost("coal_ore", 1)),
        requirements = mapOf("smithing" to 20)
    )

    val SMELT_MITHRIL_BAR = ActivityDefinition(
        "smelt_mithril_bar",
        "Smelt Mithril Bar",
        "smithing",
        7000L,
        65L,
        listOf(ItemReward("mithril_bar", 1)),
        itemCosts = listOf(ItemCost("mithril_ore", 1), ItemCost("coal_ore", 2)),
        requirements = mapOf("smithing" to 40)
    )

    // Smithing (forging)
    val FORGE_IRON_SWORD = ActivityDefinition(
        "forge_iron_sword",
        "Forge Iron Sword",
        "smithing",
        8000L,
        70L,
        listOf(ItemReward("iron_sword", 1)),
        itemCosts = listOf(ItemCost("iron_bar", 2)),
        requirements = mapOf("smithing" to 10)
    )

    val FORGE_IRON_HELMET = ActivityDefinition(
        "forge_iron_helmet",
        "Forge Iron Helmet",
        "smithing",
        8500L,
        75L,
        listOf(ItemReward("iron_helmet", 1)),
        itemCosts = listOf(ItemCost("iron_bar", 3)),
        requirements = mapOf("smithing" to 15)
    )

    private val BASE = listOf(
        MINE_COPPER, MINE_IRON, MINE_COAL, MINE_MITHRIL,
        CUT_OAK, CUT_WILLOW, CUT_MAPLE, CUT_YEW,
        FISH_TROUT, FISH_SALMON, FISH_TUNA, FISH_SWORDFISH,
        COOK_TROUT, COOK_SALMON, COOK_TUNA, COOK_SWORDFISH,
        SMELT_IRON_BAR, SMELT_GOLD_BAR, SMELT_MITHRIL_BAR,
        FORGE_IRON_SWORD, FORGE_IRON_HELMET
    )

    @Volatile
    private var extras: List<ActivityDefinition> = emptyList()

    val ALL: List<ActivityDefinition>
        get() = BASE + extras

    fun loadExtrasFromAssets(context: android.content.Context) {
        try {
            val loaded = com.eternalquest.util.ConfigLoader.loadActivitiesFromAssets(context)
            val baseIds = BASE.map { it.id }.toSet()
            val unique = loaded.filter { it.id !in baseIds }
            extras = unique
        } catch (_: Exception) {
            extras = emptyList()
        }
    }
}
