package com.epicdima.stockfly.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.epicdima.stockfly.core.database.dao.CompanyDao
import com.epicdima.stockfly.core.database.dao.NewsItemDao
import com.epicdima.stockfly.core.database.dao.RecommendationDao
import com.epicdima.stockfly.core.database.dao.StockCandlesDao
import com.epicdima.stockfly.core.database.entity.CompanyEntity
import com.epicdima.stockfly.core.database.entity.NewsItemEntity
import com.epicdima.stockfly.core.database.entity.RecommendationEntity
import com.epicdima.stockfly.core.database.entity.StockCandleItemEntity
import com.epicdima.stockfly.core.database.other.StockCandleParamConverter

@androidx.room.Database(
    version = 9,
    entities = [
        CompanyEntity::class,
        NewsItemEntity::class,
        RecommendationEntity::class,
        StockCandleItemEntity::class
    ]
)
@TypeConverters(StockCandleParamConverter::class)
internal abstract class AppDatabase : RoomDatabase(), Database {

    companion object {
        fun buildDatabase(context: Context): AppDatabase = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "stockfly.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    abstract override fun companyDao(): CompanyDao

    abstract override fun newsItemDao(): NewsItemDao

    abstract override fun recommendationDao(): RecommendationDao

    abstract override fun stockCandlesDao(): StockCandlesDao
}