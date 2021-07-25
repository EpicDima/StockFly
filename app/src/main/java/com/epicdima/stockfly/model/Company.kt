package com.epicdima.stockfly.model

import java.util.*

data class Company(
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
    val quote: Quote? = null,
    val favourite: Boolean = false,
    val favouriteNumber: Int = 0,
) {
    val currentString: String
        get() = quote?.currentString ?: ""

    val changeString: String
        get() = quote?.changeString ?: ""

    val changePercentString: String
        get() = quote?.changePercentString ?: ""

    val countryName: String
        get() = Locale("", country).displayCountry
}
