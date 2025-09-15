package com.eternalquest.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: ItemCategory,
    val stackSize: Int = 1,
    val value: Int = 0,
    val iconResource: String? = null
)

@Entity(tableName = "bank_items")
data class BankItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: String,
    val tabIndex: Int,
    val slotIndex: Int,
    val quantity: Int
)

enum class ItemCategory {
    ORE,
    LOG,
    FISH,
    FOOD,
    TOOL,
    WEAPON,
    ARMOR,
    CONSUMABLE,
    COMBAT_DROP,
    MISC,
    HERB,
    POTION,
    CRYSTAL,
    RUNE,
    GEM,
    ARTIFACT,
    ESSENCE,
    BONE_ITEM,
    SEED,
    FLOWER,
    INGREDIENT,
    SCROLL,
    KEY,
    TREASURE,
    FRAGMENT,
    SOUL,
    DUST,
    SHARD,
    ORB,
    TOME,
    RELIC,
    COMPONENT,
    CATALYST
}

object GameItems {
    val COPPER_ORE = Item("copper_ore", "Copper Ore", "A chunk of copper ore", ItemCategory.ORE, 100, 5)
    val TIN_ORE = Item("tin_ore", "Tin Ore", "A chunk of tin ore", ItemCategory.ORE, 100, 4)
    val IRON_ORE = Item("iron_ore", "Iron Ore", "A chunk of iron ore", ItemCategory.ORE, 100, 10)
    val OAK_LOG = Item("oak_log", "Oak Log", "A sturdy oak log", ItemCategory.LOG, 100, 3)
    val WILLOW_LOG = Item("willow_log", "Willow Log", "A flexible willow log", ItemCategory.LOG, 100, 6)
    val RAW_TROUT = Item("raw_trout", "Raw Trout", "A fresh trout", ItemCategory.FISH, 50, 8)
    val RAW_SALMON = Item("raw_salmon", "Raw Salmon", "A large salmon", ItemCategory.FISH, 50, 12)
    val COOKED_TROUT = Item("cooked_trout", "Cooked Trout", "A delicious cooked trout", ItemCategory.FOOD, 25, 15)
    val COOKED_SALMON = Item("cooked_salmon", "Cooked Salmon", "A perfectly cooked salmon", ItemCategory.FOOD, 25, 25)

    // Extended tier resources/food (Phase 4)
    val RAW_TUNA = Item("raw_tuna", "Raw Tuna", "A hefty tuna", ItemCategory.FISH, 50, 20)
    val COOKED_TUNA = Item("cooked_tuna", "Cooked Tuna", "A savory cooked tuna", ItemCategory.FOOD, 25, 40)
    val COAL_ORE = Item("coal_ore", "Coal Ore", "A chunk of coal", ItemCategory.ORE, 100, 15)
    val MAPLE_LOG = Item("maple_log", "Maple Log", "A resilient maple log", ItemCategory.LOG, 100, 10)
    val BIRCH_LOG = Item("birch_log", "Birch Log", "A pale birch log", ItemCategory.LOG, 100, 4)
    val TEAK_LOG = Item("teak_log", "Teak Log", "A dense teak log", ItemCategory.LOG, 100, 8)
    val MITHRIL_ORE = Item("mithril_ore", "Mithril Ore", "A rare mithril ore", ItemCategory.ORE, 100, 40)
    val SILVER_ORE = Item("silver_ore", "Silver Ore", "A chunk of silver ore", ItemCategory.ORE, 100, 20)
    val GOLD_ORE = Item("gold_ore", "Gold Ore", "A valuable gold ore", ItemCategory.ORE, 100, 60)
    val ADAMANTITE_ORE = Item("adamantite_ore", "Adamantite Ore", "A very strong ore", ItemCategory.ORE, 100, 80)
    val YEW_LOG = Item("yew_log", "Yew Log", "A dense yew log", ItemCategory.LOG, 100, 20)
    val RAW_SWORDFISH = Item("raw_swordfish", "Raw Swordfish", "A large swordfish", ItemCategory.FISH, 50, 35)
    val COOKED_SWORDFISH = Item("cooked_swordfish", "Cooked Swordfish", "A juicy cooked swordfish", ItemCategory.FOOD, 25, 70)

    // Ultra tier resources
    val RUNITE_ORE = Item("runite_ore", "Runite Ore", "A legendary ore that pulses with power", ItemCategory.ORE, 100, 150)
    val DRAGONITE_ORE = Item("dragonite_ore", "Dragonite Ore", "An otherworldly ore found in dragon lairs", ItemCategory.ORE, 100, 300)
    val CELESTIAL_ORE = Item("celestial_ore", "Celestial Ore", "A divine ore that fell from the heavens", ItemCategory.ORE, 100, 500)
    val VOID_ORE = Item("void_ore", "Void Ore", "A mysterious ore from the void realm", ItemCategory.ORE, 100, 800)
    val ETHEREAL_ORE = Item("ethereal_ore", "Ethereal Ore", "A ghostly ore that phases in and out of reality", ItemCategory.ORE, 100, 1200)

    // Ultra tier logs
    val REDWOOD_LOG = Item("redwood_log", "Redwood Log", "A massive log from an ancient tree", ItemCategory.LOG, 100, 45)
    val ELDERWOOD_LOG = Item("elderwood_log", "Elderwood Log", "Wood from the eldest trees in the forest", ItemCategory.LOG, 100, 80)
    val IRONBARK_LOG = Item("ironbark_log", "Ironbark Log", "Wood as hard as iron itself", ItemCategory.LOG, 100, 120)
    val SHADOWBARK_LOG = Item("shadowbark_log", "Shadowbark Log", "Dark wood that absorbs light", ItemCategory.LOG, 100, 200)
    val WORLD_TREE_LOG = Item("world_tree_log", "World Tree Log", "A branch from the mythical world tree", ItemCategory.LOG, 100, 400)

    // Ultra tier fish
    val RAW_SHARK = Item("raw_shark", "Raw Shark", "A massive predator of the deep", ItemCategory.FISH, 50, 60)
    val RAW_MANTA_RAY = Item("raw_manta_ray", "Raw Manta Ray", "A graceful giant of the ocean", ItemCategory.FISH, 50, 95)
    val RAW_KRAKEN_TENTACLE = Item("raw_kraken_tentacle", "Raw Kraken Tentacle", "A tentacle from the legendary kraken", ItemCategory.FISH, 50, 150)
    val RAW_LEVIATHAN = Item("raw_leviathan", "Raw Leviathan", "Meat from the ancient sea beast", ItemCategory.FISH, 50, 250)
    val RAW_VOID_FISH = Item("raw_void_fish", "Raw Void Fish", "A fish from the void dimension", ItemCategory.FISH, 50, 400)

