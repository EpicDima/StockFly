package com.epicdima.stockfly.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.epicdima.stockfly.other.Formatter
import com.epicdima.stockfly.other.Refresher
import com.epicdima.stockfly.repository.Repository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {

    @Singleton
    @Provides
    fun provideRefresher(repository: Repository, preferences: SharedPreferences): Refresher {
        return Refresher(repository, preferences)
    }

    @Singleton
    @Provides
    fun provideFormatter(): Formatter {
        return Formatter
    }
}