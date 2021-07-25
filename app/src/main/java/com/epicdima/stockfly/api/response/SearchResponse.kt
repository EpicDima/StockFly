package com.epicdima.stockfly.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.epicdima.stockfly.model.Company

@JsonClass(generateAdapter = true)
data class SearchResponse(
    val result: List<SearchDto>
)


@JsonClass(generateAdapter = true)
data class SearchDto(
    @Json(name = "symbol")
    val ticker: String,
    @Json(name = "description")
    val name: String
)


fun SearchDto.toModel(): Company {
    return Company(ticker = ticker, name = name)
}