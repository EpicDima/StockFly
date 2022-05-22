package com.epicdima.stockfly.core.formatter

fun Double.toLocalString(formatter: Formatter): String {
    return formatter.numberFormat(this)
}

fun Int.toLocalString(formatter: Formatter): String {
    return formatter.numberFormat(this)
}