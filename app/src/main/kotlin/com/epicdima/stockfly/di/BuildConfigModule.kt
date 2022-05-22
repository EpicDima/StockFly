package com.epicdima.stockfly.di

import com.epicdima.stockfly.BuildConfig
import com.epicdima.stockfly.core.buildconfig.BuildConfigContainer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BuildConfigModule {

    @Singleton
    @Provides
    fun provideBuildConfigContainer(): BuildConfigContainer {
        return BuildConfigContainer(
            BuildConfig.DEBUG,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
            BuildConfig.API_KEY
        )
    }
}