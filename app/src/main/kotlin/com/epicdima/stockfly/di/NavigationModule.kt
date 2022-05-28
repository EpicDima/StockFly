package com.epicdima.stockfly.di

import com.epicdima.stockfly.navigation.StockFlyRouter
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    private val cicerone: Cicerone<StockFlyRouter> = Cicerone.create(StockFlyRouter())

    @Singleton
    @Provides
    fun provideStockFlyRouter(): StockFlyRouter {
        return cicerone.router
    }

    @Singleton
    @Provides
    fun provideNavigationHolder(): NavigatorHolder {
        return cicerone.getNavigatorHolder()
    }
}