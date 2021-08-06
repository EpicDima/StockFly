package com.epicdima.stockfly.api

import com.epicdima.stockfly.api.response.*
import com.epicdima.stockfly.other.getStringDateNow
import com.epicdima.stockfly.other.getStringDateWeekEarlier

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
