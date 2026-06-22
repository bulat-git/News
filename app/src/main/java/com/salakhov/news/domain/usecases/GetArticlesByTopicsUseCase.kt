package com.salakhov.news.domain.usecases

import com.salakhov.news.domain.repository.NewsRepository
import javax.inject.Inject

class GetArticlesByTopicsUseCase @Inject constructor(
    val newsRepository: NewsRepository
) {
    operator fun invoke(topics: List<String>) = newsRepository.getArticlesByTopics(topics)
}