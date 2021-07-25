package com.epicdima.stockfly.ui.main.all

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.epicdima.stockfly.model.Company
import com.epicdima.stockfly.repository.Repository
import javax.inject.Inject

@HiltViewModel
class AllMainTabViewModel @Inject constructor(
    repository: Repository
) : ViewModel() {

    val companies: LiveData<List<Company>> = repository.companies
}