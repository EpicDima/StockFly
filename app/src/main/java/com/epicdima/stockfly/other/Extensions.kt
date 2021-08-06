package com.epicdima.stockfly.other

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.epicdima.stockfly.model.Company
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.util.*

fun Float.dpToPx(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    )
}

fun Float.spToPx(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        context.resources.displayMetrics
    )
}

fun Resources.getDimensionInSp(@DimenRes id: Int): Float {
    return getDimension(id) / displayMetrics.scaledDensity
}


private const val TIMEOUT_MILLIS = 10_000L

fun CoroutineScope.timeout(onTimeout: () -> Unit): Job {
    return launch {
        try {
            withTimeout(TIMEOUT_MILLIS) {
                awaitCancellation()
            }
        } catch (e: TimeoutCancellationException) {
            Timber.w(e)
            onTimeout()
        }
    }
}

fun Context.color(@ColorRes id: Int): Int {
    return ResourcesCompat.getColor(resources, id, this.theme)
}

fun @receiver:ColorInt Int.darken(ratio: Float = 0.4f): Int {
    return ColorUtils.blendARGB(this, Color.BLACK, ratio)
}

fun Context.drawable(@DrawableRes id: Int): Drawable {
    return ResourcesCompat.getDrawable(resources, id, this.theme)!!
}

fun TabLayout.Tab.set(textSize: Float, @ColorRes id: Int) {
    view.findViewById<TextView>(android.R.id.text1).apply {
        this.textSize = textSize
        setTextColor(context.color(id))
    }
}

@Suppress("DEPRECATION")
fun Configuration.locale(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        locales.get(0)
    } else {
        locale
    }
}

fun Company.ipoLocalDateString(): String {
    return try {
        val ipoDate = FORMAT_DATE_API.parse(ipo)!!
        DateFormat.getDateInstance(DateFormat.MEDIUM, Formatter.getCurrentLocale()).format(ipoDate)
    } catch (e: ParseException) {
        ""
    }
}

fun Double.toLocalString(): String {
    return Formatter.getNumberFormat().format(this)
}

fun Int.toLocalString(): String {
    return Formatter.getNumberFormat().format(this)
}

fun <T : Fragment> T.setArgument(key: String, value: String): T {
    arguments = Bundle().apply {
        putString(key, value)
    }
    return this
}

fun <T : Fragment> T.setArgument(key: String, value: Int): T {
    arguments = Bundle().apply {
        putInt(key, value)
    }
    return this
}


fun TabLayout.customize(
    viewPager: ViewPager2,
    @LayoutRes customLayout: Int,
    titles: Array<String>,
    onSelect: (TabLayout.Tab) -> Unit,
    onUnselect: (TabLayout.Tab) -> Unit
): TabLayoutMediator {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            onSelect(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            onUnselect(tab)
        }

        override fun onTabReselected(tab: TabLayout.Tab) {}
    })
    return TabLayoutMediator(this, viewPager) { tab, position ->
        tab.customView = View.inflate(context, customLayout, null)
        tab.text = titles[position]
        viewPager.currentItem = tab.position
    }.apply {
        attach()
    }
}