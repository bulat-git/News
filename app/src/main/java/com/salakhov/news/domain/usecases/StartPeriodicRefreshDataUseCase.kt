package com.salakhov.news.domain.usecases

import com.salakhov.news.data.mapper.toRefreshConfig
import com.salakhov.news.domain.repository.NewsRepository
import com.salakhov.news.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class StartPeriodicRefreshDataUseCase @Inject constructor(
    val newsRepository: NewsRepository,
    val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke() {
        settingsRepository.getSettings()
            .map { it.toRefreshConfig() }
            .distinctUntilChanged()
            .onEach { newsRepository.startRefreshData(it) }
            .collect()
    }
}
