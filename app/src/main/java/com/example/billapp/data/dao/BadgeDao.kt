package com.example.billapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.billapp.data.models.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badges WHERE isUnlocked = 1")
    fun getUnlockedBadges(): Flow<List<BadgeEntity>>

    @Query("SELECT * FROM badges WHERE id = :badgeId")
    suspend fun getBadge(badgeId: String): BadgeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: BadgeEntity)

    @Update
    suspend fun updateBadge(badge: BadgeEntity)

    @Query("""
        UPDATE badges 
        SET currentProgress = :progress, 
            isUnlocked = CASE WHEN :progress >= maxProgress THEN 1 ELSE isUnlocked END,
            unlockedDate = CASE WHEN :progress >= maxProgress AND isUnlocked = 0 
                          THEN :timestamp ELSE unlockedDate END,
            lastUpdated = :timestamp
        WHERE id = :badgeId
    """)
    suspend fun updateProgress(
        badgeId: String,
        progress: Float,
        timestamp: Long = System.currentTimeMillis()
    )
}