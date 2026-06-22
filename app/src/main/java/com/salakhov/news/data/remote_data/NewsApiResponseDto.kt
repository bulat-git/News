package com.salakhov.news.data.remote_data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsApiResponseDto(
    @SerialName("articles")
    val articles: List<ArticleDto> = listOf(),
)