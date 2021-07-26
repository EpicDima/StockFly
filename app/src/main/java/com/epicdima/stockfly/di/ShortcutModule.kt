package com.epicdima.stockfly.di

import android.content.Context
import com.epicdima.stockfly.shortcut.ShortcutConfigurator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShortcutModule {

    @Singleton
    @Provides
    fun provideShortcutConfigurator(context: Context): ShortcutConfigurator {
        return ShortcutConfigurator(context)
    }
}