package ru.yandex.stockfly.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.yandex.stockfly.db.entity.RecommendationEntity

@Dao
interface RecommendationDao {

    @Query("SELECT * FROM recommendations WHERE ticker = :ticker ORDER BY period ASC")
    suspend fun select(ticker: String): List<RecommendationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(news: List<RecommendationEntity>)
}