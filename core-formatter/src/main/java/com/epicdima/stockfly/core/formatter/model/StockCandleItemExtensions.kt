package com.epicdima.stockfly.core.formatter.model

import com.epicdima.stockfly.core.formatter.FORMAT_PRICE
import com.epicdima.stockfly.core.model.StockCandleItem
import java.text.SimpleDateFormat
import java.util.*

val StockCandleItem.priceString: String
    get() = FORMAT_PRICE.format(price)

fun StockCandleItem.getTimestampString(format: SimpleDateFormat): String {
    return format.format(Date(timestamp * 1000L)).replaceFirstChar { it.titlecase() }
}