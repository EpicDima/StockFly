package com.epicdima.stockfly.core.network.response

import com.epicdima.stockfly.core.model.Recommendation
import com.squareup.moshi.JsonClass

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
    return Recommendation(
        period,
        strongBuy,
        buy,
        hold,
        sell,
        strongSell
    )
}