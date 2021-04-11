package ru.yandex.stockfly.db.dao

import androidx.room.*
import ru.yandex.stockfly.db.entity.StockCandleItemEntity
import ru.yandex.stockfly.other.StockCandleParam

@Dao
interface StockCandlesDao {

    @Query("SELECT * FROM stock_candle_items WHERE ticker = :ticker AND param = :param AND timestamp >= :from")
    suspend fun select(
        ticker: String,
        param: StockCandleParam,
        from: Long
    ): List<StockCandleItemEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(items: List<StockCandleItemEntity>)

    @Query("DELETE FROM stock_candle_items WHERE ticker = :ticker AND param = :param AND timestamp < :from")
    suspend fun delete(ticker: String, param: StockCandleParam, from: Long)

    @Transaction
    suspend fun upsertAndSelect(
        ticker: String,
        param: StockCandleParam,
        from: Long,
        items: List<StockCandleItemEntity>
    ): List<StockCandleItemEntity> {
        delete(ticker, param, from)
        upsert(items)
        return select(ticker, param, from)
    }
}