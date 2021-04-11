package ru.yandex.stockfly.db

import ru.yandex.stockfly.db.dao.CompanyDao
import ru.yandex.stockfly.db.dao.NewsItemDao
import ru.yandex.stockfly.db.dao.RecommendationDao
import ru.yandex.stockfly.db.dao.StockCandlesDao

interface Database {
    fun companyDao(): CompanyDao
    fun newsItemDao(): NewsItemDao
    fun recommendationDao(): RecommendationDao
    fun stockCandlesDao(): StockCandlesDao
}

