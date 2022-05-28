package com.epicdima.stockfly.di

import com.epicdima.stockfly.core.navigation.OpenBrowserProvider
import com.epicdima.stockfly.core.navigation.OpenDetailsProvider
import com.epicdima.stockfly.core.navigation.OpenDialerProvider
import com.epicdima.stockfly.core.navigation.OpenSearchProvider
import com.epicdima.stockfly.navigation.StockFlyRouter
import com.github.terrakok.cicerone.Router
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NavigationBindModule {

    @Binds
    fun bindRouter(stockFlyRouter: StockFlyRouter): Router

    @Binds
    fun bindOpenSearchProvider(stockFlyRouter: StockFlyRouter): OpenSearchProvider

    @Binds
    fun bindOpenDetailsProvider(stockFlyRouter: StockFlyRouter): OpenDetailsProvider

    @Binds
    fun bindOpenBrowserProvider(stockFlyRouter: StockFlyRouter): OpenBrowserProvider

    @Binds
    fun bindOpenDialerProvider(stockFlyRouter: StockFlyRouter): OpenDialerProvider
}