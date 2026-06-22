package com.salakhov.news.di

import android.content.Context
import androidx.work.WorkManager
import com.salakhov.news.data.local_data.NewsDao
import com.salakhov.news.data.local_data.NewsDatabase
import com.salakhov.news.data.remote_data.NewsApiService
import com.salakhov.news.data.repository.NewsRepositoryImpl
import com.salakhov.news.data.repository.SettingsRepositoryImpl
import com.salakhov.news.domain.repository.NewsRepository
import com.salakhov.news.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository


    @Binds
    @Singleton
    fun bindNewsRepository(
        impl: NewsRepositoryImpl
    ): NewsRepository
    companion object {

        @Provides
        @Singleton
        fun provideWorkManager(
            @ApplicationContext context: Context
        ): WorkManager {
            return WorkManager.getInstance(context)
        }

        @Provides
        @Singleton
        fun providingJson(): Json {
            return Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        @Provides
        @Singleton
        fun providingConverterFactory(json: Json): Converter.Factory {
            return json.asConverterFactory("application/json".toMediaType())
        }

        @Provides
        @Singleton
        fun providingRetrofit(converterFactory: Converter.Factory): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://newsapi.org/")
                .addConverterFactory(converterFactory)
                .build()
        }

        @Provides
        @Singleton
        fun providingNewsApiService(retrofit: Retrofit): NewsApiService {
            return retrofit.create()
        }


        @Provides
        @Singleton
        fun providingNewsDatabase(
            @ApplicationContext context: Context
        ): NewsDatabase {
            return NewsDatabase.getInstance(context)
        }

        @Provides
        @Singleton
        fun providingNewsDao(
            database: NewsDatabase
        ): NewsDao = database.newsDao()
    }
}