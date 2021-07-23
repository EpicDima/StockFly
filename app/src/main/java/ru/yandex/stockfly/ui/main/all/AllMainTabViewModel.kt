package ru.yandex.stockfly.ui.main.all

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.yandex.stockfly.model.Company
import ru.yandex.stockfly.repository.Repository
import javax.inject.Inject

@HiltViewModel
class AllMainTabViewModel @Inject constructor(
    repository: Repository
) : ViewModel() {

    val companies: LiveData<List<Company>> = repository.companies
}