package com.epicdima.stockfly.core.ui

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.graphics.ColorUtils
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

fun @receiver:ColorInt Int.darken(ratio: Float = 0.4f): Int {
    return ColorUtils.blendARGB(this, Color.BLACK, ratio)
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


fun TabLayout.Tab.set(textSize: Float, @ColorRes id: Int) {
    view.findViewById<TextView>(android.R.id.text1).apply {
        this.textSize = textSize
        setTextColor(context.color(id))
    }
}