package ru.yandex.stockfly.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ContextModule {

    @Binds
    fun bindApplicationToContext(application: Application): Context
}