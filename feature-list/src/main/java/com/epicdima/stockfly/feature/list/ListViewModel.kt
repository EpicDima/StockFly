package com.epicdima.stockfly.feature.list

import androidx.lifecycle.ViewModel
import com.epicdima.stockfly.core.navigation.OpenSearchProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val openSearchProvider: OpenSearchProvider,
) : ViewModel() {

    fun openSearch() {
        openSearchProvider.openSearch()
    }
}