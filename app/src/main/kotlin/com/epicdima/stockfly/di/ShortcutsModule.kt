package com.epicdima.stockfly.di

import android.content.Context
import com.epicdima.stockfly.core.shortcuts.ShortcutsConfigurator
import com.epicdima.stockfly.shortcut.RealShortcutsConfigurator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShortcutsModule {

    @Singleton
    @Provides
    fun provideShortcutConfigurator(@ApplicationContext context: Context): ShortcutsConfigurator {
        return RealShortcutsConfigurator(context)
    }
}