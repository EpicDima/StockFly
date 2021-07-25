package com.epicdima.stockfly.di

import android.content.SharedPreferences
import com.epicdima.stockfly.other.Formatter
import com.epicdima.stockfly.other.Refresher
import com.epicdima.stockfly.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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