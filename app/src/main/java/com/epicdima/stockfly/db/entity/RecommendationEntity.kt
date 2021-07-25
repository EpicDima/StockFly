package com.epicdima.stockfly.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.epicdima.stockfly.model.Recommendation

@Entity(
    tableName = "recommendations",
    primaryKeys = ["ticker", "period"],
    foreignKeys = [ForeignKey(
        entity = CompanyEntity::class,
        parentColumns = ["ticker"],
        childColumns = ["ticker"],
        onDelete = ForeignKey.CASCADE,
        deferred = true
    )],
    indices = [Index("ticker"), Index("period")]
)
data class RecommendationEntity(
    val ticker: String = "",
    val period: String = "",
    val strongBuy: Int = 0,
    val buy: Int = 0,
    val hold: Int = 0,
    val sell: Int = 0,
    val strongSell: Int = 0,
)


fun RecommendationEntity.toModel(): Recommendation {
    return Recommendation(period, strongBuy, buy, hold, sell, strongSell)
}


fun Recommendation.toEntity(ticker: String): RecommendationEntity {
    return RecommendationEntity(ticker, period, strongBuy, buy, hold, sell, strongSell)
}
