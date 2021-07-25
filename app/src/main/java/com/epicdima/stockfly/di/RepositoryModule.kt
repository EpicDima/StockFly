package com.epicdima.stockfly.di

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.epicdima.stockfly.api.ApiService
import com.epicdima.stockfly.db.dao.CompanyDao
import com.epicdima.stockfly.db.dao.NewsItemDao
import com.epicdima.stockfly.db.dao.RecommendationDao
import com.epicdima.stockfly.db.dao.StockCandlesDao
import com.epicdima.stockfly.repository.AppRepository
import com.epicdima.stockfly.repository.Repository
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