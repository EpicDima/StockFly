package com.epicdima.stockfly.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.epicdima.stockfly.db.dao.CompanyDao
import com.epicdima.stockfly.db.dao.NewsItemDao
import com.epicdima.stockfly.db.dao.RecommendationDao
import com.epicdima.stockfly.db.dao.StockCandlesDao
import com.epicdima.stockfly.db.entity.CompanyEntity
import com.epicdima.stockfly.db.entity.NewsItemEntity
import com.epicdima.stockfly.db.entity.RecommendationEntity
import com.epicdima.stockfly.db.entity.StockCandleItemEntity
import com.epicdima.stockfly.db.other.StockCandleParamConverter

private const val DATABASE_NAME = "stockfly.db"

@Database(
    version = 9,
    entities = [
        CompanyEntity::class,
        NewsItemEntity::class,
        RecommendationEntity::class,
        StockCandleItemEntity::class
    ]
)
@TypeConverters(StockCandleParamConverter::class)
abstract class AppDatabase : RoomDatabase(), com.epicdima.stockfly.db.Database {
    abstract override fun companyDao(): CompanyDao
    abstract override fun newsItemDao(): NewsItemDao
    abstract override fun recommendationDao(): RecommendationDao
    abstract override fun stockCandlesDao(): StockCandlesDao
}

fun buildDatabase(context: Context): AppDatabase {
    // Так как используется Hilt (Dagger), следовательно не будет создаваться больше одного
    // экземпляра базы данных, то есть нет смысла делать синглтон внутри этого класса,
    // как например это делается во многих туториалах
    return Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .build()
}
