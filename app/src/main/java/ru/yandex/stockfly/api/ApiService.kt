package ru.yandex.stockfly.api

import ru.yandex.stockfly.BuildConfig
import ru.yandex.stockfly.api.response.*
import ru.yandex.stockfly.other.getStringDateNow
import ru.yandex.stockfly.other.getStringDateWeekEarlier

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

    suspend fun getStockCandle(
        ticker: String,
        resolution: String,
        from: Long,
        to: Long,
        token: String = API_KEY
    ): StockCandleDto

    suspend fun getCompanyRecommendations(
        ticker: String,
        token: String = API_KEY
    ): List<RecommendationDto>
}
