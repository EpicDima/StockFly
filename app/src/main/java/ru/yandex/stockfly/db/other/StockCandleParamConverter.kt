package ru.yandex.stockfly.db.other

import androidx.room.TypeConverter
import ru.yandex.stockfly.other.StockCandleParam

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