package com.epicdima.stockfly.core.database.di

import android.content.Context
import com.epicdima.stockfly.core.database.AppDatabase
import com.epicdima.stockfly.core.database.Database
import com.epicdima.stockfly.core.database.dao.CompanyDao
import com.epicdima.stockfly.core.database.dao.NewsItemDao
import com.epicdima.stockfly.core.database.dao.RecommendationDao
import com.epicdima.stockfly.core.database.dao.StockCandlesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return AppDatabase.buildDatabase(context)
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