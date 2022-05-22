package com.epicdima.stockfly.core.formatter.model

import com.epicdima.stockfly.core.formatter.FORMAT_DATE_API
import com.epicdima.stockfly.core.formatter.Formatter
import com.epicdima.stockfly.core.model.Company
import java.text.DateFormat
import java.text.ParseException
import java.util.*

fun Company.ipoLocalDateString(formatter: Formatter): String {
    return try {
        val ipoDate = FORMAT_DATE_API.parse(ipo)!!
        DateFormat.getDateInstance(DateFormat.MEDIUM, formatter.currentLocale).format(ipoDate)
    } catch (e: ParseException) {
        ""
    }
}

fun Company.changePercentString(formatter: Formatter): String {
    return quote?.changePercentString(formatter) ?: ""
}

val Company.countryName: String
    get() = Locale("", country).displayCountry

val Company.currentString: String
    get() = quote?.currentString ?: ""

val Company.changeString: String
    get() = quote?.changeString ?: ""