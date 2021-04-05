package ru.yandex.stockfly.api.response

import com.squareup.moshi.JsonClass
import ru.yandex.stockfly.model.Recommendation

@JsonClass(generateAdapter = true)
data class RecommendationDto(
    val period: String,
    val strongBuy: Int,
    val buy: Int,
    val hold: Int,
    val sell: Int,
    val strongSell: Int,
)


fun RecommendationDto.toModel(): Recommendation {
    return Recommendation(period, strongBuy, buy, hold, sell, strongSell)
}