package com.salakhov.news.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.salakhov.news.domain.entities.Interval
import com.salakhov.news.domain.entities.Language
import com.salakhov.news.domain.entities.Settings
import com.salakhov.news.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SettingsRepository {

    private val languageKey = stringPreferencesKey("language")
    private val intervalKey = intPreferencesKey("interval")
    private val notificationsEnabledKey = booleanPreferencesKey("notifications_enabled")
    private val wifiOnlyKey = booleanPreferencesKey("wifi_only")

    override fun getSettings(): Flow<Settings> = context.dataStore.data.map { preferences ->
        val langString = preferences[languageKey] ?: Settings.DEFAULT_LANGUAGE.name
        val language = Language.valueOf(langString)
        val intervalInt = preferences[intervalKey]
        val interval = Interval.entries.firstOrNull { it.minutes == intervalInt } ?: Settings.DEFAULT_INTERVAL
        Settings(
            language = language,
            interval = interval,
            notificationsEnabled = preferences[notificationsEnabledKey] ?: Settings.DEFAULT_NOTIFICATIONS,
            wifiOnly = preferences[wifiOnlyKey] ?: Settings.DEFAULT_WIFI
        )
    }

    override suspend fun updateLanguage(language: Language) {
        context.dataStore.edit { preferences ->
            preferences[languageKey] = language.name
        }
    }

    override suspend fun updateInterval(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[intervalKey] = minutes
        }
    }

    override suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[notificationsEnabledKey] = enabled
        }
    }

    override suspend fun updateWifiOnly(wifiOnly: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[wifiOnlyKey] = wifiOnly
        }
    }
}