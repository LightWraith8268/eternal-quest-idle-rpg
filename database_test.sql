-- Database Schema Validation Test
-- This represents what the Room database should generate

-- Players table (from Phase 1)
CREATE TABLE IF NOT EXISTS players (
    id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
    name TEXT NOT NULL DEFAULT 'Adventurer',
    createdAt INTEGER NOT NULL,
    lastActiveAt INTEGER NOT NULL,
    totalPlayTime INTEGER NOT NULL DEFAULT 0,
    currentSkill TEXT,
    currentActivity TEXT,
    activityStartTime INTEGER,
    activityProgress REAL NOT NULL DEFAULT 0
);

-- Skills table (from Phase 1)  
CREATE TABLE IF NOT EXISTS skills (
    name TEXT PRIMARY KEY NOT NULL,
    level INTEGER NOT NULL DEFAULT 1,
    experience INTEGER NOT NULL DEFAULT 0,
    prestigeCount INTEGER NOT NULL DEFAULT 0,
    isUnlocked INTEGER NOT NULL DEFAULT 0
);

-- Items table (from Phase 1, extended in Phase 2)
CREATE TABLE IF NOT EXISTS items (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    category TEXT NOT NULL, -- Now includes WEAPON, ARMOR, COMBAT_DROP
    stackSize INTEGER NOT NULL DEFAULT 1,
    value INTEGER NOT NULL DEFAULT 0,
    iconResource TEXT
);

-- Bank items table (from Phase 1)
CREATE TABLE IF NOT EXISTS bank_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    itemId TEXT NOT NULL,
    tabIndex INTEGER NOT NULL,
    slotIndex INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    FOREIGN KEY(itemId) REFERENCES items(id)
);

-- Activity states table (from Phase 1) 
CREATE TABLE IF NOT EXISTS activity_states (
    id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
    activeSkill TEXT,
    activityType TEXT,
    startTime INTEGER,
    progress REAL NOT NULL DEFAULT 0,
    targetItemId TEXT,
    isActive INTEGER NOT NULL DEFAULT 0
);

-- Combat stats table (NEW in Phase 2)
CREATE TABLE IF NOT EXISTS combat_stats (
    playerId INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
    hitpoints INTEGER NOT NULL DEFAULT 100,
    maxHitpoints INTEGER NOT NULL DEFAULT 100,
    attack INTEGER NOT NULL DEFAULT 1,
    strength INTEGER NOT NULL DEFAULT 1,
    defense INTEGER NOT NULL DEFAULT 1,
    combatLevel INTEGER NOT NULL DEFAULT 1,
    combatXp INTEGER NOT NULL DEFAULT 0,
    equippedWeapon TEXT,
    equippedArmor TEXT,
    autoEatEnabled INTEGER NOT NULL DEFAULT 1,
    autoEatFoodId TEXT,
    isInCombat INTEGER NOT NULL DEFAULT 0,
    currentEnemyId TEXT,
    combatStartTime INTEGER,
    lastPlayerAttack INTEGER,
    lastEnemyAttack INTEGER,
    FOREIGN KEY(equippedWeapon) REFERENCES items(id),
    FOREIGN KEY(equippedArmor) REFERENCES items(id),
    FOREIGN KEY(autoEatFoodId) REFERENCES items(id)
);

-- Current enemy table (NEW in Phase 2)
CREATE TABLE IF NOT EXISTS current_enemy (
    id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
    enemyId TEXT NOT NULL,
    currentHp INTEGER NOT NULL,
    maxHp INTEGER NOT NULL,
    nextAttackTime INTEGER NOT NULL,
    combatStartTime INTEGER NOT NULL
);

-- Test data insertion
INSERT OR REPLACE INTO players (id, name, createdAt, lastActiveAt) 
VALUES (1, 'Test Player', 1694444400000, 1694444400000);

INSERT OR REPLACE INTO combat_stats (playerId) VALUES (1);

-- Test initial skills
INSERT OR IGNORE INTO skills (name, isUnlocked) VALUES 
('mining', 1),
('woodcutting', 1), 
('fishing', 1),
('cooking', 0);

-- Test queries that the app would run
SELECT 'Testing player query:' as test;
SELECT * FROM players WHERE id = 1;

SELECT 'Testing combat stats query:' as test;  
SELECT * FROM combat_stats WHERE playerId = 1;

SELECT 'Testing skills query:' as test;
SELECT * FROM skills WHERE isUnlocked = 1;

SELECT 'Testing bank capacity:' as test;
SELECT COUNT(*) as used_slots FROM bank_items WHERE tabIndex = 0;

-- Test complex joins that would be used in game
SELECT 'Testing equipped weapon join:' as test;
SELECT cs.equippedWeapon, i.name, i.description 
FROM combat_stats cs 
LEFT JOIN items i ON cs.equippedWeapon = i.id 
WHERE cs.playerId = 1;

-- Validate data types and constraints
SELECT 'Schema validation complete' as result;