package ru.yandex.stockfly.db

import ru.yandex.stockfly.db.dao.CompanyDao
import ru.yandex.stockfly.db.dao.NewsItemDao

interface Database {
    fun companyDao(): CompanyDao
    fun newsItemDao(): NewsItemDao
}