    // Ultra tier cooked food
    val COOKED_SHARK = Item("cooked_shark", "Cooked Shark", "Perfectly grilled shark steak", ItemCategory.FOOD, 25, 120)
    val COOKED_MANTA_RAY = Item("cooked_manta_ray", "Cooked Manta Ray", "A delicacy fit for kings", ItemCategory.FOOD, 25, 190)
    val COOKED_KRAKEN_TENTACLE = Item("cooked_kraken_tentacle", "Cooked Kraken Tentacle", "Legendary seafood with mystical properties", ItemCategory.FOOD, 25, 300)
    val COOKED_LEVIATHAN = Item("cooked_leviathan", "Cooked Leviathan", "Ancient beast meat that grants strength", ItemCategory.FOOD, 25, 500)
    val COOKED_VOID_FISH = Item("cooked_void_fish", "Cooked Void Fish", "Void-touched delicacy that phases reality", ItemCategory.FOOD, 25, 800)

    // Herbs & Plants
    val DAISY = Item("daisy", "Daisy", "A simple white flower", ItemCategory.FLOWER, 50, 2)
    val ROSE = Item("rose", "Rose", "A beautiful red rose", ItemCategory.FLOWER, 50, 8)
    val LILY = Item("lily", "Lily", "An elegant white lily", ItemCategory.FLOWER, 50, 15)
    val ORCHID = Item("orchid", "Orchid", "A rare exotic orchid", ItemCategory.FLOWER, 50, 35)
    val LOTUS = Item("lotus", "Lotus", "A mystical lotus blossom", ItemCategory.FLOWER, 50, 60)
    val MOONFLOWER = Item("moonflower", "Moonflower", "A flower that blooms only at night", ItemCategory.FLOWER, 50, 120)
    val STARBLOOM = Item("starbloom", "Starbloom", "A celestial flower that glows", ItemCategory.FLOWER, 50, 250)

    val NETTLE = Item("nettle", "Nettle", "A stinging herb with healing properties", ItemCategory.HERB, 100, 5)
    val SAGE = Item("sage", "Sage", "A wise herb used in rituals", ItemCategory.HERB, 100, 12)
    val GINSENG = Item("ginseng", "Ginseng", "A powerful healing root", ItemCategory.HERB, 100, 25)
    val NIGHTSHADE = Item("nightshade", "Nightshade", "A dangerous poisonous plant", ItemCategory.HERB, 100, 40)
    val DRAGON_HERB = Item("dragon_herb", "Dragon Herb", "A rare herb that grows near dragons", ItemCategory.HERB, 100, 80)
    val VOID_MOSS = Item("void_moss", "Void Moss", "Moss that grows in the void realm", ItemCategory.HERB, 100, 150)
    val LIFE_BLOSSOM = Item("life_blossom", "Life Blossom", "A flower that radiates life energy", ItemCategory.HERB, 100, 300)

    // Seeds
    val WHEAT_SEED = Item("wheat_seed", "Wheat Seed", "Seeds for growing wheat", ItemCategory.SEED, 200, 3)
    val CARROT_SEED = Item("carrot_seed", "Carrot Seed", "Seeds for growing carrots", ItemCategory.SEED, 200, 5)
    val POTATO_SEED = Item("potato_seed", "Potato Seed", "Seeds for growing potatoes", ItemCategory.SEED, 200, 4)
    val TOMATO_SEED = Item("tomato_seed", "Tomato Seed", "Seeds for growing tomatoes", ItemCategory.SEED, 200, 8)
    val CORN_SEED = Item("corn_seed", "Corn Seed", "Seeds for growing corn", ItemCategory.SEED, 200, 12)
    val MAGIC_BEAN = Item("magic_bean", "Magic Bean", "A seed infused with magical energy", ItemCategory.SEED, 200, 50)
    val WORLD_SEED = Item("world_seed", "World Seed", "A seed that could grow into a world tree", ItemCategory.SEED, 200, 500)

    // Gems & Crystals
    val QUARTZ = Item("quartz", "Quartz", "A clear crystal with weak magical properties", ItemCategory.GEM, 50, 20)
    val AMETHYST = Item("amethyst", "Amethyst", "A purple gem that enhances magic", ItemCategory.GEM, 50, 45)
    val EMERALD = Item("emerald", "Emerald", "A green gem of nature's power", ItemCategory.GEM, 50, 85)
    val RUBY = Item("ruby", "Ruby", "A red gem that burns with inner fire", ItemCategory.GEM, 50, 120)
    val SAPPHIRE = Item("sapphire", "Sapphire", "A blue gem as deep as the ocean", ItemCategory.GEM, 50, 150)
    val DIAMOND = Item("diamond", "Diamond", "The hardest and most brilliant gem", ItemCategory.GEM, 50, 300)
    val BLACK_OPAL = Item("black_opal", "Black Opal", "A rare opal with shifting colors", ItemCategory.GEM, 50, 500)
    val STAR_STONE = Item("star_stone", "Star Stone", "A gem that contains a trapped star", ItemCategory.GEM, 50, 1000)
    val VOID_CRYSTAL = Item("void_crystal", "Void Crystal", "A crystal from the void dimension", ItemCategory.GEM, 50, 2000)

    // Potions & Consumables
    val MINOR_HEALTH_POTION = Item("minor_health_potion", "Minor Health Potion", "Restores a small amount of health", ItemCategory.POTION, 20, 25)
    val HEALTH_POTION = Item("health_potion", "Health Potion", "Restores moderate health", ItemCategory.POTION, 20, 50)
    val GREATER_HEALTH_POTION = Item("greater_health_potion", "Greater Health Potion", "Restores significant health", ItemCategory.POTION, 20, 100)
    val SUPER_HEALTH_POTION = Item("super_health_potion", "Super Health Potion", "Restores maximum health", ItemCategory.POTION, 20, 200)
    val STRENGTH_POTION = Item("strength_potion", "Strength Potion", "Temporarily increases attack power", ItemCategory.POTION, 20, 80)
    val DEFENSE_POTION = Item("defense_potion", "Defense Potion", "Temporarily increases defense", ItemCategory.POTION, 20, 80)
    val SPEED_POTION = Item("speed_potion", "Speed Potion", "Temporarily increases speed", ItemCategory.POTION, 20, 80)
    val MAGIC_POTION = Item("magic_potion", "Magic Potion", "Restores magical energy", ItemCategory.POTION, 20, 60)
    val LUCK_POTION = Item("luck_potion", "Luck Potion", "Increases drop rates temporarily", ItemCategory.POTION, 20, 150)
    val EXPERIENCE_POTION = Item("experience_potion", "Experience Potion", "Doubles XP gain for a short time", ItemCategory.POTION, 20, 300)

    // Scrolls & Tomes
    val SCROLL_OF_TELEPORT = Item("scroll_of_teleport", "Scroll of Teleport", "Instantly travel to a known location", ItemCategory.SCROLL, 10, 40)
    val SCROLL_OF_IDENTIFY = Item("scroll_of_identify", "Scroll of Identify", "Reveals the properties of an item", ItemCategory.SCROLL, 10, 30)
    val SCROLL_OF_ENCHANTING = Item("scroll_of_enchanting", "Scroll of Enchanting", "Enhances weapon or armor", ItemCategory.SCROLL, 10, 100)
    val SCROLL_OF_SUMMONING = Item("scroll_of_summoning", "Scroll of Summoning", "Calls forth a creature to aid you", ItemCategory.SCROLL, 10, 200)
    val TOME_OF_KNOWLEDGE = Item("tome_of_knowledge", "Tome of Knowledge", "Contains ancient wisdom", ItemCategory.TOME, 5, 500)
    val TOME_OF_POWER = Item("tome_of_power", "Tome of Power", "Grants temporary immense power", ItemCategory.TOME, 5, 1000)
    val GRIMOIRE = Item("grimoire", "Grimoire", "A book of dark magic spells", ItemCategory.TOME, 5, 2000)

