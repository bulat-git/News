package com.salakhov.news.domain.usecases

import com.salakhov.news.domain.entities.Language
import com.salakhov.news.domain.repository.SettingsRepository
import javax.inject.Inject

class ChangeWifiOnlyUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(wifiOnly: Boolean) = settingsRepository.updateWifiOnly(wifiOnly)
}
