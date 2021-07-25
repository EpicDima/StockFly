package com.epicdima.stockfly.model

data class NewsItem(
    val id: Long = 0,
    val datetime: Long = 0,
    val headline: String = "",
    val imageUrl: String = "",
    val summary: String = "",
    val source: String = "",
    val url: String = "",
)