    // Keys & Treasures
    val BRONZE_KEY = Item("bronze_key", "Bronze Key", "Opens basic locked doors", ItemCategory.KEY, 1, 15)
    val SILVER_KEY = Item("silver_key", "Silver Key", "Opens intermediate locks", ItemCategory.KEY, 1, 50)
    val GOLDEN_KEY = Item("golden_key", "Golden Key", "Opens precious treasure chests", ItemCategory.KEY, 1, 150)
    val SKELETON_KEY = Item("skeleton_key", "Skeleton Key", "Opens any lock", ItemCategory.KEY, 1, 500)
    val MASTER_KEY = Item("master_key", "Master Key", "The key that opens all doors", ItemCategory.KEY, 1, 2000)

    val TREASURE_CHEST = Item("treasure_chest", "Treasure Chest", "Contains valuable loot", ItemCategory.TREASURE, 1, 200)
    val GOLDEN_TREASURE_CHEST = Item("golden_treasure_chest", "Golden Treasure Chest", "Contains rare treasures", ItemCategory.TREASURE, 1, 800)
    val LEGENDARY_TREASURE_CHEST = Item("legendary_treasure_chest", "Legendary Treasure Chest", "Contains legendary items", ItemCategory.TREASURE, 1, 3000)

    // Dungeon-specific drops
    val CURSED_BONE = Item("cursed_bone", "Cursed Bone", "A bone tainted with dark magic", ItemCategory.BONE_ITEM, 30, 80)
    val ANCIENT_RUNE = Item("ancient_rune", "Ancient Rune", "A rune inscribed with forgotten power", ItemCategory.RUNE, 20, 200)
    val SHADOW_ESSENCE = Item("shadow_essence", "Shadow Essence", "Pure essence of darkness", ItemCategory.ESSENCE, 10, 400)
    val LIGHT_ESSENCE = Item("light_essence", "Light Essence", "Pure essence of light", ItemCategory.ESSENCE, 10, 400)
    val FIRE_ESSENCE = Item("fire_essence", "Fire Essence", "Concentrated fire energy", ItemCategory.ESSENCE, 10, 350)
    val ICE_ESSENCE = Item("ice_essence", "Ice Essence", "Frozen essence of winter", ItemCategory.ESSENCE, 10, 350)
    val EARTH_ESSENCE = Item("earth_essence", "Earth Essence", "Essence of the living earth", ItemCategory.ESSENCE, 10, 350)
    val AIR_ESSENCE = Item("air_essence", "Air Essence", "Essence of the eternal winds", ItemCategory.ESSENCE, 10, 350)

    // Souls & Fragments
    val LOST_SOUL = Item("lost_soul", "Lost Soul", "The soul of a wandering spirit", ItemCategory.SOUL, 5, 600)
    val CORRUPTED_SOUL = Item("corrupted_soul", "Corrupted Soul", "A soul tainted by evil", ItemCategory.SOUL, 5, 800)
    val PURE_SOUL = Item("pure_soul", "Pure Soul", "An untainted, pure soul", ItemCategory.SOUL, 5, 1200)
    val ANCIENT_SOUL = Item("ancient_soul", "Ancient Soul", "The soul of an ancient being", ItemCategory.SOUL, 5, 2000)

    val REALITY_FRAGMENT = Item("reality_fragment", "Reality Fragment", "A piece of broken reality", ItemCategory.FRAGMENT, 3, 1500)
    val TIME_FRAGMENT = Item("time_fragment", "Time Fragment", "A shard of frozen time", ItemCategory.FRAGMENT, 3, 1800)
    val SPACE_FRAGMENT = Item("space_fragment", "Space Fragment", "A fragment of twisted space", ItemCategory.FRAGMENT, 3, 2200)

    // Dusts & Components
    val FAIRY_DUST = Item("fairy_dust", "Fairy Dust", "Magical dust from fairy wings", ItemCategory.DUST, 100, 25)
    val STAR_DUST = Item("star_dust", "Star Dust", "Dust from a fallen star", ItemCategory.DUST, 100, 150)
    val VOID_DUST = Item("void_dust", "Void Dust", "Dust from the void realm", ItemCategory.DUST, 100, 400)
    val PHOENIX_DUST = Item("phoenix_dust", "Phoenix Dust", "Dust from phoenix feathers", ItemCategory.DUST, 100, 800)

    val GEAR_COMPONENT = Item("gear_component", "Gear Component", "A mechanical gear", ItemCategory.COMPONENT, 50, 30)
    val SPRING_COMPONENT = Item("spring_component", "Spring Component", "A coiled spring mechanism", ItemCategory.COMPONENT, 50, 35)
    val CRYSTAL_COMPONENT = Item("crystal_component", "Crystal Component", "A crystalline component", ItemCategory.COMPONENT, 50, 80)
    val MAGIC_CIRCUIT = Item("magic_circuit", "Magic Circuit", "A circuit that conducts magic", ItemCategory.COMPONENT, 50, 120)

    // Orbs & Catalysts
    val ORB_OF_LIGHT = Item("orb_of_light", "Orb of Light", "An orb radiating pure light", ItemCategory.ORB, 5, 800)
    val ORB_OF_DARKNESS = Item("orb_of_darkness", "Orb of Darkness", "An orb consuming all light", ItemCategory.ORB, 5, 800)
    val ORB_OF_POWER = Item("orb_of_power", "Orb of Power", "An orb crackling with energy", ItemCategory.ORB, 5, 1200)
    val ORB_OF_WISDOM = Item("orb_of_wisdom", "Orb of Wisdom", "An orb containing ancient knowledge", ItemCategory.ORB, 5, 1500)

    val TRANSMUTATION_CATALYST = Item("transmutation_catalyst", "Transmutation Catalyst", "Enables material transformation", ItemCategory.CATALYST, 10, 600)
    val ENHANCEMENT_CATALYST = Item("enhancement_catalyst", "Enhancement Catalyst", "Enhances item properties", ItemCategory.CATALYST, 10, 800)
    val FUSION_CATALYST = Item("fusion_catalyst", "Fusion Catalyst", "Allows item fusion", ItemCategory.CATALYST, 10, 1200)

    // Advanced Tools & Artifacts
    val BRONZE_PICKAXE = Item("bronze_pickaxe", "Bronze Pickaxe", "A basic mining tool", ItemCategory.TOOL, 1, 120)
    val IRON_PICKAXE = Item("iron_pickaxe", "Iron Pickaxe", "An improved mining tool", ItemCategory.TOOL, 1, 250)
    val STEEL_PICKAXE = Item("steel_pickaxe", "Steel Pickaxe", "A professional mining tool", ItemCategory.TOOL, 1, 500)
    val MITHRIL_PICKAXE = Item("mithril_pickaxe", "Mithril Pickaxe", "A superior mining tool", ItemCategory.TOOL, 1, 1200)
    val DRAGON_PICKAXE = Item("dragon_pickaxe", "Dragon Pickaxe", "The ultimate mining tool", ItemCategory.TOOL, 1, 3000)

