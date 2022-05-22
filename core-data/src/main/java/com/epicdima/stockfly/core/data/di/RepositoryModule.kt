package com.epicdima.stockfly.core.data.di

import android.content.SharedPreferences
import com.epicdima.stockfly.core.data.AppRepository
import com.epicdima.stockfly.core.data.Repository
import com.epicdima.stockfly.core.database.dao.CompanyDao
import com.epicdima.stockfly.core.database.dao.NewsItemDao
import com.epicdima.stockfly.core.database.dao.RecommendationDao
import com.epicdima.stockfly.core.database.dao.StockCandlesDao
import com.epicdima.stockfly.core.network.ApiService
import com.squareup.moshi.JsonAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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