package com.epicdima.stockfly.di

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.epicdima.stockfly.customtabs.CustomTabsProvider
import com.epicdima.stockfly.other.LayoutPool
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Qualifier

@Module
@InstallIn(ActivityComponent::class)
object SingleActivityModule {

    @ActivityScoped
    @Provides
    fun provideLayoutPool(@ActivityContext context: Context): LayoutPool {
        return LayoutPool(context)
    }

    @ActivityScoped
    @Provides
    fun provideCustomTabsProvider(@ActivityContext context: Context): CustomTabsProvider {
        return CustomTabsProvider(context)
    }

    @ActivityScoped
    @Provides
    @CompanyList
    fun provideCompanyRecycledViewPool(): RecyclerView.RecycledViewPool {
        return RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(0, 10)
        }
    }
}


@Qualifier
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class CompanyList