    val BRONZE_HATCHET = Item("bronze_hatchet", "Bronze Hatchet", "A basic woodcutting tool", ItemCategory.TOOL, 1, 100)
    val IRON_HATCHET = Item("iron_hatchet", "Iron Hatchet", "An improved woodcutting tool", ItemCategory.TOOL, 1, 220)
    val STEEL_HATCHET = Item("steel_hatchet", "Steel Hatchet", "A professional woodcutting tool", ItemCategory.TOOL, 1, 450)
    val MITHRIL_HATCHET = Item("mithril_hatchet", "Mithril Hatchet", "A superior woodcutting tool", ItemCategory.TOOL, 1, 1100)
    val DRAGON_HATCHET = Item("dragon_hatchet", "Dragon Hatchet", "The ultimate woodcutting tool", ItemCategory.TOOL, 1, 2800)

    val FISHING_NET = Item("fishing_net", "Fishing Net", "Catches multiple fish at once", ItemCategory.TOOL, 1, 80)
    val LOBSTER_POT = Item("lobster_pot", "Lobster Pot", "Catches sea creatures", ItemCategory.TOOL, 1, 150)
    val HARPOON = Item("harpoon", "Harpoon", "For catching large fish", ItemCategory.TOOL, 1, 300)
    val MAGIC_FISHING_ROD = Item("magic_fishing_rod", "Magic Fishing Rod", "Enchanted fishing tool", ItemCategory.TOOL, 1, 800)

    // Relics & Artifacts
    val ANCIENT_COIN = Item("ancient_coin", "Ancient Coin", "Currency from a lost civilization", ItemCategory.ARTIFACT, 20, 100)
    val GOLDEN_IDOL = Item("golden_idol", "Golden Idol", "A valuable religious artifact", ItemCategory.ARTIFACT, 5, 800)
    val CRYSTAL_SKULL = Item("crystal_skull", "Crystal Skull", "A mysterious crystal skull", ItemCategory.ARTIFACT, 3, 2000)
    val STAFF_OF_AGES = Item("staff_of_ages", "Staff of Ages", "An artifact that controls time", ItemCategory.ARTIFACT, 1, 10000)
    val CROWN_OF_KINGS = Item("crown_of_kings", "Crown of Kings", "The crown of ancient rulers", ItemCategory.ARTIFACT, 1, 15000)

    val RUNE_OF_POWER = Item("rune_of_power", "Rune of Power", "Increases magical abilities", ItemCategory.RUNE, 10, 400)
    val RUNE_OF_PROTECTION = Item("rune_of_protection", "Rune of Protection", "Provides magical defense", ItemCategory.RUNE, 10, 350)
    val RUNE_OF_SPEED = Item("rune_of_speed", "Rune of Speed", "Increases movement speed", ItemCategory.RUNE, 10, 300)
    val RUNE_OF_FORTUNE = Item("rune_of_fortune", "Rune of Fortune", "Increases luck in all endeavors", ItemCategory.RUNE, 10, 600)
    val MASTER_RUNE = Item("master_rune", "Master Rune", "The ultimate runic power", ItemCategory.RUNE, 5, 2000)

    // More Food Items
    val BREAD = Item("bread", "Bread", "Basic food made from wheat", ItemCategory.FOOD, 50, 8)
    val CHEESE = Item("cheese", "Cheese", "Nutritious dairy product", ItemCategory.FOOD, 30, 15)
    val APPLE = Item("apple", "Apple", "A crisp, healthy fruit", ItemCategory.FOOD, 100, 3)
    val ORANGE = Item("orange", "Orange", "A juicy citrus fruit", ItemCategory.FOOD, 100, 4)
    val MEAT_PIE = Item("meat_pie", "Meat Pie", "A hearty meat-filled pastry", ItemCategory.FOOD, 20, 25)
    val VEGETABLE_STEW = Item("vegetable_stew", "Vegetable Stew", "A nutritious vegetable mixture", ItemCategory.FOOD, 25, 20)
    val ROASTED_CHICKEN = Item("roasted_chicken", "Roasted Chicken", "Perfectly cooked poultry", ItemCategory.FOOD, 15, 35)
    val BEEF_ROAST = Item("beef_roast", "Beef Roast", "A large cut of roasted beef", ItemCategory.FOOD, 10, 50)
    val DRAGON_STEAK = Item("dragon_steak", "Dragon Steak", "The ultimate meal for warriors", ItemCategory.FOOD, 5, 200)

    // Jewelry & Accessories
    val COPPER_RING = Item("copper_ring", "Copper Ring", "A simple copper band", ItemCategory.ARMOR, 1, 25)
    val SILVER_RING = Item("silver_ring", "Silver Ring", "An elegant silver ring", ItemCategory.ARMOR, 1, 80)
    val GOLD_RING = Item("gold_ring", "Gold Ring", "A precious gold ring", ItemCategory.ARMOR, 1, 200)
    val PLATINUM_RING = Item("platinum_ring", "Platinum Ring", "A rare platinum ring", ItemCategory.ARMOR, 1, 500)
    val DIAMOND_RING = Item("diamond_ring", "Diamond Ring", "A ring set with a brilliant diamond", ItemCategory.ARMOR, 1, 1500)

    val LEATHER_NECKLACE = Item("leather_necklace", "Leather Necklace", "A simple leather cord", ItemCategory.ARMOR, 1, 15)
    val SILVER_NECKLACE = Item("silver_necklace", "Silver Necklace", "A delicate silver chain", ItemCategory.ARMOR, 1, 120)
    val GOLD_NECKLACE = Item("gold_necklace", "Gold Necklace", "A beautiful gold necklace", ItemCategory.ARMOR, 1, 300)
    val AMULET_OF_STRENGTH = Item("amulet_of_strength", "Amulet of Strength", "Increases physical power", ItemCategory.ARMOR, 1, 800)
    val AMULET_OF_MAGIC = Item("amulet_of_magic", "Amulet of Magic", "Enhances magical abilities", ItemCategory.ARMOR, 1, 800)
    val AMULET_OF_LIFE = Item("amulet_of_life", "Amulet of Life", "Increases maximum health", ItemCategory.ARMOR, 1, 1200)

    // Special Dungeon Items
    val DUNGEON_MAP = Item("dungeon_map", "Dungeon Map", "Reveals dungeon layout", ItemCategory.SCROLL, 5, 100)
    val COMPASS = Item("compass", "Compass", "Always points toward treasure", ItemCategory.TOOL, 1, 200)
    val LOCKPICK = Item("lockpick", "Lockpick", "Opens locked chests", ItemCategory.TOOL, 20, 50)
    val TORCH = Item("torch", "Torch", "Lights up dark areas", ItemCategory.TOOL, 50, 5)
    val ROPE = Item("rope", "Rope", "Useful for climbing and binding", ItemCategory.TOOL, 10, 15)

