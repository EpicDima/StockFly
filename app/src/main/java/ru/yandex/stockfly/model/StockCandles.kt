package ru.yandex.stockfly.model

import ru.yandex.stockfly.other.FORMAT_PRICE
import java.text.SimpleDateFormat
import java.util.*

data class StockCandles(
    val price: DoubleArray = doubleArrayOf(),
    val timestamp: LongArray = longArrayOf()
) {
    fun getItem(index: Int): StockCandleItem {
        return StockCandleItem(price[index], timestamp[index])
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StockCandles

        if (!price.contentEquals(other.price)) return false
        if (!timestamp.contentEquals(other.timestamp)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = price.contentHashCode()
        result = 31 * result + timestamp.contentHashCode()
        return result
    }
}


data class StockCandleItem(
    val price: Double = 0.0,
    val timestamp: Long = 0L
) {
    val priceString: String
        get() = FORMAT_PRICE.format(price)

    fun getTimestampString(format: SimpleDateFormat): String {
        return format.format(Date(timestamp * 1000L)).capitalize(Locale.ROOT)
    }
}