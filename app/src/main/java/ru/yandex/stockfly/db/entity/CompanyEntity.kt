package ru.yandex.stockfly.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import ru.yandex.stockfly.model.Company
import ru.yandex.stockfly.model.Quote

@Entity(tableName = "companies", primaryKeys = ["ticker"])
data class CompanyEntity(
    val ticker: String = "",
    val name: String = "",
    val country: String = "",
    val currency: String = "",
    val webUrl: String = "",
    val logoUrl: String = "",
    val exchange: String = "",
    val ipo: String = "",
    val marketCapitalization: Double = 0.0,
    val shareOutstanding: Double = 0.0,
    val phone: String = "",
    @Embedded
    val quote: QuoteEntity? = null,
    val favourite: Boolean = false,
    val favouriteNumber: Int = 0,
)


fun CompanyEntity.toModel(): Company {
    return Company(
        ticker,
        name,
        country,
        currency,
        webUrl,
        logoUrl,
        exchange,
        ipo,
        marketCapitalization,
        shareOutstanding,
        phone,
        quote?.toModel(),
        favourite,
        favouriteNumber
    )
}

fun Company.toEntity(): CompanyEntity {
    return CompanyEntity(
        ticker,
        name,
        country,
        currency,
        webUrl,
        logoUrl,
        exchange,
        ipo,
        marketCapitalization,
        shareOutstanding,
        phone,
        quote?.toEntity(),
        favourite,
        favouriteNumber
    )
}


data class QuoteEntity(
    val current: Double = 0.0,
    val open: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val previousClose: Double = 0.0
)


fun QuoteEntity.toModel(): Quote {
    return Quote(current, open, high, low, previousClose)
}

fun Quote.toEntity(): QuoteEntity {
    return QuoteEntity(current, open, high, low, previousClose)
}
