package com.epicdima.stockfly.core.customtabs.di

import android.content.Context
import com.epicdima.stockfly.core.customtabs.CustomTabsProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object CustomTabsModule {

    @ActivityScoped
    @Provides
    fun provideCustomTabsProvider(@ActivityContext context: Context): CustomTabsProvider {
        return CustomTabsProvider(context)
    }
}