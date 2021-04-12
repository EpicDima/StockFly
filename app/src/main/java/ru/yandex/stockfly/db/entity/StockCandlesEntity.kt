package ru.yandex.stockfly.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import ru.yandex.stockfly.model.StockCandleItem
import ru.yandex.stockfly.other.StockCandleParam

@Entity(
    tableName = "stock_candle_items",
    primaryKeys = ["ticker", "param", "timestamp"],
    foreignKeys = [ForeignKey(
        entity = CompanyEntity::class,
        parentColumns = ["ticker"],
        childColumns = ["ticker"],
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )],
    indices = [Index("ticker"), Index("param"), Index("timestamp")]
)
data class StockCandleItemEntity(
    val ticker: String = "",
    val param: StockCandleParam = StockCandleParam.DAY,
    val price: Double = 0.0,
    val timestamp: Long = 0L,
)


fun StockCandleItemEntity.toModel(): StockCandleItem {
    return StockCandleItem(price, timestamp)
}


fun StockCandleItem.toEntity(ticker: String, param: StockCandleParam): StockCandleItemEntity {
    return StockCandleItemEntity(ticker, param, price, timestamp)
}