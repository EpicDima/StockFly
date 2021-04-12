package ru.yandex.stockfly.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.yandex.stockfly.api.ApiService
import ru.yandex.stockfly.api.response.toModel
import ru.yandex.stockfly.db.dao.CompanyDao
import ru.yandex.stockfly.db.dao.NewsItemDao
import ru.yandex.stockfly.db.dao.RecommendationDao
import ru.yandex.stockfly.db.dao.StockCandlesDao
import ru.yandex.stockfly.db.entity.toEntity
import ru.yandex.stockfly.db.entity.toModel
import ru.yandex.stockfly.model.*
import ru.yandex.stockfly.other.MAX_SEARCHED_REQUESTS
import ru.yandex.stockfly.other.StockCandleParam
import ru.yandex.stockfly.other.getSearched
import ru.yandex.stockfly.other.setSearched

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

    private val _searchedRequests: MutableList<String> =
        preferences.getSearched(stringListAdapter).toMutableList()
    override val searchedRequests: List<String> = _searchedRequests

    private lateinit var refreshCompaniesObserver: Observer<List<Company>>

    init {
        observeCompanies()
        observeFavourites()
        createRefreshObserver()
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

    private fun createRefreshObserver() {
        refreshCompaniesObserver = Observer<List<Company>> { list ->
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    companyDao.update(list.mapNotNull {
                        val favourite = companyDao.select(it.ticker)!!.favourite
                        getCompanyWithQuote(it.ticker)?.toEntity()?.copy(favourite = favourite)
                    })
                    launch(Dispatchers.Main) {
                        _companies.removeObserver(refreshCompaniesObserver)
                    }
                } catch (ignored: Exception) {
                }
            }
        }
    }

    private suspend fun getCompanyWithQuote(ticker: String): Company? {
        return try {
            val quote = apiService.getQuote(ticker).toModel()
            apiService.getCompany(ticker).toModel().copy(quote = quote)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun search(query: String): List<Company> {
        return apiService.search(query).result
            .filter { !isWrongTicker(it.ticker) }
            .map {
                val company = companyDao.select(it.ticker)
                company?.toModel() ?: it.toModel()
            }
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
        val list = _searchedRequests.apply {
            add(0, request)
            if (size >= MAX_SEARCHED_REQUESTS) {
                dropLast(1)
            }
        }.toList() // для копирования списка
        _searchedRequests.clear()
        _searchedRequests.addAll(list.distinct())
        preferences.setSearched(searchedRequests, stringListAdapter)
    }

    override fun refreshCompanies(coroutineScope: CoroutineScope) {
        _companies.observeForever(refreshCompaniesObserver)
    }

    override suspend fun changeFavourite(company: Company): Int {
        return companyDao.updateFavourite(company.ticker, !company.favourite)
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
                .onFailure { Log.w("REPOSITORY", "getFromDatabase", it) }
            kotlin.runCatching { getFromApi() }
                .onSuccess { apiData ->
                    kotlin.runCatching { refreshDatabaseAndGetNew(apiData) }
                        .onSuccess { databaseData -> liveData.postValue(databaseData) }
                        .onFailure { Log.w("REPOSITORY", "refreshDbAndGetNew", it) }
                }
                .onFailure { Log.w("REPOSITORY", "getFromApi", it) }
        }
        return liveData
    }
}