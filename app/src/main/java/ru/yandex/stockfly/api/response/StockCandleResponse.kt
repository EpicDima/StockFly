package ru.yandex.stockfly.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.yandex.stockfly.model.StockCandles

@JsonClass(generateAdapter = true)
data class StockCandleDto(
    @Json(name = "c")
    val price: DoubleArray = doubleArrayOf(),
    @Json(name = "t")
    val timestamp: LongArray = longArrayOf()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StockCandleDto

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


fun StockCandleDto.toModel(): StockCandles {
    return StockCandles(price, timestamp)
}