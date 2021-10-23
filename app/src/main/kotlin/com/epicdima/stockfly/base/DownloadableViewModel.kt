package com.epicdima.stockfly.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epicdima.stockfly.other.timeout
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

abstract class DownloadableViewModel : ViewModel() {

    private val errorOccurred = AtomicBoolean(false)
    private val timeoutOccurred = AtomicBoolean(false)
    private val jobIsDone = AtomicBoolean(false)

    private val _error = MutableStateFlow(false)
    val error: StateFlow<Boolean> = _error.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private var job: Job? = null
    private var timeoutJob: Job? = null

    protected abstract fun onTimeout()
    protected abstract fun onError(e: Throwable)

    protected open fun setError() {
        _error.value = true
    }

    protected open fun resetError() {
        _error.value = false
    }

    protected open fun startLoading() {
        _loading.value = true
    }

    protected open fun stopLoading() {
        _loading.value = false
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
            } catch (e: CancellationException) {
                Timber.w(e)
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