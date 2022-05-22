package com.epicdima.stockfly.core.database.dao

import androidx.room.*
import com.epicdima.stockfly.core.database.entity.StockCandleItemEntity
import com.epicdima.stockfly.core.model.StockCandleParam

@Dao
interface StockCandlesDao {

    @Query("SELECT * FROM stock_candle_items WHERE ticker = :ticker AND param = :param AND timestamp >= :from")
    suspend fun select(
        ticker: String,
        param: StockCandleParam,
        from: Long
    ): List<StockCandleItemEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(items: List<StockCandleItemEntity>)

    @Query("DELETE FROM stock_candle_items WHERE ticker = :ticker AND param = :param AND timestamp < :from")
    suspend fun delete(ticker: String, param: StockCandleParam, from: Long)

    @Transaction
    suspend fun insertAndSelect(
        ticker: String,
        param: StockCandleParam,
        from: Long,
        items: List<StockCandleItemEntity>
    ): List<StockCandleItemEntity> {
        delete(ticker, param, from)
        insert(items)
        return select(ticker, param, from)
    }
}
