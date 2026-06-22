package com.salakhov.news.data.repository

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.salakhov.news.data.background.RefreshDataWorker
import com.salakhov.news.data.local_data.ArticleDbModel
import com.salakhov.news.data.local_data.NewsDao
import com.salakhov.news.data.local_data.SubscriptionDbModel
import com.salakhov.news.data.mapper.toDbModels
import com.salakhov.news.data.mapper.toEntities
import com.salakhov.news.data.mapper.toQueryParam
import com.salakhov.news.data.remote_data.NewsApiService
import com.salakhov.news.domain.entities.Article
import com.salakhov.news.domain.entities.Language
import com.salakhov.news.domain.entities.RefreshConfig
import com.salakhov.news.domain.repository.NewsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val newsApiService: NewsApiService,
    private val workManager: WorkManager
) : NewsRepository {

    override fun getAllSubscriptions(): Flow<List<String>> {
        return newsDao.getAllSubscriptions().map { subscriptions ->
            subscriptions.map { it.topic }
        }
    }

    override suspend fun addSubscriptions(topic: String) {
        newsDao.addSubscription(SubscriptionDbModel(topic))
    }

    override suspend fun updateArticlesForTopic(topic: String, language: Language): Boolean {
        val articles = loadArticles(topic, language)
        val ids = newsDao.addArticles(articles)
        return ids.any { it != 1L }
    }

    suspend fun loadArticles(topic: String, language: Language): List<ArticleDbModel> {
        return try {
            newsApiService.loadArticlesResponse(topic, language.toQueryParam()).toDbModels(topic)
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            }
            Log.e("NewsRepository", e.stackTraceToString())
            listOf()
        }
    }

    override suspend fun removeSubscription(topic: String) {
        newsDao.deleteSubscription(SubscriptionDbModel(topic))
    }

    override suspend fun updateArticlesForAllTopics(language: Language): List<String> {
        val updatedTopics = mutableListOf<String>()
        val topics = newsDao.getAllSubscriptions().first()
        coroutineScope {
            topics.forEach {
                launch {
                    val updated = updateArticlesForTopic(it.topic, language)
                    if (updated) {
                        updatedTopics.add(it.topic)
                    }
                }
            }
        }
        return updatedTopics
    }

    override fun getArticlesByTopics(topics: List<String>): Flow<List<Article>> {
        return newsDao.getAllArticlesByTopics(topics).map {
            it.toEntities()
        }
    }

    override suspend fun clearAllArticles(topics: List<String>) {
        newsDao.deleteArticlesByTopic(topics)
    }

    override fun startRefreshData(refreshConfig: RefreshConfig) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                if (refreshConfig.wifiOnly) {
                    NetworkType.UNMETERED
                } else {
                    NetworkType.CONNECTED
                }
            )
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest =
            PeriodicWorkRequestBuilder<RefreshDataWorker>(
                refreshConfig.interval.minutes.toLong(),
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()
        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = "RefreshDataWorker",
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request = workRequest
        )
    }
}