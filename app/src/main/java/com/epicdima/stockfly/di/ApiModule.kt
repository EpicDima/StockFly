package com.epicdima.stockfly.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.epicdima.stockfly.api.ApiService
import com.epicdima.stockfly.api.ApplicationApiService
import com.epicdima.stockfly.api.BASE_URL
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val CACHE_SIZE: Long = 50 * 1024 * 1024 // 50 MB

    @Singleton
    @Provides
    fun provideOkHttpClient(application: Application): OkHttpClient {
        return OkHttpClient.Builder().cache(Cache(application.cacheDir, CACHE_SIZE)).build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .validateEagerly(true)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApplicationApiService::class.java)
    }
}