    // Crafting Materials from Enemies
    val RABBIT_FUR = Item("rabbit_fur", "Rabbit Fur", "Soft fur from a rabbit", ItemCategory.MISC, 100, 4)
    val RABBIT_FOOT = Item("rabbit_foot", "Rabbit Foot", "A lucky rabbit's foot", ItemCategory.MISC, 50, 8)
    val BIRD_FEATHER = Item("bird_feather", "Bird Feather", "A common bird feather", ItemCategory.MISC, 200, 2)
    val EAGLE_TALON = Item("eagle_talon", "Eagle Talon", "A sharp eagle talon", ItemCategory.MISC, 30, 25)
    val CHICKEN_FEATHER = Item("chicken_feather", "Chicken Feather", "Feathers for fletching", ItemCategory.MISC, 150, 3)
    val COW_HIDE = Item("cow_hide", "Cow Hide", "Raw leather from cattle", ItemCategory.MISC, 50, 15)
    val SHEEP_WOOL = Item("sheep_wool", "Sheep Wool", "Soft wool for weaving", ItemCategory.MISC, 100, 6)
    val PIG_SKIN = Item("pig_skin", "Pig Skin", "Tough pig leather", ItemCategory.MISC, 80, 10)
    val DEER_HIDE = Item("deer_hide", "Deer Hide", "Quality deer leather", ItemCategory.MISC, 40, 20)
    val DEER_ANTLER = Item("deer_antler", "Deer Antler", "Strong antler for crafting", ItemCategory.MISC, 20, 35)
    val SNAKE_SKIN = Item("snake_skin", "Snake Skin", "Flexible snake leather", ItemCategory.MISC, 60, 18)
    val CROCODILE_LEATHER = Item("crocodile_leather", "Crocodile Leather", "Tough, scaled leather", ItemCategory.MISC, 30, 45)
    val TURTLE_SHELL = Item("turtle_shell", "Turtle Shell", "A hard protective shell", ItemCategory.MISC, 20, 60)
    val CRAB_SHELL = Item("crab_shell", "Crab Shell", "Chitinous crab armor", ItemCategory.MISC, 40, 25)
    val SCORPION_CHITIN = Item("scorpion_chitin", "Scorpion Chitin", "Hard exoskeleton material", ItemCategory.MISC, 35, 40)
    val BEETLE_CARAPACE = Item("beetle_carapace", "Beetle Carapace", "Shiny beetle shell", ItemCategory.MISC, 50, 30)
    val BAT_LEATHER = Item("bat_leather", "Bat Leather", "Thin wing membrane", ItemCategory.MISC, 80, 12)
    val LION_HIDE = Item("lion_hide", "Lion Hide", "Majestic lion leather", ItemCategory.MISC, 15, 80)
    val TIGER_PELT = Item("tiger_pelt", "Tiger Pelt", "Striped tiger fur", ItemCategory.MISC, 15, 90)
    val LEOPARD_SKIN = Item("leopard_skin", "Leopard Skin", "Spotted leopard hide", ItemCategory.MISC, 20, 70)
    val ELEPHANT_IVORY = Item("elephant_ivory", "Elephant Ivory", "Valuable ivory tusk", ItemCategory.MISC, 10, 200)
    val RHINO_HORN = Item("rhino_horn", "Rhino Horn", "A powerful rhino horn", ItemCategory.MISC, 8, 250)
    val HIPPOPOTAMUS_HIDE = Item("hippopotamus_hide", "Hippopotamus Hide", "Extremely thick hide", ItemCategory.MISC, 12, 100)
    val WHALE_BLUBBER = Item("whale_blubber", "Whale Blubber", "Oily whale fat", ItemCategory.MISC, 20, 150)
    val SHARK_SKIN = Item("shark_skin", "Shark Skin", "Rough sandpaper-like skin", ItemCategory.MISC, 25, 65)
    val OCTOPUS_INK = Item("octopus_ink", "Octopus Ink", "Black ink for writing", ItemCategory.MISC, 50, 20)
    val JELLYFISH_TENTACLE = Item("jellyfish_tentacle", "Jellyfish Tentacle", "Stinging tentacle", ItemCategory.MISC, 40, 15)
    val GIANT_PEARL = Item("giant_pearl", "Giant Pearl", "A massive pearl from an oyster", ItemCategory.GEM, 5, 500)
    val SILK_THREAD = Item("silk_thread", "Silk Thread", "Fine silk from spiders", ItemCategory.MISC, 100, 25)
    val CHITIN_PLATE = Item("chitin_plate", "Chitin Plate", "Hardened insect armor", ItemCategory.MISC, 60, 35)
    val MONSTER_SINEW = Item("monster_sinew", "Monster Sinew", "Strong tendon for bowstrings", ItemCategory.MISC, 80, 30)
    val BEAST_CLAW = Item("beast_claw", "Beast Claw", "Sharp claw from a beast", ItemCategory.MISC, 100, 22)
    val THICK_FUR = Item("thick_fur", "Thick Fur", "Warm, thick animal fur", ItemCategory.MISC, 70, 28)
    val FINE_LEATHER = Item("fine_leather", "Fine Leather", "High-quality processed leather", ItemCategory.MISC, 50, 50)
    val RUGGED_LEATHER = Item("rugged_leather", "Rugged Leather", "Tough, weathered leather", ItemCategory.MISC, 60, 40)
    val PRISTINE_FEATHER = Item("pristine_feather", "Pristine Feather", "A perfect feather for fletching", ItemCategory.MISC, 80, 15)
    val VENOM_SAC = Item("venom_sac", "Venom Sac", "Poison gland from venomous creatures", ItemCategory.MISC, 30, 55)
    val ACID_GLAND = Item("acid_gland", "Acid Gland", "Corrosive acid producer", ItemCategory.MISC, 25, 65)
    val FROST_GLAND = Item("frost_gland", "Frost Gland", "Freezing organ from ice creatures", ItemCategory.MISC, 20, 75)
    val FIRE_GLAND = Item("fire_gland", "Fire Gland", "Flame-producing organ", ItemCategory.MISC, 20, 75)
    val ELECTRIC_ORGAN = Item("electric_organ", "Electric Organ", "Bioelectric generator", ItemCategory.MISC, 15, 85)

    // Smithing outputs
    val BRONZE_BAR = Item("bronze_bar", "Bronze Bar", "A smelted bar of bronze", ItemCategory.MISC, 100, 80)
    val IRON_BAR = Item("iron_bar", "Iron Bar", "A smelted bar of iron", ItemCategory.MISC, 100, 120)
    val STEEL_BAR = Item("steel_bar", "Steel Bar", "A smelted bar of steel", ItemCategory.MISC, 100, 220)
    val SILVER_BAR = Item("silver_bar", "Silver Bar", "A smelted bar of silver", ItemCategory.MISC, 100, 180)
    val GOLD_BAR = Item("gold_bar", "Gold Bar", "A smelted bar of gold", ItemCategory.MISC, 100, 300)
    val MITHRIL_BAR = Item("mithril_bar", "Mithril Bar", "A smelted bar of mithril", ItemCategory.MISC, 100, 500)
    val ADAMANT_BAR = Item("adamant_bar", "Adamant Bar", "A smelted bar of adamant", ItemCategory.MISC, 100, 900)

