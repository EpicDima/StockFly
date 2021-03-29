package ru.yandex.stockfly.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.yandex.stockfly.model.Company

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
//    val exchange: String,
//    val ipo: String,
//    val marketCapitalization: Long,
//    val shareOutstanding: Double,
//    val phone: String,
)


fun CompanyDto.toModel(): Company {
    return Company(
        ticker,
        name,
        country,
        currency,
        webUrl,
        logoUrl,
//        exchange,
//        ipo,
//        marketCapitalization,
//        shareOutstanding,
//        phone
    )
}