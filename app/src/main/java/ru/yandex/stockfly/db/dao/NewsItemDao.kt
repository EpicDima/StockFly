package ru.yandex.stockfly.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.yandex.stockfly.db.entity.NewsItemEntity

@Dao
interface NewsItemDao {

    @Query("SELECT * FROM news WHERE ticker = :ticker ORDER BY datetime DESC")
    suspend fun select(ticker: String): List<NewsItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(news: List<NewsItemEntity>)
}