    // Ultra tier bars
    val RUNITE_BAR = Item("runite_bar", "Runite Bar", "A bar of legendary runite metal", ItemCategory.MISC, 100, 1500)
    val DRAGONITE_BAR = Item("dragonite_bar", "Dragonite Bar", "A bar forged from dragon essence", ItemCategory.MISC, 100, 2500)
    val CELESTIAL_BAR = Item("celestial_bar", "Celestial Bar", "A divine metal from the heavens", ItemCategory.MISC, 100, 4000)
    val VOID_BAR = Item("void_bar", "Void Bar", "A bar that exists between dimensions", ItemCategory.MISC, 100, 6500)
    val ETHEREAL_BAR = Item("ethereal_bar", "Ethereal Bar", "A ghostly metal that phases through reality", ItemCategory.MISC, 100, 10000)
    
    // Combat-related items
    val RAW_MEAT = Item("raw_meat", "Raw Meat", "Fresh meat from a defeated enemy", ItemCategory.COMBAT_DROP, 50, 8)
    val RAT_TAIL = Item("rat_tail", "Rat Tail", "A trophy from a giant rat", ItemCategory.COMBAT_DROP, 20, 5)
    val GOBLIN_EAR = Item("goblin_ear", "Goblin Ear", "A goblin's pointed ear", ItemCategory.COMBAT_DROP, 20, 12)
    val WOLF_PELT = Item("wolf_pelt", "Wolf Pelt", "A thick wolf pelt", ItemCategory.COMBAT_DROP, 20, 18)
    val BANDIT_MASK = Item("bandit_mask", "Bandit Mask", "A mask worn by bandits", ItemCategory.COMBAT_DROP, 10, 20)
    val ORC_TOOTH = Item("orc_tooth", "Orc Tooth", "A sharp orc tooth", ItemCategory.COMBAT_DROP, 20, 25)
    val ZOMBIE_FRAGMENT = Item("zombie_fragment", "Zombie Fragment", "An eerie fragment of undead", ItemCategory.COMBAT_DROP, 30, 22)
    val VAMPIRE_FANG = Item("vampire_fang", "Vampire Fang", "A sharp vampire fang", ItemCategory.COMBAT_DROP, 10, 60)
    val GOLEM_CORE = Item("golem_core", "Golem Core", "A humming core from a golem", ItemCategory.COMBAT_DROP, 5, 120)
    val WYVERN_SCALE = Item("wyvern_scale", "Wyvern Scale", "A hardened wyvern scale", ItemCategory.COMBAT_DROP, 8, 180)
    val BONE = Item("bone", "Bone", "A bone from an undead creature", ItemCategory.COMBAT_DROP, 50, 15)
    val DRAGON_SCALE = Item("dragon_scale", "Dragon Scale", "A shimmering dragon scale", ItemCategory.COMBAT_DROP, 10, 500)
    val DRAGON_BONE = Item("dragon_bone", "Dragon Bone", "A powerful dragon bone", ItemCategory.COMBAT_DROP, 5, 1000)

    // Expanded combat drops
    val SPIDER_SILK = Item("spider_silk", "Spider Silk", "Fine silk from giant spiders", ItemCategory.COMBAT_DROP, 30, 35)
    val BEAR_CLAW = Item("bear_claw", "Bear Claw", "A massive claw from a cave bear", ItemCategory.COMBAT_DROP, 15, 45)
    val TROLL_HIDE = Item("troll_hide", "Troll Hide", "Thick hide from a mountain troll", ItemCategory.COMBAT_DROP, 10, 55)
    val HARPY_FEATHER = Item("harpy_feather", "Harpy Feather", "A mystical feather that grants flight", ItemCategory.COMBAT_DROP, 20, 75)
    val DEMON_HORN = Item("demon_horn", "Demon Horn", "A curved horn from a lesser demon", ItemCategory.COMBAT_DROP, 8, 120)
    val ANGEL_WING = Item("angel_wing", "Angel Wing", "A pure white wing feather", ItemCategory.COMBAT_DROP, 5, 200)
    val LICH_PHYLACTERY = Item("lich_phylactery", "Lich Phylactery", "The soul vessel of an undead lich", ItemCategory.COMBAT_DROP, 3, 800)
    val TITAN_ESSENCE = Item("titan_essence", "Titan Essence", "Raw power from an ancient titan", ItemCategory.COMBAT_DROP, 2, 1500)
    val VOID_SHARD = Item("void_shard", "Void Shard", "A fragment of pure nothingness", ItemCategory.COMBAT_DROP, 1, 3000)
    
    // Weapons - Swords
    val BRONZE_SWORD = Item("bronze_sword", "Bronze Sword", "A basic bronze sword", ItemCategory.WEAPON, 1, 100)
    val IRON_SWORD = Item("iron_sword", "Iron Sword", "A sturdy iron sword", ItemCategory.WEAPON, 1, 250)
    val STEEL_SWORD = Item("steel_sword", "Steel Sword", "A sharp steel blade", ItemCategory.WEAPON, 1, 500)
    val MITHRIL_SWORD = Item("mithril_sword", "Mithril Sword", "A gleaming mithril blade", ItemCategory.WEAPON, 1, 1200)
    val ADAMANT_SWORD = Item("adamant_sword", "Adamant Sword", "An exceptionally strong blade", ItemCategory.WEAPON, 1, 2500)
    val DRAGON_SWORD = Item("dragon_sword", "Dragon Sword", "A legendary dragon forged sword", ItemCategory.WEAPON, 1, 5000)
    val RUNITE_SWORD = Item("runite_sword", "Runite Sword", "A blade of pure runic power", ItemCategory.WEAPON, 1, 8000)
    val DRAGONITE_SWORD = Item("dragonite_sword", "Dragonite Sword", "A sword infused with dragon essence", ItemCategory.WEAPON, 1, 12000)
    val CELESTIAL_SWORD = Item("celestial_sword", "Celestial Sword", "A divine blade from the heavens", ItemCategory.WEAPON, 1, 18000)
    val VOID_SWORD = Item("void_sword", "Void Sword", "A blade that cuts through reality itself", ItemCategory.WEAPON, 1, 25000)
    val ETHEREAL_SWORD = Item("ethereal_sword", "Ethereal Sword", "A ghostly blade that phases between worlds", ItemCategory.WEAPON, 1, 35000)

    // Weapons - Axes
    val BRONZE_AXE = Item("bronze_axe", "Bronze Axe", "A heavy bronze axe", ItemCategory.WEAPON, 1, 150)
    val IRON_AXE = Item("iron_axe", "Iron Axe", "A devastating iron axe", ItemCategory.WEAPON, 1, 350)
    val STEEL_AXE = Item("steel_axe", "Steel Axe", "A sharp steel axe", ItemCategory.WEAPON, 1, 600)
    val DRAGON_AXE = Item("dragon_axe", "Dragon Axe", "A mythical dragon axe", ItemCategory.WEAPON, 1, 5200)
    val RUNITE_AXE = Item("runite_axe", "Runite Axe", "An axe that cleaves through armor", ItemCategory.WEAPON, 1, 8500)
    val DRAGONITE_AXE = Item("dragonite_axe", "Dragonite Axe", "An axe forged from dragon essence", ItemCategory.WEAPON, 1, 13000)
    val CELESTIAL_AXE = Item("celestial_axe", "Celestial Axe", "A divine axe blessed by the gods", ItemCategory.WEAPON, 1, 19000)
    val VOID_AXE = Item("void_axe", "Void Axe", "An axe that devours matter itself", ItemCategory.WEAPON, 1, 27000)

