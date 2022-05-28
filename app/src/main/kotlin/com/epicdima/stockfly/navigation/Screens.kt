package com.epicdima.stockfly.navigation

import android.content.Intent
import android.net.Uri
import com.epicdima.stockfly.feature.details.DetailsFragment
import com.epicdima.stockfly.feature.list.ListFragment
import com.epicdima.stockfly.feature.list.search.SearchFragment
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    fun list() = FragmentScreen { ListFragment.newInstance() }

    fun search() = FragmentScreen { SearchFragment.newInstance() }

    fun details(ticker: String) = FragmentScreen("Details_$ticker") {
        DetailsFragment.newInstance(ticker)
    }

    fun browser(url: String) = ActivityScreen {
        Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }

    fun dialer(phoneNumber: String) = ActivityScreen {
        Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phoneNumber}"))
    }
}