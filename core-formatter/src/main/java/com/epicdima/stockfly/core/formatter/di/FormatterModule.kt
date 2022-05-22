package com.epicdima.stockfly.core.formatter.di

import android.content.Context
import com.epicdima.stockfly.core.formatter.Formatter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FormatterModule {

    @Singleton
    @Provides
    fun provideFormatter(@ApplicationContext context: Context): Formatter {
        return Formatter(context)
    }
}