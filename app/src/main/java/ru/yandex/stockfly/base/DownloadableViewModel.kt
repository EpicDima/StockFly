package ru.yandex.stockfly.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import ru.yandex.stockfly.other.timeout
import java.util.concurrent.atomic.AtomicBoolean

abstract class DownloadableViewModel : ViewModel() {

    private val errorOccurred = AtomicBoolean(false)
    private val timeoutOccurred = AtomicBoolean(false)
    private val jobIsDone = AtomicBoolean(false)

    private val _error = MutableLiveData(false)
    val error: LiveData<Boolean> = _error

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private var job: Job? = null
    private var timeoutJob: Job? = null

    protected abstract fun onTimeout()
    protected abstract fun onError(e: Throwable)

    protected open fun setError() {
        _error.postValue(true)
    }

    protected open fun resetError() {
        _error.postValue(false)
    }

    protected open fun startLoading() {
        _loading.postValue(true)
    }

    protected open fun stopLoading() {
        _loading.postValue(false)
    }

    protected open fun jobDone() {
        jobIsDone.set(true)
    }

    protected fun startJob(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        stopImmediately: Boolean = false,
        function: suspend () -> Unit
    ) {
        errorOccurred.set(false)
        jobIsDone.set(false)
        job = viewModelScope.launch(dispatcher) {
            try {
                function()
                if (stopImmediately) {
                    jobIsDone.set(true)
                }
            } catch (ignored: CancellationException) {
                jobIsDone.set(true)
            } catch (e: Exception) {
                errorOccurred.set(true)
                onError(e)
            }
        }
    }

    protected fun stopJob(): Boolean {
        job?.cancel()
        return !jobIsDone.get()
    }

    protected fun startTimeoutJob() {
        timeoutOccurred.set(false)
        timeoutJob = viewModelScope.timeout {
            if (stopJob()) {
                timeoutOccurred.set(true)
                onTimeout()
            }
        }
    }

    protected fun stopTimeoutJob(): Boolean {
        timeoutJob?.cancel()
        return !timeoutOccurred.get()
    }
}