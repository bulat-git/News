package com.salakhov.news.domain.repository

import com.salakhov.news.domain.entities.Article
import com.salakhov.news.domain.entities.Language
import com.salakhov.news.domain.entities.RefreshConfig
import kotlinx.coroutines.flow.Flow

interface NewsRepository {

    fun getAllSubscriptions(): Flow<List<String>>

    suspend fun addSubscriptions(topic: String)

    suspend fun updateArticlesForTopic(topic: String, language: Language) : Boolean

    suspend fun removeSubscription(topic: String)

    suspend fun updateArticlesForAllTopics(language: Language): List<String>

    fun getArticlesByTopics(topics: List<String>): Flow<List<Article>>

    suspend fun clearAllArticles(topics: List<String>)
    fun startRefreshData(refreshConfig: RefreshConfig)
}