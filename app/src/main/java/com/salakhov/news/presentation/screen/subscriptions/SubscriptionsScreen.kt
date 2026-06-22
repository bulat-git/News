package com.salakhov.news.presentation.screen.subscriptions

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.salakhov.news.R
import com.salakhov.news.domain.entities.Article
import com.salakhov.news.presentation.utils.toDateFormat

@Composable
fun SubscriptionsScreen(
    modifier: Modifier = Modifier,
    onSettingNavigate: () -> Unit,
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopScreen(
                onDeleteClick = {
                    viewModel.processCommand(SubscriptionsCommand.ClearArticles)
                },
                onRefreshClick = {
                    viewModel.processCommand(SubscriptionsCommand.RefreshData)
                },
                onSettingsClick = {
                    onSettingNavigate()
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.primary
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Subscriptions(
                    subscriptions = state.subscriptions,
                    query = state.query,
                    isEnable = state.subscribeButtonEnabled,
                    onQueryChange = {
                        viewModel.processCommand(SubscriptionsCommand.InputTopic(it))
                    },
                    onDeleteClick = {
                        viewModel.processCommand(SubscriptionsCommand.RemoveSubscription(it))
                    },
                    onTopicClick = {
                        viewModel.processCommand(SubscriptionsCommand.ToggleTopicSelection(it))
                    },
                    onSubscribeClick = {
                        viewModel.processCommand(SubscriptionsCommand.ClickSubscribe)
                    }
                )
            }
            if (state.articles.isNotEmpty()) {
                item {
                    HorizontalDivider()
                }
                item {
                    Text(stringResource(R.string.articles_counter, state.articles.size))
                }
                item {
                    HorizontalDivider()
                }
                items(
                    items = state.articles,
                    key = { it.url }
                ) {
                    ArticleCard(
                        modifier = Modifier,
                        article = it
                    )
                }
            } else if (state.subscriptions.isNotEmpty()) {
                item {
                    HorizontalDivider()
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.empty_articles_message)
                    )
                }

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopScreen(
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(text = stringResource(R.string.subscriptions_title))
        },
        actions = {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(8.dp)
                    .clickable { onDeleteClick() },
                imageVector = Icons.Default.Delete,
                contentDescription = null,
            )
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(8.dp)
                    .clickable { onRefreshClick() },
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
            )
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(8.dp)
                    .clickable { onSettingsClick() },
                imageVector = Icons.Default.Settings,
                contentDescription = null,
            )
        }
    )
}

@Composable
private fun SubscriptionChip(
    modifier: Modifier = Modifier,
    topic: String,
    isSelected: Boolean,
    onChipClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    FilterChip(
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.Gray.copy(alpha = 0.2f),
            disabledLabelColor = Color.Gray.copy(alpha = 0.2f),
            selectedContainerColor = MaterialTheme.colorScheme.tertiary,

            ),
        modifier = modifier,
        selected = isSelected,
        onClick = {
            onChipClick(topic)
        },
        label = {
            Text(text = topic)
        },
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .clickable {
                        onDeleteClick(topic)
                    },
                imageVector = Icons.Default.Clear,
                contentDescription = null
            )
        }
    )
}

@Composable
fun Subscriptions(
    modifier: Modifier = Modifier,
    subscriptions: Map<String, Boolean>,
    query: String,
    isEnable: Boolean,
    onQueryChange: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onSubscribeClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(stringResource(R.string.search_bar_label))
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                unfocusedBorderColor = MaterialTheme.colorScheme.tertiary
            ),

            )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = isEnable,
            onClick = onSubscribeClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
            )

        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            Text(stringResource(R.string.add_subscription))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.subscriptions_label, subscriptions.size),
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (subscriptions.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(subscriptions.toList()) { (topic, isSelected) ->
                    SubscriptionChip(
                        modifier = Modifier,
                        topic = topic,
                        onChipClick = onTopicClick,
                        onDeleteClick = onDeleteClick,
                        isSelected = isSelected
                    )
                }
            }
        } else {
            Text(
                text = "No subscriptions",
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
private fun ArticleCard(
    modifier: Modifier = Modifier,
    article: Article
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        article.imageUrl?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            text = article.title,
            maxLines = 2,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (article.description.isNotBlank()) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = article.description,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = article.sourceName)
            Text(text = article.publishedAt.toDateFormat())
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val context = LocalContext.current
            Button(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, article.url.toUri())
                    context.startActivity(intent)
                },

                ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.read_article)
                )
            }
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "${article.title}\n\n${article.url.toUri()}")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.share_article)
                )
            }
        }
    }
}