package com.epicdima.stockfly.di

import android.app.Application
import com.epicdima.stockfly.BuildConfig
import com.epicdima.stockfly.api.ApiService
import com.epicdima.stockfly.api.ApplicationApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(application: Application): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .cache(Cache(application.cacheDir, 20L * 1024 * 1024)) // 20 MB
            .addInterceptor(userAgentTokenInterceptor(application))
            .addInterceptor(queryTokenInterceptor())

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor())
        }

        return builder.build()
    }

    private fun userAgentTokenInterceptor(application: Application): Interceptor {
        val userAgent =
            "${application.packageName}/${BuildConfig.VERSION_NAME}/${BuildConfig.VERSION_CODE} ${
                System.getProperty("http.agent", "")
            }".trim()

        return Interceptor { chain ->
            chain.proceed(
                chain
                    .request()
                    .newBuilder()
                    .header("User-Agent", userAgent)
                    .build()
            )
        }
    }

    private fun queryTokenInterceptor(): Interceptor {
        return Interceptor { chain ->
            chain.proceed(
                chain
                    .request()
                    .newBuilder()
                    .url(
                        chain
                            .request()
                            .url
                            .newBuilder()
                            .addQueryParameter("token", BuildConfig.API_KEY)
                            .build()
                    )
                    .build()
            )
        }
    }

    private fun loggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC)
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://finnhub.io/api/v1/")
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