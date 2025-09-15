package com.eternalquest.ui.util

import androidx.compose.ui.graphics.Color
import com.eternalquest.R
import com.eternalquest.data.entities.GameItems
import com.eternalquest.data.entities.ItemCategory

data class SpriteRef(val resId: Int, val tint: Color? = null)

object Sprites {
    private val ores = listOf(
        R.drawable.sprite_ore_copper,
        R.drawable.sprite_ore_iron,
        R.drawable.sprite_ore_coal,
        R.drawable.sprite_ore_mithril,
        R.drawable.sprite_ore_gold
    )
    private val logs = listOf(
        R.drawable.sprite_log_oak,
        R.drawable.sprite_log_willow,
        R.drawable.sprite_log_maple,
        R.drawable.sprite_log_yew
    )
    private val fishRaw = listOf(
        R.drawable.sprite_fish_trout,
        R.drawable.sprite_fish_salmon,
        R.drawable.sprite_fish_tuna,
        R.drawable.sprite_fish_swordfish
    )
    private val fishCooked = listOf(
        R.drawable.sprite_fish_cooked
    )
    private val bars = listOf(
        R.drawable.sprite_bar_iron,
        R.drawable.sprite_bar_gold,
        R.drawable.sprite_bar_mithril
    )
    private val swords = listOf(
        R.drawable.sprite_sword_bronze,
        R.drawable.sprite_sword_iron,
        R.drawable.sprite_sword_steel,
        R.drawable.sprite_sword_dragon
    )
    private val axes = listOf(
        R.drawable.sprite_axe_bronze,
        R.drawable.sprite_axe_iron
    )
    private val helmets = listOf(
        R.drawable.sprite_helmet_leather,
        R.drawable.sprite_helmet_iron
    )
    private val chests = listOf(
        R.drawable.sprite_chest_leather,
        R.drawable.sprite_chest_iron
    )
    private val drops = listOf(
        R.drawable.sprite_meat_raw,
        R.drawable.sprite_drop_rat_tail,
        R.drawable.sprite_drop_goblin_ear,
        R.drawable.sprite_drop_orc_tooth,
        R.drawable.sprite_drop_bone,
        R.drawable.sprite_drop_dragon_scale,
        R.drawable.sprite_drop_dragon_bone
    )

    private val enemyMap = mapOf(
        "rat" to R.drawable.sprite_enemy_rat,
        "goblin" to R.drawable.sprite_enemy_goblin,
        "orc" to R.drawable.sprite_enemy_orc,
        "skeleton" to R.drawable.sprite_enemy_skeleton,
        "dragon" to R.drawable.sprite_enemy_dragon
    )

    private val palette = listOf(
        Color(0xFFEF9A9A), Color(0xFFF48FB1), Color(0xFFCE93D8), Color(0xFFB39DDB),
        Color(0xFF9FA8DA), Color(0xFF90CAF9), Color(0xFF81D4FA), Color(0xFF80DEEA),
        Color(0xFFA5D6A7), Color(0xFFFFF59D)
    )

    private fun hashColor(key: String): Color = palette[(key.hashCode().ushr(1)) % palette.size]

    fun forEnemyId(enemyId: String): SpriteRef {
        // Direct mapping first
        enemyMap[enemyId]?.let { return SpriteRef(it) }
        val id = enemyId.lowercase()
        val res = when {
            // Specific creature heuristics
            id.contains("slime") -> R.drawable.sprite_enemy_slime
            id.contains("bat") -> R.drawable.sprite_enemy_bat
            id.contains("spider") -> R.drawable.sprite_enemy_spider
            id.contains("wolf") || id.contains("bear") || id.contains("beast") -> R.drawable.sprite_enemy_beast
            id.contains("kobold") || id.contains("gnoll") || id.contains("bandit") || id.contains("ogre") || id.contains("troll") || id.contains("minotaur") || id.contains("warlord") || id.contains("knight") || id.contains("assassin") || id.contains("mage") -> R.drawable.sprite_enemy_humanoid
            id.contains("zombie") || id.contains("skeleton") || id.contains("lich") || id.contains("archlich") || id.contains("wraith") -> R.drawable.sprite_enemy_undead
            id.contains("vampire") -> R.drawable.sprite_enemy_vampire
            id.contains("elemental") -> R.drawable.sprite_enemy_elemental
            id.contains("golem") -> R.drawable.sprite_enemy_golem
            id.contains("angel") -> R.drawable.sprite_enemy_angel
            id.contains("demon") -> R.drawable.sprite_enemy_demon
            id.contains("wyvern") || id.contains("drake") || id.contains("wyrm") || id.contains("dragon") -> R.drawable.sprite_enemy_dragon
            id.contains("void") -> R.drawable.sprite_enemy_void
            id.contains("cosmic") || id.contains("primordial") || id.contains("deity") || id.contains("world_eater") -> R.drawable.sprite_enemy_cosmic
            else -> R.drawable.sprite_enemy_rat
        }
        return SpriteRef(res)
    }

