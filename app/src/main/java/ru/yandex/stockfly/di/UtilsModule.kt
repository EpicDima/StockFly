package ru.yandex.stockfly.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.yandex.stockfly.other.Formatter
import ru.yandex.stockfly.other.Refresher
import ru.yandex.stockfly.repository.Repository
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