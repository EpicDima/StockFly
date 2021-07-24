package ru.yandex.stockfly.di

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object JsonModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Singleton
    @Provides
    fun provideStringListAdapter(moshi: Moshi): JsonAdapter<List<String>> {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        return moshi.adapter(type)
    }
}