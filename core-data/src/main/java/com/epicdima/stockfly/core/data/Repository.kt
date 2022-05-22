package com.epicdima.stockfly.core.data

import com.epicdima.stockfly.core.model.*
import kotlinx.coroutines.flow.Flow

interface Repository {

    val companies: Flow<List<Company>>

    val favourites: Flow<List<Company>>

    val searchedRequests: List<String>

    suspend fun search(query: String): List<Company>

    suspend fun getCompanyForSearch(company: Company): Company

    suspend fun addSearchRequest(request: String)

    suspend fun removeSearchRequest(request: String)

    suspend fun refreshCompanies()

    suspend fun changeFavourite(company: Company)

    suspend fun changeFavouriteNumber(from: Int, to: Int)

    fun getCompany(ticker: String): Flow<Company>

    fun getCompanyWithRefresh(ticker: String): Flow<Company>

    suspend fun getStockCandles(ticker: String, param: StockCandleParam): StockCandles?

    suspend fun getStockCandlesWithRefresh(ticker: String, param: StockCandleParam): StockCandles?

    fun getCompanyNewsWithRefresh(ticker: String): Flow<List<NewsItem>>

    fun getCompanyRecommendationsWithRefresh(ticker: String): Flow<List<Recommendation>>

    suspend fun deleteCompany(company: Company)
}