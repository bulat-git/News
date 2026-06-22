package com.salakhov.news.domain.usecases

import com.salakhov.news.domain.repository.NewsRepository
import com.salakhov.news.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateSubscripForAllTopicsUseCase @Inject constructor(
    val newsRepository: NewsRepository,
    val settingsRepository: SettingsRepository
) {


    suspend operator fun invoke(): List<String> {
        val settings = settingsRepository.getSettings().first()
        return newsRepository.updateArticlesForAllTopics(settings.language)
    }
}