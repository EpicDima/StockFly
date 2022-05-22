package com.epicdima.stockfly.core.utils.di

import android.content.Context
import com.epicdima.stockfly.core.utils.LayoutPool
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object LayoutPoolModule {

    @ActivityScoped
    @Provides
    fun provideLayoutPool(@ActivityContext context: Context): LayoutPool {
        return LayoutPool(context)
    }
}