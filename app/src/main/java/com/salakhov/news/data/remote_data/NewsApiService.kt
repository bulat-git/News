package com.salakhov.news.data.remote_data


import com.salakhov.news.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/everything?apiKey=${BuildConfig.NEWS_API_KEY}")
    suspend fun loadArticlesResponse(
        @Query(value = "q") topic: String,
        @Query(value = "language") language: String
    ): NewsApiResponseDto
}