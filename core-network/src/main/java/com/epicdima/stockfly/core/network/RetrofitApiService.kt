package com.epicdima.stockfly.core.network

import com.epicdima.stockfly.core.network.response.*
import retrofit2.http.GET
import retrofit2.http.Query

internal interface RetrofitApiService : ApiService {

    @GET("search")
    override suspend fun search(
        @Query("q") query: String,
    ): SearchResponse

    @GET("stock/profile2")
    override suspend fun getCompany(
        @Query("symbol") ticker: String,
    ): CompanyDto

    @GET("company-news")
    override suspend fun getCompanyNews(
        @Query("symbol") ticker: String,
        @Query("from") from: String,
        @Query("to") to: String,
    ): List<NewsItemDto>

    @GET("quote")
    override suspend fun getQuote(
        @Query("symbol") ticker: String,
    ): QuoteDto

    @GET("stock/candle")
    override suspend fun getStockCandles(
        @Query("symbol") ticker: String,
        @Query("resolution") resolution: String,
        @Query("from") from: Long,
        @Query("to") to: Long,
    ): StockCandlesDto

    @GET("stock/recommendation")
    override suspend fun getCompanyRecommendations(
        @Query("symbol") ticker: String,
    ): List<RecommendationDto>
}
