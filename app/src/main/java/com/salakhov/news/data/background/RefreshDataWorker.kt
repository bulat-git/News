package com.salakhov.news.data.background

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.salakhov.news.domain.usecases.GetSettingsUseCase
import com.salakhov.news.domain.usecases.UpdateSubscripForAllTopicsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class RefreshDataWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSubscripForAllTopicsUseCase: UpdateSubscripForAllTopicsUseCase,
    private val notificationsHelper: NotificationsHelper
): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        Log.d("RefreshDataWorker", "Start")
        val settings = getSettingsUseCase().first()
        val updatedTopics = updateSubscripForAllTopicsUseCase()
        if (updatedTopics.isNotEmpty() && settings.notificationsEnabled) {
            notificationsHelper.showNewArticlesNotification(updatedTopics)
        }
        Log.d("RefreshDataWorker", "Finish")
        return Result.success()
    }
}