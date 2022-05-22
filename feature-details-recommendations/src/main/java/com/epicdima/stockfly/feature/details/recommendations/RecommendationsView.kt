package com.epicdima.stockfly.feature.details.recommendations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.epicdima.stockfly.core.formatter.model.periodFormatted
import com.epicdima.stockfly.core.formatter.toLocalString
import com.epicdima.stockfly.core.model.Recommendation
import com.epicdima.stockfly.core.ui.color
import com.epicdima.stockfly.core.ui.darken
import com.epicdima.stockfly.core.ui.dpToPx
import com.epicdima.stockfly.core.ui.spToPx
import com.epicdima.stockfly.core.utils.Quintuple

private typealias PaintQuintuple = Quintuple<Paint, Paint, Paint, Paint, Paint>
private typealias StringQuintuple = Quintuple<String, String, String, String, String>
private typealias ValuesTextSizes = Quintuple<Pair<Int, Int>, Pair<Int, Int>, Pair<Int, Int>, Pair<Int, Int>, Pair<Int, Int>>

private const val NO_POSITION = -1

class RecommendationsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    // все числа лучше вынести и также лучше сделать аттрибуты для этой вьюшки

    private val maxBarContentWidth = 48.0f.dpToPx(context)
    private val suggestionStartEndPadding = 16.0f.dpToPx(context)
    private val suggestionTopBottomPadding = 12.0f.dpToPx(context)
    private val suggestionBorderRadius = 16.0f.dpToPx(context)
    private val suggestionDateValueTextDistance = 16.0f.dpToPx(context)
    private val suggestionInterTextDistance = 10.0f.dpToPx(context)
    private val suggestionShadowSize = 4.0f.dpToPx(context)
    private val suggestionShadowDown = 4.0f.dpToPx(context)
    private val suggestionNear = 2.0f.dpToPx(context)

    private val noDataTextSize = 18.0f.spToPx(context)
    private val suggestionDateTextSize = 12.0f.spToPx(context)
    private val suggestionValueTextSize = 12.0f.spToPx(context)

    private val noDataColor = context.color(com.epicdima.stockfly.core.ui.R.color.black)
    private val strongBuyColor = context.color(com.epicdima.stockfly.core.ui.R.color.persian_green)
    private val buyColor = context.color(com.epicdima.stockfly.core.ui.R.color.shamrock)
    private val holdColor = context.color(com.epicdima.stockfly.core.ui.R.color.ripe_lemon)
    private val sellColor = context.color(com.epicdima.stockfly.core.ui.R.color.ryb_orange)
    private val strongSellColor =
        context.color(com.epicdima.stockfly.core.ui.R.color.deep_carmine_pink)
    private val unselectedStrongBuyColor = strongBuyColor.darken()
    private val unselectedBuyColor = buyColor.darken()
    private val unselectedHoldColor = holdColor.darken()
    private val unselectedSellColor = sellColor.darken()
    private val unselectedStrongSellColor = strongSellColor.darken()
    private val suggestionColor = context.color(com.epicdima.stockfly.core.ui.R.color.black)
    private val suggestionShadowColor =
        context.color(com.epicdima.stockfly.core.ui.R.color.dark_translucent)
    private val suggestionTextColor = context.color(com.epicdima.stockfly.core.ui.R.color.white)

    private val noDataText = context.getString(R.string.no_recommendations)

    private val noDataPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = noDataColor
        textSize = noDataTextSize
        textAlign = Paint.Align.CENTER
        typeface =
            ResourcesCompat.getFont(context, com.epicdima.stockfly.core.ui.R.font.montserrat_bold)
    }
    private val defaultBarPalette =
        com.epicdima.stockfly.feature.details.recommendations.PaintQuintuple(
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = strongBuyColor },
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = buyColor },
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = holdColor },
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = sellColor },
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = strongSellColor },
        )
    private val unselectedBarPalette =
        com.epicdima.stockfly.feature.details.recommendations.PaintQuintuple(
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = unselectedStrongBuyColor },
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = unselectedBuyColor },
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = unselectedHoldColor },
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = unselectedSellColor },
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = unselectedStrongSellColor },
        )
    private val suggestionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = suggestionColor
        setShadowLayer(suggestionShadowSize, 0.0f, suggestionShadowDown, suggestionShadowColor)
    }
    private val suggestionDatePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = suggestionTextColor
        textSize = suggestionDateTextSize
        textAlign = Paint.Align.CENTER
        typeface = ResourcesCompat.getFont(
            context,
            com.epicdima.stockfly.core.ui.R.font.montserrat_semibold
        )
    }
    private val suggestionValuePalette =
        com.epicdima.stockfly.feature.details.recommendations.PaintQuintuple(
            getValueTextPaint().apply { color = strongBuyColor },
            getValueTextPaint().apply { color = buyColor },
            getValueTextPaint().apply { color = holdColor },
            getValueTextPaint().apply { color = sellColor },
            getValueTextPaint().apply { color = strongSellColor },
        )

    private val textBoundRect = Rect()

    private var recommendations: List<Recommendation>? = null

    private var max: Double = 0.0
    private var length: Int = 0
    private var barWidth: Double = 0.0
    private var barContentWidth: Double = 0.0
    private var barSidePadding: Double = 0.0
    private var partHeight: Double = 0.0

    private var selectedIndex = NO_POSITION

    private lateinit var formatter: com.epicdima.stockfly.core.formatter.Formatter

    fun setFormatter(formatter: com.epicdima.stockfly.core.formatter.Formatter) {
        this.formatter = formatter
    }

    fun updateData(
        newRecommendations: List<Recommendation>?,
        withResetSelectedIndex: Boolean = true
    ) {
        recommendations = newRecommendations
        max = recommendations?.maxOfOrNull { it.sum.toDouble() } ?: 0.0
        length = recommendations?.size ?: 0
        calculateBarSizes()
        if (withResetSelectedIndex) {
            selectedIndex = NO_POSITION
        }
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateBarSizes()
    }

    private fun calculateBarSizes() {
        if (length > 0) {
            barWidth = (width / length).toDouble()
            barContentWidth = barWidth * 0.8
            if (barContentWidth > maxBarContentWidth) {
                barContentWidth = maxBarContentWidth.toDouble()
                barSidePadding = (barWidth - barContentWidth) * 0.5
            } else {
                barSidePadding = barWidth * 0.1
            }
            partHeight = height / max
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!recommendations.isNullOrEmpty()) {
            if (selectedIndex == NO_POSITION) {
                recommendations?.forEachIndexed { i, rec -> drawDefaultBar(i, rec, canvas) }
            } else {
                recommendations?.let {
                    it.forEachIndexed { i, rec -> drawUnselectedBar(i, rec, canvas) }
                    drawDefaultBar(selectedIndex, it[selectedIndex], canvas)
                    drawSuggestion(it[selectedIndex], canvas)
                }
            }
        } else {
            drawNoData(canvas)
        }
    }

    private fun drawNoData(canvas: Canvas) {
        canvas.drawText(noDataText, width * 0.5f, height * 0.5f, noDataPaint)
    }

    private fun drawDefaultBar(index: Int, recommendation: Recommendation, canvas: Canvas) {
        drawBar(index, recommendation, defaultBarPalette, canvas)
    }

    private fun drawUnselectedBar(index: Int, recommendation: Recommendation, canvas: Canvas) {
        drawBar(index, recommendation, unselectedBarPalette, canvas)
    }

    private fun drawBar(
        index: Int,
        recommendation: Recommendation,
        palette: PaintQuintuple,
        canvas: Canvas
    ) {
        val left = (index * barWidth + barSidePadding).toFloat()
        val right = (left + barContentWidth).toFloat()
        var y = height.toFloat()
        y = drawBarPart(left, right, y, recommendation.strongSell, palette.fifth, canvas)
        y = drawBarPart(left, right, y, recommendation.sell, palette.fourth, canvas)
        y = drawBarPart(left, right, y, recommendation.hold, palette.third, canvas)
        y = drawBarPart(left, right, y, recommendation.buy, palette.second, canvas)
        drawBarPart(left, right, y, recommendation.strongBuy, palette.first, canvas)
    }

    private fun drawBarPart(
        left: Float,
        right: Float,
        y: Float,
        value: Int,
        paint: Paint,
        canvas: Canvas
    ): Float {
        val height = (partHeight * value).toFloat()
        canvas.drawRect(left, y - height, right, y, paint)
        return y - height
    }

    private fun drawSuggestion(recommendation: Recommendation, canvas: Canvas) {
        val date = recommendation.periodFormatted(formatter)
        val stringValues = getStringValues(recommendation, formatter)

        val (dateWidth, dateHeight) = suggestionDatePaint.getTextWidthAndHeight(date)
        val valuesSizes = getValuesTextSizes(stringValues)

        val suggestionSizes = calculateSuggestionSizes(dateWidth, dateHeight, valuesSizes)

        var (x, y) = drawSuggestionRect(recommendation, suggestionSizes, canvas)
        y = drawDate(x, y, date, dateWidth, dateHeight, canvas)
        y = drawValue(
            x,
            y,
            stringValues.first,
            valuesSizes.first.second,
            suggestionValuePalette.first,
            canvas
        )
        y = drawValue(
            x,
            y,
            stringValues.second,
            valuesSizes.second.second,
            suggestionValuePalette.second,
            canvas
        )
        y = drawValue(
            x,
            y,
            stringValues.third,
            valuesSizes.third.second,
            suggestionValuePalette.third,
            canvas
        )
        y = drawValue(
            x,
            y,
            stringValues.fourth,
            valuesSizes.fourth.second,
            suggestionValuePalette.fourth,
            canvas
        )
        drawValue(
            x,
            y,
            stringValues.fifth,
            valuesSizes.fifth.second,
            suggestionValuePalette.fifth,
            canvas
        )
    }

    private fun getStringValues(
        recommendation: Recommendation,
        formatter: com.epicdima.stockfly.core.formatter.Formatter
    ): StringQuintuple {
        return com.epicdima.stockfly.feature.details.recommendations.StringQuintuple(
            context.getString(
                R.string.strong_buy_value,
                recommendation.strongBuy.toLocalString(formatter)
            ),
            context.getString(R.string.buy_value, recommendation.buy.toLocalString(formatter)),
            context.getString(R.string.hold_value, recommendation.hold.toLocalString(formatter)),
            context.getString(R.string.sell_value, recommendation.sell.toLocalString(formatter)),
            context.getString(
                R.string.strong_sell_value,
                recommendation.strongSell.toLocalString(formatter)
            )
        )
    }

    private fun getValuesTextSizes(stringValues: StringQuintuple): ValuesTextSizes {
        return com.epicdima.stockfly.feature.details.recommendations.ValuesTextSizes(
            suggestionValuePalette.first.getTextWidthAndHeight(stringValues.first),
            suggestionValuePalette.second.getTextWidthAndHeight(stringValues.second),
            suggestionValuePalette.third.getTextWidthAndHeight(stringValues.third),
            suggestionValuePalette.fourth.getTextWidthAndHeight(stringValues.fourth),
            suggestionValuePalette.fifth.getTextWidthAndHeight(stringValues.fifth)
        )
    }

    private fun calculateSuggestionSizes(
        dateWidth: Int,
        dateHeight: Int,
        valuesSizes: ValuesTextSizes
    ): Pair<Float, Float> {
        val suggestionWidth = suggestionStartEndPadding * 2 + maxOf(
            dateWidth,
            valuesSizes.first.first,
            valuesSizes.second.first,
            valuesSizes.third.first,
            valuesSizes.fourth.first,
            valuesSizes.fifth.first
        )
        val suggestionHeight =
            suggestionTopBottomPadding * 2 + suggestionDateValueTextDistance + suggestionInterTextDistance * 4 + dateHeight + valuesSizes.first.second + valuesSizes.second.second + valuesSizes.third.second + valuesSizes.fourth.second + valuesSizes.fifth.second
        return Pair(suggestionWidth, suggestionHeight)
    }

    private fun drawSuggestionRect(
        recommendation: Recommendation,
        suggestionSizes: Pair<Float, Float>,
        canvas: Canvas
    ): Pair<Float, Float> {
        val x = (selectedIndex * barWidth + 0.5f * barWidth).toFloat()
        val y = (height - 0.5f * recommendation.sum * partHeight).toFloat()
        val (suggestionRect, yNew) = calculateSuggestionRect(
            x,
            y,
            suggestionSizes.first,
            suggestionSizes.second
        )
        canvas.drawRoundRect(
            suggestionRect,
            suggestionBorderRadius,
            suggestionBorderRadius,
            suggestionPaint
        )
        return Pair(suggestionRect.left + suggestionStartEndPadding, yNew)
    }

    private fun calculateSuggestionRect(
        x: Float,
        y: Float,
        suggestionWidth: Float,
        suggestionHeight: Float
    ): Pair<RectF, Float> {
        val near = barContentWidth * 0.5f + suggestionNear
        var left = x - near - suggestionWidth
        var top = y - suggestionHeight * 0.5f
        var right = x - near
        var bottom = y + suggestionHeight * 0.5f

        if (bottom > height) {
            bottom = height - suggestionHeight * 0.2f
            top = height - suggestionHeight - suggestionHeight * 0.2f
        } else if (top < 0.0f) {
            bottom = suggestionHeight * 1.2f
            top = suggestionHeight * 0.2f
        }
        if (left < 0.0f) {
            left = x + near
            right = left + suggestionWidth
        }
        return Pair(
            RectF(left.toFloat(), top, right.toFloat(), bottom),
            top + suggestionTopBottomPadding
        )
    }

    private fun drawDate(
        x: Float,
        y: Float,
        date: String,
        dateWidth: Int,
        dateHeight: Int,
        canvas: Canvas
    ): Float {
        canvas.drawText(
            date,
            x + dateWidth * 0.5f,
            y + dateHeight * 0.5f - (suggestionDatePaint.descent() + suggestionDatePaint.ascent()) * 0.5f,
            suggestionDatePaint
        )
        return y + dateHeight + suggestionDateValueTextDistance
    }

    private fun drawValue(
        x: Float,
        y: Float,
        value: String,
        valueHeight: Int,
        paint: Paint,
        canvas: Canvas
    ): Float {
        canvas.drawText(value, x, y - (paint.descent() + paint.ascent()) * 0.5f, paint)
        return y + valueHeight + suggestionInterTextDistance
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            if (length > 0) {
                val newSelectedIndex = event.x.fromRealX()
                if (selectedIndex != newSelectedIndex) {
                    if (newSelectedIndex < 0 || newSelectedIndex >= length) {
                        selectedIndex = NO_POSITION
                    } else {
                        selectedIndex = newSelectedIndex
                        invalidate()
                        return performClick()
                    }
                }
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            if (selectedIndex != NO_POSITION) {
                selectedIndex = NO_POSITION
                invalidate()
                return performClick()
            }
        }
        return false
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun getValueTextPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = suggestionValueTextSize
            textAlign = Paint.Align.LEFT
            typeface = ResourcesCompat.getFont(
                context,
                com.epicdima.stockfly.core.ui.R.font.montserrat_semibold
            )
        }
    }


    private fun Float.fromRealX(): Int {
        val value = (this / barWidth).toInt()
        return if (value >= length) length - 1 else value
    }

    private fun Paint.getTextWidthAndHeight(text: String): Pair<Int, Int> {
        getTextBounds(text, 0, text.length, textBoundRect)
        return Pair(textBoundRect.width(), textBoundRect.height())
    }
}