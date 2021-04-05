package ru.yandex.stockfly.model

import ru.yandex.stockfly.other.FORMAT_DATE_API
import ru.yandex.stockfly.other.FORMAT_PERIOD_DATE
import java.util.*

data class Recommendation(
    val period: String = "",
    val strongBuy: Int = 0,
    val buy: Int = 0,
    val hold: Int = 0,
    val sell: Int = 0,
    val strongSell: Int = 0,
) {
    private val periodDate: Date
        get() = FORMAT_DATE_API.parse(period) ?: Date(0)

    val periodFormatted: String
        get() = FORMAT_PERIOD_DATE.format(periodDate).capitalize(Locale.ROOT)

    val sum: Int
        get() = strongBuy + buy + hold + sell + strongSell
}
