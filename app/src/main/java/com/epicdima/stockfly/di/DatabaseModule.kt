package com.epicdima.stockfly.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.epicdima.stockfly.db.Database
import com.epicdima.stockfly.db.buildDatabase
import com.epicdima.stockfly.db.dao.CompanyDao
import com.epicdima.stockfly.db.dao.NewsItemDao
import com.epicdima.stockfly.db.dao.RecommendationDao
import com.epicdima.stockfly.db.dao.StockCandlesDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(application: Application): Database {
        return buildDatabase(application)
    }

    @Singleton
    @Provides
    fun provideCompanyDao(database: Database): CompanyDao {
        return database.companyDao()
    }

    @Singleton
    @Provides
    fun provideNewsItemDao(database: Database): NewsItemDao {
        return database.newsItemDao()
    }

    @Singleton
    @Provides
    fun provideRecommendationDao(database: Database): RecommendationDao {
        return database.recommendationDao()
    }

    @Singleton
    @Provides
    fun provideStockCandlesDao(database: Database): StockCandlesDao {
        return database.stockCandlesDao()
    }
}