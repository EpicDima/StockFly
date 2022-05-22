package com.epicdima.stockfly.feature.list.shared.di

import androidx.recyclerview.widget.RecyclerView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Qualifier

@Module
@InstallIn(ActivityComponent::class)
object CompanyListModule {

    @ActivityScoped
    @Provides
    @CompanyList
    fun provideCompanyRecycledViewPool(): RecyclerView.RecycledViewPool {
        return RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(0, 15)
        }
    }
}


@Qualifier
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class CompanyList