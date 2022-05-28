package com.epicdima.stockfly.navigation

import com.epicdima.stockfly.core.navigation.*
import com.github.terrakok.cicerone.Router

class StockFlyRouter : Router(), OpenSearchProvider, OpenDetailsProvider, OpenBrowserProvider,
    OpenDialerProvider, OpenListProvider {

    override fun openList() {
        executeCommands(ReplaceAllIfNotExist(Screens.list()))
    }

    override fun openSearch(query: String) {
        executeCommands(ForwardWithoutReplaceSameTop(Screens.search(query)))
    }

    override fun openDetails(ticker: String) {
        executeCommands(ForwardWithoutReplaceSameTop(Screens.details(ticker)))
    }

    override fun openBrowser(url: String) {
        navigateTo(Screens.browser(url))
    }

    override fun openDialer(phoneNumber: String) {
        navigateTo(Screens.dialer(phoneNumber))
    }
}