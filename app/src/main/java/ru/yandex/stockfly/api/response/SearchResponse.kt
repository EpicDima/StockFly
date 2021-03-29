package ru.yandex.stockfly.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.yandex.stockfly.model.SearchItem

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


fun SearchDto.toModel(): SearchItem {
    return SearchItem(ticker, name)
}