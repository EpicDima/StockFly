package ru.yandex.stockfly.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.yandex.stockfly.shortcut.ShortcutConfigurator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShortcutModule {

    @Singleton
    @Provides
    fun provideShortcutConfigurator(): ShortcutConfigurator {
        return ShortcutConfigurator()
    }
}