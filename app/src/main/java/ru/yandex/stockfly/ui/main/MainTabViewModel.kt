package ru.yandex.stockfly.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.yandex.stockfly.model.Company
import ru.yandex.stockfly.repository.Repository
import ru.yandex.stockfly.ui.main.MainTabFragment.Companion.FAVOURITE_TAB_NUMBER
import ru.yandex.stockfly.ui.main.MainTabFragment.Companion.TAB_NUMBER_KEY
import javax.inject.Inject

@HiltViewModel
class MainTabViewModel @Inject constructor(
    private val repository: Repository,
    state: SavedStateHandle
) : ViewModel() {

    private val tabNumber = state.get<Int>(TAB_NUMBER_KEY)!!

    val companies: LiveData<List<Company>> = if (tabNumber == FAVOURITE_TAB_NUMBER) {
        repository.favourites
    } else {
        repository.companies
    }

    fun changeFavouriteNumber(from: Int, to: Int) {
        if (tabNumber == FAVOURITE_TAB_NUMBER) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.changeFavouriteNumber(from, to)
            }
        }
    }
}