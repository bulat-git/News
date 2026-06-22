package com.salakhov.news.presentation.startup

import androidx.annotation.Size
import com.salakhov.news.domain.usecases.StartPeriodicRefreshDataUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class StartAppManager @Inject constructor(
    private val startPeriodicRefreshDataUseCase: StartPeriodicRefreshDataUseCase
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun startRefreshData() {
        scope.launch { startPeriodicRefreshDataUseCase() }
    }
}