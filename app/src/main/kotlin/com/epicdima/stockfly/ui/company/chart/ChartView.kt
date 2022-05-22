package com.epicdima.stockfly.ui.company.chart

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import com.epicdima.stockfly.R
import com.epicdima.stockfly.core.formatter.model.getTimestampString
import com.epicdima.stockfly.core.formatter.model.priceString
import com.epicdima.stockfly.core.model.StockCandles
import com.epicdima.stockfly.core.ui.color
import com.epicdima.stockfly.core.ui.dpToPx
import com.epicdima.stockfly.core.ui.drawable
import com.epicdima.stockfly.core.ui.spToPx
import com.epicdima.stockfly.core.utils.Quadruple
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

private const val NO_POSITION = -1

class ChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    // все числа лучше вынести и также лучше сделать аттрибуты для этой вьюшки

    private val lengthAnimationDuration = 600L

    private var lengthAnimator: ValueAnimator? = null

    private var realLength: Int = 0

    private val lineWidth = 2.0f.dpToPx(context)
    private val circleRadius = 4.0f.dpToPx(context)
    private val circleBorderSize = 2.0f.dpToPx(context)
    private val circleShadowSize = 4.0f.dpToPx(context)
    private val chartPaddingSize = circleRadius + circleBorderSize + circleShadowSize
    private val suggestionStartEndPadding = 16.0f.dpToPx(context)
    private val suggestionTopBottomPadding = 12.0f.dpToPx(context)
    private val suggestionBorderRadius = 16.0f.dpToPx(context)
    private val suggestionToPointDistance = 36.0f.dpToPx(context)
    private val suggestionPriceToDateTimeDistance = 6.0f.dpToPx(context)
    private val suggestionTriangleWidth = 12.0f.dpToPx(context)
    private val suggestionTriangleHeight = 6.0f.dpToPx(context)
    private val suggestionTrianlePointDistance =
        suggestionToPointDistance - suggestionTriangleHeight
    private val suggestionShadowSize = 4.0f.dpToPx(context)
    private val suggestionShadowDown = 4.0f.dpToPx(context)

    private val noDataTextSize = 18.0f.spToPx(context)
    private val suggestionPriceTextSize = 16.0f.spToPx(context)
    private val suggestionDateTimeTextSize = 12.0f.spToPx(context)

    private val noDataColor = context.color(R.color.black)
    private val lineColor = context.color(R.color.black)
    private val startGradientColor = context.color(R.color.grey)
    private val endGradientColor = context.color(R.color.white)
    private val circleColor = context.color(R.color.black)
    private val circleBorderColor = context.color(R.color.light)
    private val circleShadowColor = context.color(R.color.darker_translucent)
    private val suggestionColor = context.color(R.color.black)
    private val suggestionShadowColor = context.color(R.color.dark_translucent)
    private val suggestionPriceColor = context.color(R.color.white)
    private val suggestionDateTimeColor = context.color(R.color.dark)

    private val noDataText = context.getString(R.string.no_data)

    private val noDataPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = noDataColor
        textSize = noDataTextSize
        textAlign = Paint.Align.CENTER
        typeface = ResourcesCompat.getFont(context, R.font.montserrat_bold)
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        color = lineColor
    }
    private val lineGradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val circleFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = circleColor
    }
    private val circleStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = circleBorderSize
        color = circleBorderColor
        setShadowLayer(circleShadowSize, 0.0f, 0.0f, circleShadowColor)
    }

    private val suggestionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = suggestionColor
        setShadowLayer(suggestionShadowSize, 0.0f, suggestionShadowDown, suggestionShadowColor)
    }
    private val suggestionPricePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = suggestionPriceColor
        textSize = suggestionPriceTextSize
        textAlign = Paint.Align.CENTER
        typeface = ResourcesCompat.getFont(context, R.font.montserrat_semibold)
    }
    private val suggestionDateTimePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = suggestionDateTimeColor
        textSize = suggestionDateTimeTextSize
        textAlign = Paint.Align.CENTER
        typeface = ResourcesCompat.getFont(context, R.font.montserrat_semibold)
    }

    private val arrowDownIcon = context.drawable(R.drawable.ic_arrow_down)
    private val arrowUpIcon = context.drawable(R.drawable.ic_arrow_up)

    private val linePath = Path()
    private val gradientPath = Path()
    private val textBoundRect = Rect()

    private var data: StockCandles? = null
    private var dateTimeFormat = SimpleDateFormat.getDateTimeInstance(
        SimpleDateFormat.DEFAULT,
        SimpleDateFormat.DEFAULT,
        Locale.US
    ) as SimpleDateFormat

    private var min: Double = 0.0
    private var max: Double = 0.0
    private var length: Int = 0

    private var selectedIndex = NO_POSITION

    fun updateData(
        newData: StockCandles?,
        withAnimation: Boolean = true,
        withResetSelectedIndex: Boolean = true
    ) {
        data = newData
        min = data?.price?.minOfOrNull { it } ?: 0.0
        max = data?.price?.maxOfOrNull { it } ?: 0.0
        length = data?.price?.size ?: 0
        if (withResetSelectedIndex) {
            selectedIndex = NO_POSITION
        }
        if (length > 1 && withAnimation) {
            startAnimation()
        } else {
            if (lengthAnimator == null || lengthAnimator?.isRunning != true) {
                realLength = length
                invalidate()
            } else {
                if (length < realLength) {
                    invalidate()
                } else {
                    startAnimation(realLength, lengthAnimator?.currentPlayTime ?: 0)
                }
            }
        }
    }

    fun updateFormat(format: SimpleDateFormat) {
        dateTimeFormat = format
    }

    private fun startAnimation(start: Int = 2, currentAnimationTime: Long = 0L) {
        lengthAnimator?.cancel()
        lengthAnimator = ValueAnimator.ofInt(start, length).apply {
            duration = (lengthAnimationDuration - currentAnimationTime).coerceAtLeast(0)
            addUpdateListener {
                realLength = it.animatedValue as Int
                invalidate()
            }
        }
        lengthAnimator?.doOnEnd {
            realLength = length
            invalidate()
        }
        lengthAnimator?.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        changeLineGradient()
    }

    private fun changeLineGradient() {
        lineGradientPaint.shader = LinearGradient(
            0.0f,
            0.0f,
            0.0f,
            height.toFloat(),
            startGradientColor,
            endGradientColor,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        data.let { stockCandle ->
            if (stockCandle != null && length > 1) {
                drawLine(stockCandle.price, canvas)
                drawGradient(canvas)
                if (selectedIndex != NO_POSITION) {
                    drawSuggestion(stockCandle, canvas)
                }
                linePath.reset()
                gradientPath.reset()
            } else {
                drawNoData(canvas)
            }
        }
    }

    private fun drawNoData(canvas: Canvas) {
        canvas.drawText(noDataText, width * 0.5f, height * 0.5f, noDataPaint)
    }

    private fun drawLine(values: DoubleArray, canvas: Canvas) {
        linePath.moveTo(0.toRealX(), values.first().toRealY())
        for (i in 1 until realLength - 1) {
            linePath.lineTo(i.toRealX(), values[i].toRealY())
        }
        linePath.lineTo((realLength - 1).toRealX(), values[realLength - 1].toRealY())
        canvas.drawPath(linePath, linePaint)
    }

    private fun drawGradient(canvas: Canvas) {
        gradientPath.set(linePath)
        gradientPath.lineTo((realLength - 1).toRealX(), height.toFloat())
        gradientPath.lineTo(0.0f, height.toFloat())
        gradientPath.close()
        canvas.drawPath(gradientPath, lineGradientPaint)
    }

    private fun drawSuggestion(stockCandles: StockCandles, canvas: Canvas) {
        val stockCandleItem = stockCandles.getItem(selectedIndex)

        val x = selectedIndex.toRealX()
        val y = stockCandleItem.price.toRealY()

        val price = stockCandleItem.priceString
        val dateTime = stockCandleItem.getTimestampString(dateTimeFormat)

        val (priceWidth, priceHeight) = suggestionPricePaint.getTextWidthAndHeight(price)
        val (dateTimeWidth, dateTimeHeight) = suggestionDateTimePaint.getTextWidthAndHeight(
            dateTime
        )

        val suggestionWidth = maxOf(priceWidth, dateTimeWidth) + suggestionStartEndPadding * 2
        val suggestionHeight =
            priceHeight + dateTimeHeight + suggestionTopBottomPadding * 2 + suggestionPriceToDateTimeDistance

        val (suggestionRect, xNew, directionDown, showTriangle) = calculateSuggestionRect(
            x,
            y,
            suggestionWidth,
            suggestionHeight
        )

        drawCircle(x, y, canvas)
        drawSuggestionRectWithArrow(xNew, y, directionDown, showTriangle, suggestionRect, canvas)
        drawPrice(xNew, y, price, priceHeight, dateTimeHeight, canvas, directionDown)
        drawDateTime(xNew, y, dateTime, priceHeight, dateTimeHeight, canvas, directionDown)
    }

    private fun drawCircle(x: Float, y: Float, canvas: Canvas) {
        canvas.drawCircle(x, y, circleRadius, circleFillPaint)
        canvas.drawCircle(x, y, circleRadius, circleStrokePaint)
    }

    private fun drawSuggestionTriangle(x: Float, y: Float, canvas: Canvas, directionDown: Boolean) {
        val (top, bottom) = if (directionDown) {
            Pair(
                (y - suggestionTrianlePointDistance - suggestionTriangleHeight).toInt(),
                (y - suggestionTrianlePointDistance).toInt()
            )
        } else {
            // +1 из-за несимметричной картинки, иначе иногда просвечивается полоска
            // в один пиксель на стыке стрелки и прямоугольника
            Pair(
                (y + suggestionTrianlePointDistance + 1).toInt(),
                (y + suggestionTrianlePointDistance + suggestionTriangleHeight + 1).toInt()
            )
        }
        val rect = Rect(
            (x - suggestionTriangleWidth * 0.5).toInt(),
            top,
            (x + suggestionTriangleWidth * 0.5).toInt(),
            bottom
        )

        if (directionDown) {
            arrowDownIcon.bounds = rect
            arrowDownIcon.draw(canvas)
        } else {
            arrowUpIcon.bounds = rect
            arrowUpIcon.draw(canvas)
        }
    }

    private fun calculateSuggestionRect(
        x: Float,
        y: Float,
        suggestionWidth: Float,
        suggestionHeight: Float
    ): Quadruple<RectF, Float, Boolean, Boolean> {
        var left = x - suggestionWidth * 0.5f
        var top = y - suggestionToPointDistance - suggestionHeight
        var right = x + suggestionWidth * 0.5f
        var bottom = y - suggestionToPointDistance

        var xNew = x
        var directionDown = true
        var showTriangle = true

        if (left < 0.0f) {
            xNew = suggestionWidth * 0.5f
            left = 0.0f
            right = suggestionWidth
            showTriangle = false
        } else if (right > width) {
            xNew = width - suggestionWidth * 0.5f
            left = width - suggestionWidth
            right = width.toFloat()
            showTriangle = false
        }
        if (top < 0.0f) {
            directionDown = false
            top = y + suggestionToPointDistance + suggestionHeight
            bottom = y + suggestionToPointDistance
        }
        return Quadruple(RectF(left, top, right, bottom), xNew, directionDown, showTriangle)
    }

    private fun drawSuggestionRectWithArrow(
        x: Float,
        y: Float,
        directionDown: Boolean,
        showTriangle: Boolean,
        suggestionRect: RectF,
        canvas: Canvas
    ) {
        if (directionDown) {
            drawSuggestionRect(suggestionRect, canvas)
            if (showTriangle) {
                drawSuggestionTriangle(x, y, canvas, directionDown)
            }
        } else {
            if (showTriangle) {
                drawSuggestionTriangle(x, y, canvas, directionDown)
            }
            drawSuggestionRect(suggestionRect, canvas)
        }
    }

    private fun drawSuggestionRect(suggestionRect: RectF, canvas: Canvas) {
        canvas.drawRoundRect(
            suggestionRect,
            suggestionBorderRadius,
            suggestionBorderRadius,
            suggestionPaint
        )
    }

    private fun drawPrice(
        x: Float,
        y: Float,
        price: String,
        priceHeight: Int,
        dateTimeHeight: Int,
        canvas: Canvas,
        directionDown: Boolean
    ) {
        val yText = if (directionDown) {
            y - suggestionToPointDistance - suggestionTopBottomPadding - suggestionPriceToDateTimeDistance - dateTimeHeight - priceHeight * 0.5f - (suggestionPricePaint.descent() + suggestionPricePaint.ascent()) * 0.5f
        } else {
            y + suggestionToPointDistance + suggestionTopBottomPadding + priceHeight * 0.5f - (suggestionPricePaint.descent() + suggestionPricePaint.ascent()) * 0.5f
        }
        canvas.drawText(price, x, yText, suggestionPricePaint)
    }

    private fun drawDateTime(
        x: Float,
        y: Float,
        dateTime: String,
        priceHeight: Int,
        dateTimeHeight: Int,
        canvas: Canvas,
        directionDown: Boolean
    ) {
        val yText = if (directionDown) {
            y - suggestionToPointDistance - suggestionTopBottomPadding - dateTimeHeight * 0.5f - (suggestionDateTimePaint.descent() + suggestionDateTimePaint.ascent()) * 0.5f
        } else {
            y + suggestionToPointDistance + suggestionTopBottomPadding + suggestionPriceToDateTimeDistance + priceHeight + dateTimeHeight * 0.5f - (suggestionDateTimePaint.descent() + suggestionDateTimePaint.ascent()) * 0.5f
        }
        canvas.drawText(dateTime, x, yText, suggestionDateTimePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            if (length > 0 && (lengthAnimator == null || lengthAnimator?.isRunning != true)) {
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


    private fun Int.toRealX(): Float {
        return (width * this / (length - 1)).toFloat()
    }

    private fun Float.fromRealX(): Int {
        return (this * (length - 1) / width).roundToInt()
    }

    private fun Double.toRealY(): Float {
        return height - ((height - chartPaddingSize * 2) * (this - min) / (max - min)).toFloat() - chartPaddingSize
    }

    private fun Paint.getTextWidthAndHeight(text: String): Pair<Int, Int> {
        getTextBounds(text, 0, text.length, textBoundRect)
        return Pair(textBoundRect.width(), textBoundRect.height())
    }
}