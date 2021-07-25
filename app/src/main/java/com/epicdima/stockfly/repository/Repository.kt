package com.epicdima.stockfly.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.model.NewsItem
import com.epicdima.stockfly.model.Recommendation
import com.epicdima.stockfly.model.StockCandles
import com.epicdima.stockfly.other.StockCandleParam

interface Repository {
    val companies: LiveData<List<Company>>
    val favourites: LiveData<List<Company>>
    val searchedRequests: List<String>

    suspend fun search(query: String): List<Company>
    suspend fun getCompanyForSearch(company: Company): Company
    fun addSearchRequest(request: String)
    suspend fun refreshCompanies()
    suspend fun changeFavourite(company: Company)
    suspend fun changeFavouriteNumber(from: Int, to: Int)
    fun getCompany(ticker: String, coroutineScope: CoroutineScope = GlobalScope): LiveData<Company>
    fun getCompanyWithRefresh(
        ticker: String,
        coroutineScope: CoroutineScope = GlobalScope
    ): LiveData<Company>

    suspend fun getStockCandles(ticker: String, param: StockCandleParam): StockCandles?
    suspend fun getStockCandlesWithRefresh(ticker: String, param: StockCandleParam): StockCandles?
    fun getCompanyNewsWithRefresh(
        ticker: String,
        coroutineScope: CoroutineScope = GlobalScope
    ): LiveData<List<NewsItem>>

    fun getCompanyRecommendationsWithRefresh(
        ticker: String,
        coroutineScope: CoroutineScope
    ): LiveData<List<Recommendation>>
}