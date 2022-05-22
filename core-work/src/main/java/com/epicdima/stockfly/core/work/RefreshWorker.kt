package com.epicdima.stockfly.core.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.epicdima.stockfly.core.data.Repository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class RefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(context, workerParameters) {

    companion object {

        private const val WORK_REQUEST_TAG = "refresh_work"
        private const val DELAY_INITIAL_MINUTES = 20L
        private const val DELAY_REFRESH_HOURS = 4L
        private const val DELAY_RETRY_MINUTES = 10L

        fun startRefreshWork(context: Context) {
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_REQUEST_TAG,
                    ExistingPeriodicWorkPolicy.KEEP,
                    createRefreshWorkRequest()
                )
        }

        private fun createRefreshWorkRequest(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<RefreshWorker>(DELAY_REFRESH_HOURS, TimeUnit.HOURS)
                .setInitialDelay(DELAY_INITIAL_MINUTES, TimeUnit.MINUTES)
                .setBackoffCriteria(BackoffPolicy.LINEAR, DELAY_RETRY_MINUTES, TimeUnit.MINUTES)
                .addTag(WORK_REQUEST_TAG)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .setRequiresStorageNotLow(true)
                        .build()
                )
                .build()
        }
    }

    override suspend fun doWork(): Result {
        Timber.i("refresh")
        return try {
            repository.refreshCompanies()
            Timber.i("success")
            Result.success()
        } catch (e: Exception) {
            Timber.w(e, "retry")
            Result.retry()
        }
    }
}