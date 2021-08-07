package com.epicdima.stockfly.db

import com.epicdima.stockfly.db.dao.CompanyDao
import com.epicdima.stockfly.db.dao.NewsItemDao
import com.epicdima.stockfly.db.dao.RecommendationDao
import com.epicdima.stockfly.db.dao.StockCandlesDao

interface Database {

    fun companyDao(): CompanyDao

    fun newsItemDao(): NewsItemDao

    fun recommendationDao(): RecommendationDao

    fun stockCandlesDao(): StockCandlesDao
}