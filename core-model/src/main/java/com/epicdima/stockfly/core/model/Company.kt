package com.epicdima.stockfly.core.model

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
)