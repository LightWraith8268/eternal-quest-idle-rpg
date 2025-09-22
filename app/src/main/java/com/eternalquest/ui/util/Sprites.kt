package com.eternalquest.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
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
    private val bows = listOf(
        R.drawable.sprite_weapon_bow
    )
    private val staffs = listOf(
        R.drawable.sprite_weapon_staff
    )
    private val daggers = listOf(
        R.drawable.sprite_weapon_dagger
    )
    private val maces = listOf(
        R.drawable.sprite_weapon_mace
    )
    private val spears = listOf(
        R.drawable.sprite_weapon_spear
    )
    private val helmets = listOf(
        R.drawable.sprite_helmet_leather,
        R.drawable.sprite_helmet_iron
    )
    private val chests = listOf(
        R.drawable.sprite_chest_leather,
        R.drawable.sprite_chest_iron
    )
    private val leggings = listOf(
        R.drawable.sprite_armor_leggings
    )
    private val boots = listOf(
        R.drawable.sprite_armor_boots
    )
    private val gloves = listOf(
        R.drawable.sprite_armor_gloves
    )
    private val shields = listOf(
        R.drawable.sprite_armor_shield
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
        val item = try {
            com.eternalquest.util.ItemCatalog.get(itemId)
        } catch (_: Exception) {
            null
        } ?: GameItems.ALL.find { it.id == itemId }
        val tint = hashColor(itemId)
        return when (item?.category) {
            ItemCategory.ORE -> pick(ores, itemId)
            ItemCategory.LOG -> pick(logs, itemId)
            ItemCategory.FISH -> pick(fishRaw, itemId)
            ItemCategory.FOOD -> pick(fishCooked, itemId)
            ItemCategory.WEAPON -> pick(swords + axes + bows + staffs + daggers + maces + spears, itemId)
            ItemCategory.ARMOR -> pick(helmets + chests + leggings + boots + gloves + shields, itemId)
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

    // Composable painters that procedurally generate unique vectors per id
    @Composable
    fun painterForItemId(itemId: String): androidx.compose.ui.graphics.painter.Painter {
        val context = androidx.compose.ui.platform.LocalContext.current
        com.eternalquest.util.SpritesMap.load(context)
        val mapped = com.eternalquest.util.SpritesMap.resolveDrawableId(context, "item", itemId)
        if (mapped != null) return androidx.compose.ui.res.painterResource(id = mapped)
        val item = try { com.eternalquest.util.ItemCatalog.get(itemId) } catch (_: Exception) { null } ?: GameItems.ALL.find { it.id == itemId }
        val vector = remember(itemId) { SpriteGenerator.vectorForItem(itemId, item?.category) }
        return rememberVectorPainter(vector)
    }

    @Composable
    fun painterForEnemyId(enemyId: String): androidx.compose.ui.graphics.painter.Painter {
        val context = androidx.compose.ui.platform.LocalContext.current
        com.eternalquest.util.SpritesMap.load(context)
        val mapped = com.eternalquest.util.SpritesMap.resolveDrawableId(context, "enemy", enemyId)
        if (mapped != null) return androidx.compose.ui.res.painterResource(id = mapped)
        val vector = remember(enemyId) { SpriteGenerator.vectorForEnemy(enemyId) }
        return rememberVectorPainter(vector)
    }

    @Composable
    fun painterForAreaId(areaId: String): androidx.compose.ui.graphics.painter.Painter {
        val context = androidx.compose.ui.platform.LocalContext.current
        com.eternalquest.util.SpritesMap.load(context)
        val mapped = com.eternalquest.util.SpritesMap.resolveDrawableId(context, "area", areaId)
        if (mapped != null) return androidx.compose.ui.res.painterResource(id = mapped)
        val vector = remember(areaId) { SpriteGenerator.vectorForArea(areaId) }
        return rememberVectorPainter(vector)
    }
}

// Procedural vector generator to ensure uniqueness per id while keeping it lightweight
private object SpriteGenerator {
    private val basePalette = listOf(
        Color(0xFFEF5350), Color(0xFFAB47BC), Color(0xFF5C6BC0), Color(0xFF29B6F6),
        Color(0xFF26A69A), Color(0xFF66BB6A), Color(0xFFFFCA28), Color(0xFFFF7043)
    )

    private fun color(hash: Int, index: Int): Color {
        val idx = ((hash ushr (index * 4)) and 0xF) % basePalette.size
        return basePalette[idx]
    }

    fun vectorForItem(id: String, category: ItemCategory?): ImageVector {
        val h = id.hashCode()
        val c0 = color(h, 0)
        val c1 = color(h, 1)
        val c2 = color(h, 2)
        // 48x48 viewport
        val builder = Builder(name = "item_$id", defaultWidth = 48.dp, defaultHeight = 48.dp, viewportWidth = 48f, viewportHeight = 48f)
        // Base shape varies by category
        when (category) {
            ItemCategory.ORE -> builder.path(fill = SolidColor(c0), pathFillType = PathFillType.NonZero) {
                moveTo(12f, 18f); lineTo(24f, 10f); lineTo(36f, 18f); lineTo(30f, 34f); lineTo(18f, 34f); close()
            }
            ItemCategory.LOG -> builder.path(fill = SolidColor(c0)) {
                moveTo(12f, 14f); lineTo(36f, 14f); lineTo(36f, 34f); lineTo(12f, 34f); close()
            }
            ItemCategory.FISH -> builder.path(fill = SolidColor(c0)) {
                moveTo(10f, 24f); lineTo(18f, 16f); lineTo(30f, 16f); lineTo(36f, 24f); lineTo(30f, 32f); lineTo(18f, 32f); close()
                moveTo(36f, 24f); lineTo(42f, 20f); lineTo(42f, 28f); close()
            }
            ItemCategory.FOOD -> builder.path(fill = SolidColor(c0)) {
                moveTo(12f, 28f); lineTo(20f, 22f); lineTo(28f, 22f); lineTo(36f, 28f); lineTo(36f, 34f); lineTo(12f, 34f); close()
            }
            ItemCategory.WEAPON -> builder.path(fill = SolidColor(c0)) {
                moveTo(22f, 10f); lineTo(26f, 10f); lineTo(26f, 34f); lineTo(22f, 34f); close()
                moveTo(20f, 34f); lineTo(28f, 34f); lineTo(28f, 38f); lineTo(20f, 38f); close()
            }
            ItemCategory.ARMOR -> builder.path(fill = SolidColor(c0)) {
                moveTo(16f, 12f); lineTo(32f, 12f); lineTo(36f, 20f); lineTo(32f, 36f); lineTo(16f, 36f); lineTo(12f, 20f); close()
            }
            else -> builder.path(fill = SolidColor(c0)) {
                moveTo(12f, 12f); lineTo(36f, 12f); lineTo(36f, 36f); lineTo(12f, 36f); close()
            }
        }
        // Accent shapes derived from hash to make each id unique
        builder.path(fill = SolidColor(c1.copy(alpha = 0.8f))) {
            moveTo((h ushr 1 and 15).toFloat() + 8f, 16f)
            lineTo(40f, ((h ushr 5) and 15).toFloat() + 18f)
            lineTo(12f, ((h ushr 9) and 15).toFloat() + 22f)
            close()
        }
        builder.path(fill = SolidColor(c2.copy(alpha = 0.6f))) {
            moveTo(((h ushr 3) and 15).toFloat() + 10f, ((h ushr 7) and 15).toFloat() + 10f)
            lineTo(((h ushr 11) and 15).toFloat() + 20f, ((h ushr 13) and 15).toFloat() + 10f)
            lineTo(((h ushr 15) and 15).toFloat() + 14f, ((h ushr 17) and 15).toFloat() + 20f)
            close()
        }
        return builder.build()
    }

    fun vectorForEnemy(id: String): ImageVector {
        val h = id.hashCode()
        val c0 = color(h, 0)
        val c1 = color(h, 1)
        val builder = Builder(name = "enemy_$id", defaultWidth = 64.dp, defaultHeight = 64.dp, viewportWidth = 64f, viewportHeight = 64f)
        // Body
        builder.path(fill = SolidColor(c0)) { moveTo(20f, 20f); lineTo(44f, 20f); lineTo(44f, 44f); lineTo(20f, 44f); close() }
        // Eyes / features
        builder.path(fill = SolidColor(Color(0xFF212121))) {
            moveTo(26f, 28f); lineTo(30f, 28f); lineTo(30f, 32f); lineTo(26f, 32f); close()
            moveTo(34f, 28f); lineTo(38f, 28f); lineTo(38f, 32f); lineTo(34f, 32f); close()
        }
        // Accent
        builder.path(fill = SolidColor(c1.copy(alpha = 0.7f))) {
            moveTo(24f, 36f); lineTo(40f, 36f); lineTo(32f, 48f); close()
        }
        return builder.build()
    }

    fun vectorForArea(id: String): ImageVector {
        val h = id.hashCode()
        val c0 = color(h, 0)
        val c1 = color(h, 2)
        val builder = Builder(name = "area_$id", defaultWidth = 48.dp, defaultHeight = 48.dp, viewportWidth = 48f, viewportHeight = 48f)
        builder.path(fill = SolidColor(c0)) { moveTo(4f, 34f); lineTo(44f, 34f); lineTo(44f, 44f); lineTo(4f, 44f); close() }
        builder.path(fill = SolidColor(c1)) { moveTo(10f, 20f); lineTo(24f, 10f); lineTo(38f, 20f); lineTo(38f, 34f); lineTo(10f, 34f); close() }
        return builder.build()
    }
}