    // Weapons - Bows
    val OAK_BOW = Item("oak_bow", "Oak Bow", "A simple wooden bow", ItemCategory.WEAPON, 1, 120)
    val WILLOW_BOW = Item("willow_bow", "Willow Bow", "A flexible willow bow", ItemCategory.WEAPON, 1, 200)
    val MAPLE_BOW = Item("maple_bow", "Maple Bow", "A sturdy maple bow", ItemCategory.WEAPON, 1, 350)
    val YEW_BOW = Item("yew_bow", "Yew Bow", "A powerful yew longbow", ItemCategory.WEAPON, 1, 800)
    val MAGIC_BOW = Item("magic_bow", "Magic Bow", "A bow infused with magical energy", ItemCategory.WEAPON, 1, 1500)
    val ELVEN_BOW = Item("elven_bow", "Elven Bow", "A masterwork bow crafted by elves", ItemCategory.WEAPON, 1, 3000)
    val DRAGON_BOW = Item("dragon_bow", "Dragon Bow", "A bow made from dragonbone", ItemCategory.WEAPON, 1, 6000)
    val SHADOW_BOW = Item("shadow_bow", "Shadow Bow", "A bow that fires arrows of pure darkness", ItemCategory.WEAPON, 1, 12000)

    // Weapons - Staffs
    val APPRENTICE_STAFF = Item("apprentice_staff", "Apprentice Staff", "A basic staff for novice mages", ItemCategory.WEAPON, 1, 180)
    val ADEPT_STAFF = Item("adept_staff", "Adept Staff", "A staff for practicing mages", ItemCategory.WEAPON, 1, 400)
    val MASTER_STAFF = Item("master_staff", "Master Staff", "A staff wielded by master mages", ItemCategory.WEAPON, 1, 900)
    val ARCANE_STAFF = Item("arcane_staff", "Arcane Staff", "A staff infused with arcane power", ItemCategory.WEAPON, 1, 2000)
    val ELEMENTAL_STAFF = Item("elemental_staff", "Elemental Staff", "A staff that commands the elements", ItemCategory.WEAPON, 1, 4500)
    val VOID_STAFF = Item("void_staff", "Void Staff", "A staff that channels void magic", ItemCategory.WEAPON, 1, 10000)
    val COSMIC_STAFF = Item("cosmic_staff", "Cosmic Staff", "A staff that harnesses cosmic power", ItemCategory.WEAPON, 1, 20000)

    // Weapons - Daggers
    val BRONZE_DAGGER = Item("bronze_dagger", "Bronze Dagger", "A quick bronze blade", ItemCategory.WEAPON, 1, 80)
    val IRON_DAGGER = Item("iron_dagger", "Iron Dagger", "A sharp iron dagger", ItemCategory.WEAPON, 1, 180)
    val STEEL_DAGGER = Item("steel_dagger", "Steel Dagger", "A razor-sharp steel dagger", ItemCategory.WEAPON, 1, 350)
    val MITHRIL_DAGGER = Item("mithril_dagger", "Mithril Dagger", "A lightweight mithril dagger", ItemCategory.WEAPON, 1, 800)
    val POISON_DAGGER = Item("poison_dagger", "Poison Dagger", "A dagger coated with deadly poison", ItemCategory.WEAPON, 1, 1500)
    val SHADOW_DAGGER = Item("shadow_dagger", "Shadow Dagger", "A dagger that strikes from the shadows", ItemCategory.WEAPON, 1, 3500)
    val ASSASSIN_DAGGER = Item("assassin_dagger", "Assassin Dagger", "The perfect weapon for silent kills", ItemCategory.WEAPON, 1, 7500)
    
    // Armor - Leather Set
    val LEATHER_HELMET = Item("leather_helmet", "Leather Helmet", "Basic head protection", ItemCategory.ARMOR, 1, 50)
    val LEATHER_CHESTPLATE = Item("leather_chestplate", "Leather Chestplate", "Light torso armor", ItemCategory.ARMOR, 1, 80)
    val LEATHER_LEGGINGS = Item("leather_leggings", "Leather Leggings", "Flexible leg protection", ItemCategory.ARMOR, 1, 70)
    val LEATHER_BOOTS = Item("leather_boots", "Leather Boots", "Light foot protection", ItemCategory.ARMOR, 1, 60)
    val LEATHER_GLOVES = Item("leather_gloves", "Leather Gloves", "Basic hand protection", ItemCategory.ARMOR, 1, 45)

    // Armor - Iron Set
    val IRON_HELMET = Item("iron_helmet", "Iron Helmet", "Solid iron protection", ItemCategory.ARMOR, 1, 200)
    val IRON_CHESTPLATE = Item("iron_chestplate", "Iron Chestplate", "Heavy iron armor", ItemCategory.ARMOR, 1, 400)
    val IRON_LEGGINGS = Item("iron_leggings", "Iron Leggings", "Sturdy iron leg armor", ItemCategory.ARMOR, 1, 350)
    val IRON_BOOTS = Item("iron_boots", "Iron Boots", "Sturdy iron boots", ItemCategory.ARMOR, 1, 220)
    val IRON_GLOVES = Item("iron_gloves", "Iron Gloves", "Iron gauntlets", ItemCategory.ARMOR, 1, 180)
    val IRON_SHIELD = Item("iron_shield", "Iron Shield", "A solid iron shield", ItemCategory.ARMOR, 1, 300)

    // Armor - Steel Set
    val STEEL_HELMET = Item("steel_helmet", "Steel Helmet", "Strong steel headgear", ItemCategory.ARMOR, 1, 500)
    val STEEL_CHESTPLATE = Item("steel_chestplate", "Steel Chestplate", "Heavy steel armor", ItemCategory.ARMOR, 1, 800)
    val STEEL_LEGGINGS = Item("steel_leggings", "Steel Leggings", "Reinforced steel leg armor", ItemCategory.ARMOR, 1, 700)
    val STEEL_BOOTS = Item("steel_boots", "Steel Boots", "Heavy steel boots", ItemCategory.ARMOR, 1, 520)
    val STEEL_GLOVES = Item("steel_gloves", "Steel Gloves", "Steel-plated gauntlets", ItemCategory.ARMOR, 1, 450)
    val STEEL_SHIELD = Item("steel_shield", "Steel Shield", "A reinforced steel shield", ItemCategory.ARMOR, 1, 600)

