package com.epicdima.stockfly.core.database

import com.epicdima.stockfly.core.database.dao.CompanyDao
import com.epicdima.stockfly.core.database.dao.NewsItemDao
import com.epicdima.stockfly.core.database.dao.RecommendationDao
import com.epicdima.stockfly.core.database.dao.StockCandlesDao

interface Database {

    fun companyDao(): CompanyDao

    fun newsItemDao(): NewsItemDao

    fun recommendationDao(): RecommendationDao

    fun stockCandlesDao(): StockCandlesDao
}