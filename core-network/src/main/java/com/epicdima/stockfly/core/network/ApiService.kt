package com.epicdima.stockfly.core.network

import com.epicdima.stockfly.core.network.response.*

interface ApiService {

    suspend fun search(
        query: String,
    ): SearchResponse

    suspend fun getCompany(
        ticker: String,
    ): CompanyDto

    suspend fun getCompanyNews(
        ticker: String,
        from: String = getStringDateWeekEarlier(),
        to: String = getStringDateNow(),
    ): List<NewsItemDto>

    suspend fun getQuote(
        ticker: String,
    ): QuoteDto

    suspend fun getStockCandles(
        ticker: String,
        resolution: String,
        from: Long,
        to: Long,
    ): StockCandlesDto

    suspend fun getCompanyRecommendations(
        ticker: String,
    ): List<RecommendationDto>
}
