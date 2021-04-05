package ru.yandex.stockfly.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.yandex.stockfly.db.dao.CompanyDao
import ru.yandex.stockfly.db.dao.NewsItemDao
import ru.yandex.stockfly.db.dao.RecommendationDao
import ru.yandex.stockfly.db.entity.CompanyEntity
import ru.yandex.stockfly.db.entity.NewsItemEntity
import ru.yandex.stockfly.db.entity.RecommendationEntity

private const val DATABASE_NAME = "stockfly.db"

@Database(
    version = 5,
    entities = [CompanyEntity::class, NewsItemEntity::class, RecommendationEntity::class]
)
abstract class AppDatabase : RoomDatabase(), ru.yandex.stockfly.db.Database {
    abstract override fun companyDao(): CompanyDao
    abstract override fun newsItemDao(): NewsItemDao
    abstract override fun recommendationDao(): RecommendationDao
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
