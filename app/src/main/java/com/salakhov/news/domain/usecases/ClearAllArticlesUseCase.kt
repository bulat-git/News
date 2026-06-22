package com.salakhov.news.domain.usecases

import com.salakhov.news.domain.repository.NewsRepository
import javax.inject.Inject

class ClearAllArticlesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(topics: List<String>) = newsRepository.clearAllArticles(topics)
}