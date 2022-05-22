package com.epicdima.stockfly.core.database.other

import androidx.room.TypeConverter
import com.epicdima.stockfly.core.model.StockCandleParam

class StockCandleParamConverter {

    companion object {
        private val VALUES = StockCandleParam.values()
    }

    @TypeConverter
    fun toObject(value: Int): StockCandleParam {
        return VALUES[value]
    }

    @TypeConverter
    fun fromObject(value: StockCandleParam): Int {
        return value.ordinal
    }
}