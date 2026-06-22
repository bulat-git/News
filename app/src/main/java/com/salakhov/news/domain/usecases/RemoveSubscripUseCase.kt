package com.salakhov.news.domain.usecases

import com.salakhov.news.domain.repository.NewsRepository
import javax.inject.Inject

class RemoveSubscripUseCase @Inject constructor(
    val newsRepository: NewsRepository
) {

    suspend operator fun invoke(topic: String) = newsRepository.removeSubscription(topic)
}