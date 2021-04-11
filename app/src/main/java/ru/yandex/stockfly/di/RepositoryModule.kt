package ru.yandex.stockfly.di

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.yandex.stockfly.api.ApiService
import ru.yandex.stockfly.db.dao.CompanyDao
import ru.yandex.stockfly.db.dao.NewsItemDao
import ru.yandex.stockfly.db.dao.RecommendationDao
import ru.yandex.stockfly.db.dao.StockCandlesDao
import ru.yandex.stockfly.repository.AppRepository
import ru.yandex.stockfly.repository.Repository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        apiService: ApiService,
        companyDao: CompanyDao,
        newsItemDao: NewsItemDao,
        recommendationDao: RecommendationDao,
        stockCandlesDao: StockCandlesDao,
        preferences: SharedPreferences,
        adapter: JsonAdapter<List<String>>,
    ): Repository {
        return AppRepository(
            apiService,
            companyDao,
            newsItemDao,
            recommendationDao,
            stockCandlesDao,
            preferences,
            adapter
        )
    }
}