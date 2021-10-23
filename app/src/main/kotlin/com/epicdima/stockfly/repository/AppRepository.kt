package com.epicdima.stockfly.repository

import android.content.SharedPreferences
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
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
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

    override val companies: Flow<List<Company>> = flow {
        companyDao
            .selectAll()
            .flowOn(Dispatchers.IO)
            .map { list ->
                list.map { it.toModel() }
            }
            .flowOn(Dispatchers.Default)
            .collect { emit(it) }
    }

    override val favourites: Flow<List<Company>> = flow {
        companyDao
            .selectFavourites()
            .flowOn(Dispatchers.IO)
            .map { list ->
                list.map { it.toModel() }
            }
            .flowOn(Dispatchers.Default)
            .collect { emit(it) }
    }

    override val searchedRequests: List<String>
        get() = getSearched(stringListAdapter)

    private fun getSearched(adapter: JsonAdapter<List<String>>): List<String> {
        return adapter.fromJson(preferences.getSearched() ?: EMPTY_JSON_STRING)!!
    }

    private suspend fun getCompanyWithQuote(ticker: String): Company? =
        withContext(Dispatchers.IO) {
            try {
                apiService
                    .getCompany(ticker)
                    .toModel()
                    .copy(
                        quote = apiService
                            .getQuote(ticker)
                            .toModel()
                    )
            } catch (e: Exception) {
                Timber.w(e)
                null
            }
        }

    override suspend fun search(query: String): List<Company> = withContext(Dispatchers.IO) {
        apiService.search(query).result
            .filter { !isWrongTicker(it.ticker) }
            .map { companyDao.selectAsModel(it.ticker)?.toModel() ?: it.toModel() }
    }

    private fun isWrongTicker(ticker: String): Boolean {
        // Проверка на символы . и : надо, потому что в бесплатной версии API есть поддержка
        // только US symbols, поэтому остальные просто выкидываются, иначе будет много
        // компаний без информации
        return ticker.contains(".") || ticker.contains(":")
    }

    override suspend fun getCompanyForSearch(company: Company): Company =
        withContext(Dispatchers.Default) {
            val updatedCompany = getCompanyWithQuote(company.ticker)
            if (updatedCompany != null) {
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

    override suspend fun refreshCompanies() = withContext(Dispatchers.IO) {
        companyDao.update(companyDao
            .selectAllAsList()
            .mapNotNull {
                val existing = companyDao.selectAsModel(it.ticker)!!
                getCompanyWithQuote(it.ticker)
                    ?.toEntity()
                    ?.copy(
                        favourite = existing.favourite,
                        favouriteNumber = existing.favouriteNumber
                    )
            })
    }

    override suspend fun changeFavourite(company: Company) = withContext(Dispatchers.IO) {
        val fromDb = companyDao.selectAsModel(company.ticker)!!.toModel()
        companyDao.update(
            fromDb.copy(favourite = !fromDb.favourite, favouriteNumber = 0).toEntity()
        )
        val list = companyDao
            .selectFavouritesAsList()
            .mapIndexed { index, entity -> entity.copy(favouriteNumber = index + 1) }
        companyDao.update(list)
    }

    override suspend fun changeFavouriteNumber(from: Int, to: Int) =
        withContext(Dispatchers.Default) {
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

    override fun getCompany(ticker: String): Flow<Company> {
        return companyDao.select(ticker)
            .flowOn(Dispatchers.IO)
            .filterNotNull()
            .map { it.toModel() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Default)
    }

    override fun getCompanyWithRefresh(
        ticker: String,
    ): Flow<Company> {
        return getDataWithRefresh(
            getFromDatabase = {
                companyDao.selectAsModel(ticker)!!.toModel()
            },
            getFromApi = {
                getCompanyWithQuote(ticker)!!
            },
            refreshDatabaseAndGetNew = { value ->
                companyDao.upsertAndSelect(value.toEntity()).toModel()
            }
        )
    }

    override suspend fun getStockCandles(ticker: String, param: StockCandleParam): StockCandles? =
        withContext(Dispatchers.IO) {
            val (from, _) = param.getTimeInterval()
            stockCandlesDao.select(ticker, param, from).map { it.toModel() }
                .toStockCandles()
        }

    override suspend fun getStockCandlesWithRefresh(
        ticker: String,
        param: StockCandleParam
    ): StockCandles? = withContext(Dispatchers.IO) {
        val (from, to) = param.getTimeInterval()
        val fromApi = apiService.getStockCandles(ticker, param.resolution, from, to).toModel()
        val forDb = fromApi.toStockCandleItems().map { it.toEntity(ticker, param) }
        val fromDb = stockCandlesDao.insertAndSelect(ticker, param, from, forDb)
        fromDb.map { it.toModel() }.toStockCandles()
    }

    override fun getCompanyNewsWithRefresh(
        ticker: String
    ): Flow<List<NewsItem>> {
        return getDataWithRefresh(
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
        ticker: String
    ): Flow<List<Recommendation>> {
        return getDataWithRefresh(
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

    override suspend fun deleteCompany(company: Company) = withContext(Dispatchers.IO) {
        companyDao.delete(company.toEntity())
    }

    private fun <T> getDataWithRefresh(
        getFromDatabase: suspend () -> T,
        getFromApi: suspend () -> T,
        refreshDatabaseAndGetNew: suspend (value: T) -> T
    ): Flow<T> {
        return flow<T> {
            runCatching { getFromDatabase() }
                .onSuccess { databaseData -> emit(databaseData) }
                .onFailure { Timber.w(it, "getFromDatabase") }
            runCatching { getFromApi() }
                .onSuccess { apiData ->
                    runCatching { refreshDatabaseAndGetNew(apiData) }
                        .onSuccess { databaseData -> emit(databaseData) }
                        .onFailure { Timber.w(it, "refreshDbAndGetNew") }
                }
                .onFailure { Timber.w(it, "getFromApi") }
        }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }
}