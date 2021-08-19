package com.epicdima.stockfly.ui.main.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllMainTabViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val companies: StateFlow<List<Company>> = repository.companies
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun changeFavourite(company: Company) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.changeFavourite(company)
        }
    }

    fun deleteCompany(company: Company) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCompany(company)
        }
    }
}