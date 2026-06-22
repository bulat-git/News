package com.salakhov.news.domain.usecases

import com.salakhov.news.domain.repository.NewsRepository
import javax.inject.Inject

class GetAllSubscripUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    operator fun invoke() = newsRepository.getAllSubscriptions()
}