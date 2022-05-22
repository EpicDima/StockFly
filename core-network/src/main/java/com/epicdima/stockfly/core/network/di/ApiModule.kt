package com.epicdima.stockfly.core.network.di

import android.app.Application
import com.epicdima.stockfly.core.buildconfig.BuildConfigContainer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(
        application: Application,
        buildConfigContainer: BuildConfigContainer,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .cache(Cache(application.cacheDir, 20L * 1024 * 1024)) // 20 MB
            .addInterceptor(userAgentTokenInterceptor(application, buildConfigContainer))
            .addInterceptor(queryTokenInterceptor(buildConfigContainer))

        if (buildConfigContainer.isDebug) {
            builder.addInterceptor(loggingInterceptor())
        }

        return builder.build()
    }

    private fun userAgentTokenInterceptor(
        application: Application,
        buildConfigContainer: BuildConfigContainer
    ): Interceptor {
        val userAgent =
            "${application.packageName}/${buildConfigContainer.versionName}/${buildConfigContainer.versionCode} ${
                System.getProperty("http.agent", "")
            }".trim()
        val humanReadableUserAgent = toHumanReadableAscii(userAgent)

        return Interceptor { chain ->
            chain.proceed(
                chain
                    .request()
                    .newBuilder()
                    .header("User-Agent", humanReadableUserAgent)
                    .build()
            )
        }
    }

    private fun queryTokenInterceptor(buildConfigContainer: BuildConfigContainer): Interceptor {
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
                            .addQueryParameter("token", buildConfigContainer.apiKey)
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

    private fun toHumanReadableAscii(s: String): String {
        var i = 0
        val length = s.length
        var c: Int
        while (i < length) {
            c = s.codePointAt(i)
            if (c > '\u001f'.code && c < '\u007f'.code) {
                i += Character.charCount(c)
                continue
            }
            val buffer = Buffer()
            buffer.writeUtf8(s, 0, i)
            var j = i
            while (j < length) {
                c = s.codePointAt(j)
                buffer.writeUtf8CodePoint(if (c > '\u001f'.code && c < '\u007f'.code) c else '?'.code)
                j += Character.charCount(c)
            }
            return buffer.readUtf8()
        }
        return s
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
    fun provideApiService(retrofit: Retrofit): com.epicdima.stockfly.core.network.ApiService {
        return retrofit.create(com.epicdima.stockfly.core.network.RetrofitApiService::class.java)
    }
}