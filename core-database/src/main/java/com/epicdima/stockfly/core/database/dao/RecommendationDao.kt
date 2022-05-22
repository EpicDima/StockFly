package com.epicdima.stockfly.core.database.dao

import androidx.room.*
import com.epicdima.stockfly.core.database.entity.RecommendationEntity

@Dao
interface RecommendationDao {

    @Query("SELECT * FROM recommendations WHERE ticker = :ticker ORDER BY period ASC")
    suspend fun select(ticker: String): List<RecommendationEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(recommendations: List<RecommendationEntity>)

    @Transaction
    suspend fun insertAndSelect(
        ticker: String,
        recommendations: List<RecommendationEntity>
    ): List<RecommendationEntity> {
        insert(recommendations)
        return select(ticker)
    }
}
