package com.epicdima.stockfly.core.database.dao

import androidx.room.*
import com.epicdima.stockfly.core.database.entity.NewsItemEntity

@Dao
interface NewsItemDao {

    @Query("SELECT * FROM news WHERE ticker = :ticker ORDER BY datetime DESC")
    suspend fun select(ticker: String): List<NewsItemEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(news: List<NewsItemEntity>)

    @Transaction
    suspend fun insertAndSelect(ticker: String, news: List<NewsItemEntity>): List<NewsItemEntity> {
        insert(news)
        return select(ticker)
    }
}
