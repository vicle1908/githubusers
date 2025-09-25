@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.example.githubusers.feature.repository.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ForkLeft
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.githubusers.feature.repository.domain.model.RepositoryDetail
import com.example.githubusers.feature.repository.presentation.viewmodel.RepositoryDetailUiState
import com.example.githubusers.feature.repository.presentation.viewmodel.RepositoryDetailViewModel
import com.example.githubusers.navigation.api.LocalNavigateBack

@Composable
fun RepositoryDetailRoute(owner: String, name: String, viewModel: RepositoryDetailViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(owner, name) {
        viewModel.loadRepository(owner, name)
    }

    RepositoryDetailScreen(
        uiState = uiState,
        owner = owner,
        name = name
    )
}

@Composable
private fun RepositoryDetailScreen(uiState: RepositoryDetailUiState, owner: String, name: String) {
    val navigateBack = LocalNavigateBack.current
    RepositoryDetailScaffold(
        title = "$owner/$name",
        onBack = { navigateBack() }
    ) { innerPadding ->
        RepositoryDetailStateContent(
            uiState = uiState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        )
    }
}

@Composable
private fun RepositoryDetailScaffold(title: String, onBack: () -> Unit, content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO hook up share/star actions */ }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More actions")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        content = content
    )
}

@Composable
private fun RepositoryDetailStateContent(uiState: RepositoryDetailUiState, modifier: Modifier = Modifier) {
    when (uiState) {
        RepositoryDetailUiState.Loading -> RepositoryDetailLoading(modifier)
        is RepositoryDetailUiState.Error -> RepositoryDetailError(message = uiState.message, modifier = modifier)
        is RepositoryDetailUiState.Success -> RepositoryDetailContent(
            repository = uiState.repository,
            modifier = modifier
        )
    }
}

@Composable
private fun RepositoryDetailLoading(modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Loading repository details...")
    }
}

@Composable
private fun RepositoryDetailError(message: String, modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error loading repository",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun RepositoryDetailContent(repository: RepositoryDetail, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repositoryDescriptionSection(repository)
        repositoryStatsSection(repository)
        repositoryDetailsSection(repository)
        repositoryTopicsSection(repository.topics)
    }
}

private fun LazyListScope.repositoryDescriptionSection(repository: RepositoryDetail) {
    val description = repository.description.orEmpty()
    sectionCardItem(
        title = "Description",
        visible = description.isNotBlank()
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun LazyListScope.repositoryStatsSection(repository: RepositoryDetail) {
    sectionCardItem(title = "Statistics") {
        StatsRow(
            icon = Icons.Filled.Star,
            label = "Stars",
            value = repository.stargazersCount.toString()
        )
        StatsRow(
            icon = Icons.Filled.People,
            label = "Watchers",
            value = repository.watchersCount.toString()
        )
        StatsRow(
            icon = Icons.Filled.ForkLeft,
            label = "Forks",
            value = repository.forksCount.toString()
        )
        StatsRow(
            icon = Icons.Filled.Code,
            label = "Open Issues",
            value = repository.openIssuesCount.toString()
        )
    }
}

private fun LazyListScope.repositoryDetailsSection(repository: RepositoryDetail) {
    sectionCardItem(title = "Details") {
        DetailRow(
            icon = Icons.Filled.Language,
            label = "Language",
            value = repository.language ?: "Not specified"
        )
        repository.licenseName?.let { license ->
            DetailRow(
                icon = Icons.Filled.Storage,
                label = "License",
                value = license
            )
        }
        DetailRow(
            icon = Icons.Filled.DateRange,
            label = "Last Push",
            value = repository.pushedAt
        )
        repository.homepage?.takeIf { it.isNotBlank() }?.let { homepage ->
            DetailRow(
                icon = Icons.Filled.Language,
                label = "Homepage",
                value = homepage
            )
        }
    }
}

private fun LazyListScope.repositoryTopicsSection(topics: List<String>) {
    sectionCardItem(
        title = "Topics",
        visible = topics.isNotEmpty()
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            topics.forEach { topic ->
                TopicChip(topic = topic)
            }
        }
    }
}

private fun LazyListScope.sectionCardItem(
    title: String,
    visible: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    if (!visible) return
    item {
        SectionCard(title = title) {
            content()
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun StatsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    RowContent(
        icon = icon,
        label = label,
        value = value,
        modifier = modifier
    )
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    RowContent(
        icon = icon,
        label = label,
        value = value,
        modifier = modifier
    )
}

@Composable
private fun RowContent(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun TopicChip(topic: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Topic,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = topic,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