    fun forItemId(itemId: String): SpriteRef {
        val item = GameItems.ALL.find { it.id == itemId }
        val name = item?.name?.lowercase() ?: itemId.lowercase()
        val tint = hashColor(itemId)
        return when (item?.category) {
            ItemCategory.ORE -> pick(ores, itemId)
            ItemCategory.LOG -> pick(logs, itemId)
            ItemCategory.FISH -> pick(fishRaw, itemId)
            ItemCategory.FOOD -> pick(fishCooked, itemId)
            ItemCategory.WEAPON -> pick(swords + axes, itemId)
            ItemCategory.ARMOR -> pick(helmets + chests, itemId)
            ItemCategory.TOOL -> SpriteRef(R.drawable.sprite_item_tool)
            ItemCategory.CONSUMABLE -> SpriteRef(R.drawable.sprite_item_consumable)
            ItemCategory.COMBAT_DROP -> pick(drops, itemId)
            ItemCategory.MISC -> SpriteRef(R.drawable.sprite_item_misc)
            ItemCategory.HERB -> SpriteRef(R.drawable.sprite_item_herb)
            ItemCategory.POTION -> SpriteRef(R.drawable.sprite_item_potion)
            ItemCategory.CRYSTAL -> SpriteRef(R.drawable.sprite_item_crystal)
            ItemCategory.RUNE -> SpriteRef(R.drawable.sprite_item_rune)
            ItemCategory.GEM -> SpriteRef(R.drawable.sprite_item_gem)
            ItemCategory.ARTIFACT -> SpriteRef(R.drawable.sprite_item_artifact)
            ItemCategory.ESSENCE -> SpriteRef(R.drawable.sprite_item_essence)
            ItemCategory.BONE_ITEM -> SpriteRef(R.drawable.sprite_item_bone_item)
            ItemCategory.SEED -> SpriteRef(R.drawable.sprite_item_seed)
            ItemCategory.FLOWER -> SpriteRef(R.drawable.sprite_item_flower)
            ItemCategory.INGREDIENT -> SpriteRef(R.drawable.sprite_item_ingredient)
            ItemCategory.SCROLL -> SpriteRef(R.drawable.sprite_item_scroll)
            ItemCategory.KEY -> SpriteRef(R.drawable.sprite_item_key)
            ItemCategory.TREASURE -> SpriteRef(R.drawable.sprite_item_treasure)
            ItemCategory.FRAGMENT -> SpriteRef(R.drawable.sprite_item_fragment)
            ItemCategory.SOUL -> SpriteRef(R.drawable.sprite_item_soul)
            ItemCategory.DUST -> SpriteRef(R.drawable.sprite_item_dust)
            ItemCategory.SHARD -> SpriteRef(R.drawable.sprite_item_shard)
            ItemCategory.ORB -> SpriteRef(R.drawable.sprite_item_orb)
            ItemCategory.TOME -> SpriteRef(R.drawable.sprite_item_tome)
            ItemCategory.RELIC -> SpriteRef(R.drawable.sprite_item_relic)
            ItemCategory.COMPONENT -> SpriteRef(R.drawable.sprite_item_component)
            ItemCategory.CATALYST -> SpriteRef(R.drawable.sprite_item_catalyst)
            else -> SpriteRef(R.drawable.sprite_meat_raw, tint)
        }
    }

    private fun pick(list: List<Int>, key: String): SpriteRef {
        if (list.isEmpty()) return SpriteRef(R.drawable.sprite_meat_raw, hashColor(key))
        val idx = (key.hashCode().ushr(1)) % list.size
        return SpriteRef(list[idx], null)
    }

    // Optional helpers for future UI usage
    fun forSkillName(skillName: String): SpriteRef {
        return when (skillName.lowercase()) {
            "mining" -> SpriteRef(R.drawable.sprite_skill_mining)
            "woodcutting" -> SpriteRef(R.drawable.sprite_skill_woodcutting)
            "fishing" -> SpriteRef(R.drawable.sprite_skill_fishing)
            "cooking" -> SpriteRef(R.drawable.sprite_skill_cooking)
            "smithing" -> SpriteRef(R.drawable.sprite_skill_smithing)
            else -> SpriteRef(R.drawable.sprite_player)
        }
    }

    fun forAreaId(areaId: String): SpriteRef {
        val id = areaId.lowercase()
        val res = when {
            id.contains("boss") -> R.drawable.sprite_area_boss
            id.contains("raid") -> R.drawable.sprite_area_raid
            id.contains("cave") || id.contains("crypt") || id.contains("castle") || id.contains("lair") || id.contains("temple") || id.contains("fortress") || id.contains("dungeon") || id.contains("ruins") || id.contains("portal") -> R.drawable.sprite_area_dungeon
            else -> R.drawable.sprite_area_overworld
        }
        return SpriteRef(res)
    }
}
