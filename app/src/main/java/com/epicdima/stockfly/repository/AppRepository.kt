package com.epicdima.stockfly.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.epicdima.stockfly.api.ApiService
import com.epicdima.stockfly.api.response.toModel
import com.epicdima.stockfly.db.dao.CompanyDao
import com.epicdima.stockfly.db.dao.NewsItemDao
import com.epicdima.stockfly.db.dao.RecommendationDao
import com.epicdima.stockfly.db.dao.StockCandlesDao
import com.epicdima.stockfly.db.entity.toEntity
import com.epicdima.stockfly.db.entity.toModel
import com.epicdima.stockfly.model.*
import com.epicdima.stockfly.other.StockCandleParam
import com.epicdima.stockfly.other.getSearched
import com.epicdima.stockfly.other.setSearched
import timber.log.Timber
import java.util.*

private const val EMPTY_JSON_STRING = "[]"
private const val MAX_SEARCHED_REQUESTS = 50

class AppRepository(
    private val apiService: ApiService,
    private val companyDao: CompanyDao,
    private val newsItemDao: NewsItemDao,
    private val recommendationDao: RecommendationDao,
    private val stockCandlesDao: StockCandlesDao,
    private val preferences: SharedPreferences,
    private val stringListAdapter: JsonAdapter<List<String>>,
) : Repository {

    private val _companies = MutableLiveData<List<Company>>()
    override val companies: LiveData<List<Company>> = _companies

    private val _favourites = MutableLiveData<List<Company>>()
    override val favourites: LiveData<List<Company>> = _favourites

    override val searchedRequests: List<String>
        get() = getSearched(stringListAdapter)

    init {
        Timber.i("init")
        observeCompanies()
        observeFavourites()
    }

    private fun observeCompanies() {
        companyDao.selectAll().observeForever { list ->
            GlobalScope.launch(Dispatchers.IO) {
                _companies.postValue(list.map { it.toModel() })
            }
        }
    }

    private fun observeFavourites() {
        companyDao.selectFavourites().observeForever { list ->
            GlobalScope.launch(Dispatchers.IO) {
                _favourites.postValue(list.map { it.toModel() })
            }
        }
    }

    private fun getSearched(adapter: JsonAdapter<List<String>>): List<String> {
        return adapter.fromJson(preferences.getSearched() ?: EMPTY_JSON_STRING)!!
    }

    private suspend fun getCompanyWithQuote(ticker: String): Company? {
        return try {
            val quote = apiService.getQuote(ticker).toModel()
            apiService.getCompany(ticker).toModel().copy(quote = quote)
        } catch (e: Exception) {
            Timber.w(e)
            null
        }
    }

    override suspend fun search(query: String): List<Company> {
        return apiService.search(query).result
            .filter { !isWrongTicker(it.ticker) }
            .map { companyDao.select(it.ticker)?.toModel() ?: it.toModel() }
    }

    private fun isWrongTicker(ticker: String): Boolean {
        // Проверка на символы . и : надо, потому что в бесплатной версии API есть поддержка
        // только US symbols, поэтому остальные просто выкидываются, иначе будет много
        // компаний без информации
        return ticker.contains(".") || ticker.contains(":")
    }

    override suspend fun getCompanyForSearch(company: Company): Company {
        val updatedCompany = getCompanyWithQuote(company.ticker)
        return if (updatedCompany != null) {
            if (company.favourite) {
                updatedCompany.copy(favourite = company.favourite)
            } else {
                updatedCompany
            }
        } else {
            company
        }
    }

    override fun addSearchRequest(request: String) {
        val list = searchedRequests.toMutableList()
        list.add(0, request)
        setSearched(list, stringListAdapter)
    }

    private fun setSearched(searched: List<String>, adapter: JsonAdapter<List<String>>) {
        val list = searched.subList(0, minOf(searched.size, MAX_SEARCHED_REQUESTS)).distinct()
        preferences.setSearched(adapter.toJson(list))
    }

    override suspend fun refreshCompanies() {
        val list = companyDao.selectAllAsList()
        companyDao.update(list.mapNotNull {
            val existing = companyDao.select(it.ticker)!!
            getCompanyWithQuote(it.ticker)?.toEntity()?.copy(
                favourite = existing.favourite,
                favouriteNumber = existing.favouriteNumber
            )
        })
    }

    override suspend fun changeFavourite(company: Company) {
        val fromDb = companyDao.select(company.ticker)!!.toModel()
        companyDao.update(fromDb.copy(favourite = !fromDb.favourite).toEntity())
        val list = companyDao
            .selectFavouritesAsList()
            .mapIndexed { index, entity -> entity.copy(favouriteNumber = index + 1) }
        companyDao.update(list)
    }

    override suspend fun changeFavouriteNumber(from: Int, to: Int) {
        val list = companyDao.selectFavouritesAsList().toMutableList()
        if (from < to) {
            for (i in from until to) {
                Collections.swap(list, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(list, i, i - 1)
            }
        }
        companyDao.update(list.mapIndexed { index, entity -> entity.copy(favouriteNumber = index + 1) })
    }

    override fun getCompany(ticker: String, coroutineScope: CoroutineScope): LiveData<Company> {
        val company = MutableLiveData<Company>()
        coroutineScope.launch(Dispatchers.IO) {
            companyDao.select(ticker)?.let {
                company.postValue(it.toModel())
            }
        }
        return company
    }

    override fun getCompanyWithRefresh(
        ticker: String,
        coroutineScope: CoroutineScope
    ): LiveData<Company> {
        return getDataWithRefresh(coroutineScope,
            getFromDatabase = {
                companyDao.select(ticker)!!.toModel()
            },
            getFromApi = {
                getCompanyWithQuote(ticker)!!
            },
            refreshDatabaseAndGetNew = { value ->
                companyDao.upsertAndSelect(value.toEntity()).toModel()
            }
        )
    }

    override suspend fun getStockCandles(ticker: String, param: StockCandleParam): StockCandles? {
        val (from, _) = param.getTimeInterval()
        return stockCandlesDao.select(ticker, param, from).map { it.toModel() }
            .toStockCandles()
    }

    override suspend fun getStockCandlesWithRefresh(
        ticker: String,
        param: StockCandleParam
    ): StockCandles? {
        val (from, to) = param.getTimeInterval()
        val fromApi = apiService.getStockCandles(ticker, param.resolution, from, to).toModel()
        val forDb = fromApi.toStockCandleItems().map { it.toEntity(ticker, param) }
        val fromDb = stockCandlesDao.insertAndSelect(ticker, param, from, forDb)
        return fromDb.map { it.toModel() }.toStockCandles()
    }

    override fun getCompanyNewsWithRefresh(
        ticker: String,
        coroutineScope: CoroutineScope
    ): LiveData<List<NewsItem>> {
        return getDataWithRefresh(coroutineScope,
            getFromDatabase = {
                newsItemDao.select(ticker).map { it.toModel() }
            },
            getFromApi = {
                apiService.getCompanyNews(ticker).map { it.toModel() }
            },
            refreshDatabaseAndGetNew = { value ->
                newsItemDao.insertAndSelect(ticker, value.map { it.toEntity(ticker) })
                    .map { it.toModel() }
            }
        )
    }

    override fun getCompanyRecommendationsWithRefresh(
        ticker: String,
        coroutineScope: CoroutineScope
    ): LiveData<List<Recommendation>> {
        return getDataWithRefresh(coroutineScope,
            getFromDatabase = {
                recommendationDao.select(ticker).map { it.toModel() }
            },
            getFromApi = {
                apiService.getCompanyRecommendations(ticker).map { it.toModel() }
            },
            refreshDatabaseAndGetNew = { value ->
                recommendationDao.insertAndSelect(ticker, value.map { it.toEntity(ticker) })
                    .map { it.toModel() }
            }
        )
    }

    private fun <T> getDataWithRefresh(
        coroutineScope: CoroutineScope,
        getFromDatabase: suspend () -> T,
        getFromApi: suspend () -> T,
        refreshDatabaseAndGetNew: suspend (value: T) -> T
    ): LiveData<T> {
        val liveData = MutableLiveData<T>()
        coroutineScope.launch(Dispatchers.IO) {
            kotlin.runCatching { getFromDatabase() }
                .onSuccess { databaseData -> liveData.postValue(databaseData) }
                .onFailure { Timber.w(it, "getFromDatabase") }
            kotlin.runCatching { getFromApi() }
                .onSuccess { apiData ->
                    kotlin.runCatching { refreshDatabaseAndGetNew(apiData) }
                        .onSuccess { databaseData -> liveData.postValue(databaseData) }
                        .onFailure { Timber.w(it, "refreshDbAndGetNew") }
                }
                .onFailure { Timber.w(it, "getFromApi") }
        }
        return liveData
    }
}