package ru.yandex.stockfly.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import ru.yandex.stockfly.other.timeout

abstract class DownloadableViewModel : ViewModel() {

    private val _error = MutableLiveData(false)
    val error: LiveData<Boolean> = _error

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private var job: Job? = null
    private var timeoutJob: Job? = null

    protected abstract fun onTimeout()
    protected abstract fun onError(e: Throwable)

    protected fun setError() {
        _error.postValue(true)
    }

    protected fun resetError() {
        _error.postValue(false)
    }

    protected fun startLoading() {
        _loading.postValue(true)
    }

    protected fun stopLoading() {
        _loading.postValue(false)
    }

    protected fun startJob(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        function: suspend () -> Unit
    ) {
        job = viewModelScope.launch(dispatcher) {
            try {
                function()
            } catch (ignored: CancellationException) {
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    protected fun stopJob(): Boolean {
        val active = (job?.isActive == true)
        job?.cancel()
        return active
    }

    protected fun startTimeoutJob() {
        timeoutJob = viewModelScope.timeout {
            if (stopJob()) {
                onTimeout()
            }
        }
    }

    protected fun stopTimeoutJob(): Boolean {
        val active = (timeoutJob?.isActive == true)
        timeoutJob?.cancel()
        return active
    }
}