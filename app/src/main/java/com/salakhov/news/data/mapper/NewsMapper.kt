package com.salakhov.news.data.mapper

import com.salakhov.news.data.local_data.ArticleDbModel
import com.salakhov.news.data.remote_data.NewsApiResponseDto
import com.salakhov.news.domain.entities.Article
import com.salakhov.news.domain.entities.Language
import java.text.SimpleDateFormat
import java.util.Locale

fun NewsApiResponseDto.toDbModels(topic: String): List<ArticleDbModel>{
    return articles.map {
        ArticleDbModel(
            title = it.title,
            description = it.description,
            url = it.url,
            imageUrl = it.urlToImage,
            sourceName = it.source.name,
            topic = topic,
            publishedAt = it.publishedAt.toTimeStamp()
        )
    }
}

fun List<ArticleDbModel>.toEntities(): List<Article> {
    return map {
        Article(
            title = it.title,
            description = it.description,
            url = it.url,
            imageUrl = it.imageUrl,
            sourceName = it.sourceName,
            publishedAt = it.publishedAt
        )
    }.distinct()
}


fun Language.toQueryParam(): String {
    return when (this) {
        Language.RUSSIAN -> "ru"
        Language.ENGLISH -> "en"
        Language.GERMAN -> "de"
        Language.FRENCH -> "fr"
        Language.JAPANESE -> "jp"
    }
}

private fun String.toTimeStamp(): Long {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    return dateFormatter.parse(this)?.time ?: System.currentTimeMillis()
}