    // Armor - Mithril Set
    val MITHRIL_HELMET = Item("mithril_helmet", "Mithril Helmet", "Lightweight mithril headgear", ItemCategory.ARMOR, 1, 1200)
    val MITHRIL_CHESTPLATE = Item("mithril_chestplate", "Mithril Chestplate", "Light but strong mithril armor", ItemCategory.ARMOR, 1, 2000)
    val MITHRIL_LEGGINGS = Item("mithril_leggings", "Mithril Leggings", "Flexible mithril leg armor", ItemCategory.ARMOR, 1, 1700)
    val MITHRIL_BOOTS = Item("mithril_boots", "Mithril Boots", "Light and strong boots", ItemCategory.ARMOR, 1, 1100)
    val MITHRIL_GLOVES = Item("mithril_gloves", "Mithril Gloves", "Elegant mithril gauntlets", ItemCategory.ARMOR, 1, 950)
    val MITHRIL_SHIELD = Item("mithril_shield", "Mithril Shield", "A lightweight mithril shield", ItemCategory.ARMOR, 1, 1500)

    // Armor - Adamant Set
    val ADAMANT_HELMET = Item("adamant_helmet", "Adamant Helmet", "Exceptionally strong headgear", ItemCategory.ARMOR, 1, 2500)
    val ADAMANT_CHESTPLATE = Item("adamant_chestplate", "Adamant Chestplate", "Nearly impenetrable armor", ItemCategory.ARMOR, 1, 4000)
    val ADAMANT_LEGGINGS = Item("adamant_leggings", "Adamant Leggings", "Adamant leg protection", ItemCategory.ARMOR, 1, 3500)
    val ADAMANT_BOOTS = Item("adamant_boots", "Adamant Boots", "Exceptionally sturdy boots", ItemCategory.ARMOR, 1, 2000)
    val ADAMANT_GLOVES = Item("adamant_gloves", "Adamant Gloves", "Adamant gauntlets", ItemCategory.ARMOR, 1, 2200)
    val ADAMANT_SHIELD = Item("adamant_shield", "Adamant Shield", "An adamant tower shield", ItemCategory.ARMOR, 1, 3000)

    // Armor - Dragon Set
    val DRAGON_HELMET = Item("dragon_helmet", "Dragon Helmet", "Helmet forged from dragon scales", ItemCategory.ARMOR, 1, 8000)
    val DRAGON_CHESTPLATE = Item("dragon_chestplate", "Dragon Chestplate", "Armor made from dragon hide", ItemCategory.ARMOR, 1, 15000)
    val DRAGON_LEGGINGS = Item("dragon_leggings", "Dragon Leggings", "Leg armor of dragon origin", ItemCategory.ARMOR, 1, 12000)
    val DRAGON_BOOTS = Item("dragon_boots", "Dragon Boots", "Boots crafted from dragon scales", ItemCategory.ARMOR, 1, 9000)
    val DRAGON_GLOVES = Item("dragon_gloves", "Dragon Gloves", "Gloves imbued with dragon power", ItemCategory.ARMOR, 1, 7500)
    val DRAGON_SHIELD = Item("dragon_shield", "Dragon Shield", "A shield forged from dragonbone", ItemCategory.ARMOR, 1, 10000)
    
    val ALL = listOf(
        // Ores
        COPPER_ORE, TIN_ORE, IRON_ORE, COAL_ORE, SILVER_ORE, GOLD_ORE, MITHRIL_ORE, ADAMANTITE_ORE,
        RUNITE_ORE, DRAGONITE_ORE, CELESTIAL_ORE, VOID_ORE, ETHEREAL_ORE,
        // Logs
        OAK_LOG, WILLOW_LOG, BIRCH_LOG, TEAK_LOG, MAPLE_LOG, YEW_LOG,
        REDWOOD_LOG, ELDERWOOD_LOG, IRONBARK_LOG, SHADOWBARK_LOG, WORLD_TREE_LOG,
        // Fish
        RAW_TROUT, RAW_SALMON, RAW_TUNA, RAW_SWORDFISH,
        RAW_SHARK, RAW_MANTA_RAY, RAW_KRAKEN_TENTACLE, RAW_LEVIATHAN, RAW_VOID_FISH,
        // Food
        COOKED_TROUT, COOKED_SALMON, COOKED_TUNA, COOKED_SWORDFISH,
        COOKED_SHARK, COOKED_MANTA_RAY, COOKED_KRAKEN_TENTACLE, COOKED_LEVIATHAN, COOKED_VOID_FISH,
        // Combat drops
        RAW_MEAT, RAT_TAIL, WOLF_PELT, GOBLIN_EAR, BANDIT_MASK, ORC_TOOTH, ZOMBIE_FRAGMENT, VAMPIRE_FANG, GOLEM_CORE, WYVERN_SCALE, BONE, DRAGON_SCALE, DRAGON_BONE,
        SPIDER_SILK, BEAR_CLAW, TROLL_HIDE, HARPY_FEATHER, DEMON_HORN, ANGEL_WING, LICH_PHYLACTERY, TITAN_ESSENCE, VOID_SHARD,
        // Swords
        BRONZE_SWORD, IRON_SWORD, STEEL_SWORD, MITHRIL_SWORD, ADAMANT_SWORD, DRAGON_SWORD,
        RUNITE_SWORD, DRAGONITE_SWORD, CELESTIAL_SWORD, VOID_SWORD, ETHEREAL_SWORD,
        // Axes
        BRONZE_AXE, IRON_AXE, STEEL_AXE, DRAGON_AXE, RUNITE_AXE, DRAGONITE_AXE, CELESTIAL_AXE, VOID_AXE,
        // Bows
        OAK_BOW, WILLOW_BOW, MAPLE_BOW, YEW_BOW, MAGIC_BOW, ELVEN_BOW, DRAGON_BOW, SHADOW_BOW,
        // Staffs
        APPRENTICE_STAFF, ADEPT_STAFF, MASTER_STAFF, ARCANE_STAFF, ELEMENTAL_STAFF, VOID_STAFF, COSMIC_STAFF,
        // Daggers
        BRONZE_DAGGER, IRON_DAGGER, STEEL_DAGGER, MITHRIL_DAGGER, POISON_DAGGER, SHADOW_DAGGER, ASSASSIN_DAGGER,
        // Armor
        LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, LEATHER_GLOVES,
        IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS, IRON_GLOVES, IRON_SHIELD,
        STEEL_HELMET, STEEL_CHESTPLATE, STEEL_LEGGINGS, STEEL_BOOTS, STEEL_GLOVES, STEEL_SHIELD,
        MITHRIL_HELMET, MITHRIL_CHESTPLATE, MITHRIL_LEGGINGS, MITHRIL_BOOTS, MITHRIL_GLOVES, MITHRIL_SHIELD,
        ADAMANT_HELMET, ADAMANT_CHESTPLATE, ADAMANT_LEGGINGS, ADAMANT_BOOTS, ADAMANT_GLOVES, ADAMANT_SHIELD,
        DRAGON_HELMET, DRAGON_CHESTPLATE, DRAGON_LEGGINGS, DRAGON_BOOTS, DRAGON_GLOVES, DRAGON_SHIELD,
        // Bars
        BRONZE_BAR, IRON_BAR, STEEL_BAR, SILVER_BAR, GOLD_BAR, MITHRIL_BAR, ADAMANT_BAR,
        RUNITE_BAR, DRAGONITE_BAR, CELESTIAL_BAR, VOID_BAR, ETHEREAL_BAR
    )
}
