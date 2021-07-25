package com.epicdima.stockfly.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.epicdima.stockfly.model.Company

@JsonClass(generateAdapter = true)
data class CompanyDto(
    val ticker: String,
    val name: String,
    val country: String,
    val currency: String,
    @Json(name = "weburl")
    val webUrl: String,
    @Json(name = "logo")
    val logoUrl: String,
    val exchange: String,
    val ipo: String,
    val marketCapitalization: Double,
    val shareOutstanding: Double,
    val phone: String,
)


fun CompanyDto.toModel(): Company {
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
        phone
    )
}