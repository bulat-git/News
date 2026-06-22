@file:OptIn(ExperimentalCoroutinesApi::class)

package com.salakhov.news.presentation.screen.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salakhov.news.domain.entities.Article
import com.salakhov.news.domain.usecases.AddSubscripUseCase
import com.salakhov.news.domain.usecases.ClearAllArticlesUseCase
import com.salakhov.news.domain.usecases.GetAllSubscripUseCase
import com.salakhov.news.domain.usecases.GetArticlesByTopicsUseCase
import com.salakhov.news.domain.usecases.RemoveSubscripUseCase
import com.salakhov.news.domain.usecases.UpdateSubscripForAllTopicsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val getArticlesByTopicsUseCase: GetArticlesByTopicsUseCase,
    private val addSubscriptionsUseCase: AddSubscripUseCase,
    private val removeSubscriptionUseCase: RemoveSubscripUseCase,
    private val updateArticlesForAllTopicsUseCase: UpdateSubscripForAllTopicsUseCase,
    private val clearAllArticlesUseCase: ClearAllArticlesUseCase,
    private val getAllSubscriptionsUseCase: GetAllSubscripUseCase
): ViewModel() {

    private val _state = MutableStateFlow(SubscriptionsState())
    val state = _state.asStateFlow()

    init {
        observeSubscriptions()
        observeSelectedTopics()
    }

    // в этой функции мы описываем как реагировать на команды, вызываем usecases и меняем стейт экрана
    fun processCommand(command: SubscriptionsCommand) {
        when (command) {
            SubscriptionsCommand.ClearArticles -> {
                viewModelScope.launch { val topics = state.value.selectedTopics
                    clearAllArticlesUseCase(topics)
                }
            }
            SubscriptionsCommand.ClickSubscribe -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        val topic = state.value.query.trim()
                        addSubscriptionsUseCase(topic)
                        previousState.copy(query = "")
                    }
                }
            }
            is SubscriptionsCommand.InputTopic -> {
                _state.update { previousState ->
                    previousState.copy(query = command.query)
                }
            }
            SubscriptionsCommand.RefreshData -> {
                viewModelScope.launch {
                    updateArticlesForAllTopicsUseCase()
                }
            }
            is SubscriptionsCommand.RemoveSubscription -> {
                viewModelScope.launch {
                    removeSubscriptionUseCase(command.topic)
                }
            }
            is SubscriptionsCommand.ToggleTopicSelection -> {
                _state.update {previousState ->
                    val subscriptions = previousState.subscriptions.toMutableMap()
                    val isSelected = subscriptions[command.topic] ?: false
                    subscriptions[command.topic] = !isSelected
                    previousState.copy(subscriptions = subscriptions)
                }
            }
        }
    }

    private fun observeSelectedTopics() {
        state.map { it.selectedTopics }
            .distinctUntilChanged()
            .flatMapLatest {
                getArticlesByTopicsUseCase(it)
            }
            .onEach { articles ->
                _state.update { previousState ->
                    previousState.copy(articles = articles)
                }
            }.launchIn(viewModelScope)
    }

    private fun observeSubscriptions() {
        getAllSubscriptionsUseCase()
            .onEach { subscriptions ->
                _state.update { previousState ->
                    val updatedTopics = subscriptions.associateWith { topic ->
                        previousState.subscriptions[topic] ?: true
                    }
                    previousState.copy(subscriptions = updatedTopics)
                }
            }.launchIn(viewModelScope)
    }
}

// Определяем набор действий который выполняет вью модель
sealed interface SubscriptionsCommand {

    data class InputTopic(val query: String): SubscriptionsCommand

    data object ClickSubscribe: SubscriptionsCommand

    data object RefreshData: SubscriptionsCommand

    data class ToggleTopicSelection(val topic: String): SubscriptionsCommand

    data object ClearArticles: SubscriptionsCommand

    data class RemoveSubscription(val topic: String): SubscriptionsCommand
}

// Состояние экрана(state), запрашивает данные которые будут передаваться в отрисованный экран
data class SubscriptionsState(
    val query: String = "",
    val subscriptions: Map<String, Boolean> = mapOf(),
    val articles: List<Article> = listOf()
) {

    val subscribeButtonEnabled: Boolean
        get() = query.isNotBlank()

    val selectedTopics: List<String>
        get() = subscriptions.filter { it.value }.map { it.key }
}