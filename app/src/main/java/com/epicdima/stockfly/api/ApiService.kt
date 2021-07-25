package com.epicdima.stockfly.api

import com.epicdima.stockfly.BuildConfig
import com.epicdima.stockfly.api.response.*
import com.epicdima.stockfly.other.getStringDateNow
import com.epicdima.stockfly.other.getStringDateWeekEarlier

const val BASE_URL = "https://finnhub.io/api/v1/"
private const val API_KEY = BuildConfig.API_KEY

interface ApiService {

    suspend fun search(
        query: String,
        token: String = API_KEY
    ): SearchResponse

    suspend fun getCompany(
        ticker: String,
        token: String = API_KEY
    ): CompanyDto

    suspend fun getCompanyNews(
        ticker: String,
        from: String = getStringDateWeekEarlier(),
        to: String = getStringDateNow(),
        token: String = API_KEY
    ): List<NewsItemDto>

    suspend fun getQuote(
        ticker: String,
        token: String = API_KEY
    ): QuoteDto

    suspend fun getStockCandles(
        ticker: String,
        resolution: String,
        from: Long,
        to: Long,
        token: String = API_KEY
    ): StockCandlesDto

    suspend fun getCompanyRecommendations(
        ticker: String,
        token: String = API_KEY
    ): List<RecommendationDto>
}
