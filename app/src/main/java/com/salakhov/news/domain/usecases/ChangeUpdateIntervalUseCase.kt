package com.salakhov.news.domain.usecases

import com.salakhov.news.domain.entities.Interval
import com.salakhov.news.domain.repository.SettingsRepository
import javax.inject.Inject

class ChangeUpdateIntervalUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(interval: Interval) = settingsRepository.updateInterval(interval.minutes)
}
