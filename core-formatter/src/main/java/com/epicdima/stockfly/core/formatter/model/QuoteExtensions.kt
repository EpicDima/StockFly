package com.epicdima.stockfly.core.formatter.model

import com.epicdima.stockfly.core.formatter.FORMAT_CHANGE
import com.epicdima.stockfly.core.formatter.FORMAT_PRICE
import com.epicdima.stockfly.core.formatter.Formatter
import com.epicdima.stockfly.core.model.Quote

val Quote.currentString: String
    get() = FORMAT_PRICE.format(current)

val Quote.changeString: String
    get() {
        val result = FORMAT_CHANGE.format(change)
        return if ("+0" == result) "0" else result
    }

fun Quote.changePercentString(formatter: Formatter): String {
    return formatter.percentFormat(changePercent)
}