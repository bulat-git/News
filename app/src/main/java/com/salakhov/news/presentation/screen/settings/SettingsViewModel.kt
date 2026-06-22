package com.salakhov.news.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salakhov.news.domain.entities.Interval
import com.salakhov.news.domain.entities.Language
import com.salakhov.news.domain.entities.Settings
import com.salakhov.news.domain.usecases.ChangeLanguageUseCase
import com.salakhov.news.domain.usecases.ChangeNotificationsEnableUseCase
import com.salakhov.news.domain.usecases.ChangeUpdateIntervalUseCase
import com.salakhov.news.domain.usecases.ChangeWifiOnlyUseCase
import com.salakhov.news.domain.usecases.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val changeNotificationsEnableUseCase: ChangeNotificationsEnableUseCase,
    private val changeUpdateIntervalUseCase: ChangeUpdateIntervalUseCase,
    private val changeWifiOnlyUseCase: ChangeWifiOnlyUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val changeLanguageUseCase: ChangeLanguageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()


    init {
        getActualSettings()
    }

    fun processCommand(command: SettingsCommand) {
        when (command) {
            is SettingsCommand.ChangeLanguage -> {
                viewModelScope.launch {
                    changeLanguageUseCase(command.language)
                }
            }

            is SettingsCommand.ChangeNotificationsEnable -> {
                viewModelScope.launch {
                    changeNotificationsEnableUseCase(command.notificationsEnabled)
                }
            }

            is SettingsCommand.ChangeUpdateInterval -> {
                viewModelScope.launch {
                    changeUpdateIntervalUseCase(command.interval)
                }
            }

            is SettingsCommand.ChangeWifiOnly -> {
                viewModelScope.launch {
                    changeWifiOnlyUseCase(command.wifiOnly)
                }
            }
        }
    }

    private fun getActualSettings() {
        getSettingsUseCase()
            .distinctUntilChanged()
            .onEach { settings ->
                _state.update {
                    it.copy(
                        language = settings.language,
                        interval = settings.interval,
                        notificationsEnabled = settings.notificationsEnabled,
                        wifiOnly = settings.wifiOnly
                    )
                }
            }.launchIn(viewModelScope)
    }
}

sealed interface SettingsCommand {

    data class ChangeLanguage(val language: Language) : SettingsCommand

    data class ChangeUpdateInterval(val interval: Interval) : SettingsCommand

    data class ChangeNotificationsEnable(val notificationsEnabled: Boolean) : SettingsCommand

    data class ChangeWifiOnly(val wifiOnly: Boolean) : SettingsCommand
}

data class SettingsState(
    val language: Language = Settings.DEFAULT_LANGUAGE,
    val interval: Interval = Settings.DEFAULT_INTERVAL,
    val notificationsEnabled: Boolean = Settings.DEFAULT_NOTIFICATIONS,
    val wifiOnly: Boolean = Settings.DEFAULT_WIFI,
    val proposedLanguages: List<Language> = Language.entries,
    val proposedIntervals: List<Interval> = Interval.entries,
)