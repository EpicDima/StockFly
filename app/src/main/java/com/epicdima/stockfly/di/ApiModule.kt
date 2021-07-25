package com.epicdima.stockfly.di

import android.app.Application
import com.epicdima.stockfly.BuildConfig
import com.epicdima.stockfly.api.ApiService
import com.epicdima.stockfly.api.ApplicationApiService
import com.epicdima.stockfly.api.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val CACHE_SIZE: Long = 20 * 1024 * 1024 // 20 MB

    @Singleton
    @Provides
    fun provideOkHttpClient(application: Application): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .cache(Cache(application.cacheDir, CACHE_SIZE))

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BASIC)
            )
        }

        return builder.build()
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