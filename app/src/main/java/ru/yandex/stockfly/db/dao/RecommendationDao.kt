package ru.yandex.stockfly.db.dao

import androidx.room.*
import ru.yandex.stockfly.db.entity.RecommendationEntity

@Dao
interface RecommendationDao {

    @Query("SELECT * FROM recommendations WHERE ticker = :ticker ORDER BY period ASC")
    suspend fun select(ticker: String): List<RecommendationEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(recommendations: List<RecommendationEntity>)

    @Transaction
    suspend fun upsertAndSelect(
        ticker: String,
        recommendations: List<RecommendationEntity>
    ): List<RecommendationEntity> {
        upsert(recommendations)
        return select(ticker)
    }
}