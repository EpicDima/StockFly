package ru.yandex.stockfly.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.yandex.stockfly.model.Quote

@JsonClass(generateAdapter = true)
data class QuoteDto(
    @Json(name = "c")
    val current: Double,
    @Json(name = "o")
    val open: Double,
    @Json(name = "h")
    val high: Double,
    @Json(name = "l")
    val low: Double,
    @Json(name = "pc")
    val previousClose: Double
)


fun QuoteDto.toModel(): Quote {
    return Quote(current, open, high, low, previousClose)
}