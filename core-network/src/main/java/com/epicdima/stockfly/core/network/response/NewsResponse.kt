package com.epicdima.stockfly.core.network.response

import com.epicdima.stockfly.core.model.NewsItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewsItemDto(
    val id: Long,
    val datetime: Long,
    val headline: String,
    @Json(name = "image")
    val imageUrl: String,
    val summary: String,
    val source: String,
    val url: String,
)


fun NewsItemDto.toModel(): NewsItem {
    return NewsItem(
        id,
        datetime,
        headline,
        imageUrl,
        summary,
        source,
        url
    )
}