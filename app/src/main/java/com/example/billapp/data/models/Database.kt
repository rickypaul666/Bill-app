package com.example.billapp.data.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.billapp.data.dao.AchievementDao
import com.example.billapp.data.dao.BadgeDao

@Database(
    entities = [AchievementEntity::class, BadgeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AchievementDatabase : RoomDatabase() {
    abstract fun achievementDao(): AchievementDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        @Volatile
        private var INSTANCE: AchievementDatabase? = null

        fun getDatabase(context: Context): AchievementDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AchievementDatabase::class.java,
                    "achievement_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}