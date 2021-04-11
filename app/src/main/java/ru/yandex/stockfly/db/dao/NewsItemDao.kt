package ru.yandex.stockfly.db.dao

import androidx.room.*
import ru.yandex.stockfly.db.entity.NewsItemEntity

@Dao
interface NewsItemDao {

    @Query("SELECT * FROM news WHERE ticker = :ticker ORDER BY datetime DESC")
    suspend fun select(ticker: String): List<NewsItemEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(news: List<NewsItemEntity>)

    @Transaction
    suspend fun upsertAndSelect(ticker: String, news: List<NewsItemEntity>): List<NewsItemEntity> {
        upsert(news)
        return select(ticker)
    }
}