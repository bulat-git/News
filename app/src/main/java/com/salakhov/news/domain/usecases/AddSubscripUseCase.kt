package com.salakhov.news.domain.usecases

import com.salakhov.news.domain.repository.NewsRepository
import com.salakhov.news.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class AddSubscripUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(topic: String){
        newsRepository.addSubscriptions(topic)
        CoroutineScope(currentCoroutineContext()).launch {
            val settings = settingsRepository.getSettings().first()
            newsRepository.updateArticlesForTopic(topic, settings.language)
        }

    }
} 