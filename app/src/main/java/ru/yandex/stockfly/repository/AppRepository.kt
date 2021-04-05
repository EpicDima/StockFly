package ru.yandex.stockfly.repository

import android.content.SharedPreferences
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
                    companyDao.upsert(list.mapNotNull { getCompanyWithQuote(it.ticker)?.toEntity() }
                        .toList())
                    launch(Dispatchers.Main) {
                        _companies.removeObserver(refreshCompaniesObserver)
                    }
                } catch (ignored: Exception) {
                }
            }
        }
    }

    private suspend fun getCompanyWithQuote(ticker: String): Company? {
        var company: Company? = null
        return try {
            company = apiService.getCompany(ticker).toModel()
            val quote = apiService.getQuote(ticker).toModel()
            company.copy(quote = quote)
        } catch (e: Exception) {
            company
        }
    }

    override suspend fun search(query: String): List<SearchItem> {
        // Проверка на символы . и : надо, потому что в бесплатной версии API есть поддержка
        // только US symbols, поэтому остальные просто выкидываются, иначе будет много
        // компаний без данных о стоимости
        return apiService.search(query).result
            .filter { !(it.ticker.contains(".") || it.ticker.contains(":")) }
            .map { it.toModel() }
    }

    override fun addSearchRequest(request: String) {
        val list = _searchedRequests.apply {
            add(0, request)
            if (size >= MAX_SEARCHED_REQUESTS) {
                dropLast(1)
            }
        }.toList()
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
        val company = MutableLiveData<Company>()
        coroutineScope.launch(Dispatchers.IO) {
            companyDao.select(ticker)?.let {
                company.postValue(it.toModel())
            }
            getCompanyWithQuote(ticker)?.let {
                companyDao.upsert(it.toEntity())
                companyDao.select(ticker)?.let { entity ->
                    company.postValue(entity.toModel())
                }
            }
        }
        return company
    }

    override suspend fun getStockCandles(ticker: String, param: StockCandleParam): StockCandles {
        val (from, to) = param.getTimeInterval()
        return apiService.getStockCandle(ticker, param.resolution, from, to).toModel()
    }

    override fun getCompanyNewsWithRefresh(
        ticker: String,
        coroutineScope: CoroutineScope
    ): LiveData<List<NewsItem>> {
        val news = MutableLiveData<List<NewsItem>>()
        coroutineScope.launch(Dispatchers.IO) {
            newsItemDao.select(ticker).let { list ->
                news.postValue(list.map { it.toModel() })
            }
            try {
                newsItemDao.upsert(
                    apiService.getCompanyNews(ticker).map { it.toModel().toEntity(ticker) }.toList()
                )
                newsItemDao.select(ticker).let { list ->
                    news.postValue(list.map { it.toModel() })
                }
            } catch (ignored: Exception) {
            }
        }
        return news
    }

    override fun getCompanyRecommendationsWithRefresh(
        ticker: String,
        coroutineScope: CoroutineScope
    ): LiveData<List<Recommendation>> {
        val recommendations = MutableLiveData<List<Recommendation>>()
        coroutineScope.launch(Dispatchers.IO) {
            recommendationDao.select(ticker).let { list ->
                recommendations.postValue(list.map { it.toModel() })
            }
            try {
                recommendationDao.upsert(
                    apiService.getCompanyRecommendations(ticker).map {
                        it.toModel().toEntity(ticker)
                    }.toList()
                )
                recommendationDao.select(ticker).let { list ->
                    recommendations.postValue(list.map { it.toModel() })
                }
            } catch (ignored: Exception) {
            }
        }
        return recommendations
    }
}
