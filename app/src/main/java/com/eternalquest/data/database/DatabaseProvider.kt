package com.eternalquest.data.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private val instances: MutableMap<Int, EternalQuestDatabase> = mutableMapOf()

    fun get(context: Context, profileId: Int): EternalQuestDatabase {
        synchronized(this) {
            instances[profileId]?.let { return it }
            val dbName = "eternal_quest_database_profile_${profileId}"
            val db = Room.databaseBuilder(
                context.applicationContext,
                EternalQuestDatabase::class.java,
                dbName
            )
            .addMigrations(
                EternalQuestDatabase.MIGRATION_1_2,
                EternalQuestDatabase.MIGRATION_2_3,
                EternalQuestDatabase.MIGRATION_3_4,
                EternalQuestDatabase.MIGRATION_4_5,
                EternalQuestDatabase.MIGRATION_5_6,
                EternalQuestDatabase.MIGRATION_6_7,
                EternalQuestDatabase.MIGRATION_7_8,
                EternalQuestDatabase.MIGRATION_8_9
            )
            .build()
            instances[profileId] = db
            return db
        }
    }
}
