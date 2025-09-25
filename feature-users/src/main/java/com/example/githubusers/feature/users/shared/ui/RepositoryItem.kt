package com.example.githubusers.feature.users.shared.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.githubusers.feature.users.domain.model.Repository
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
* Individual repository item in the list.
 */
@Composable
fun RepositoryItem(repository: Repository, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Repository name
            Text(
                text = repository.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            forkIndicator(repository = repository)

            descriptionSection(description = repository.description)

            Spacer(modifier = Modifier.height(12.dp))

            topicsSection(topics = repository.topics)

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun forkIndicator(repository: Repository) {
    if (repository.isFork) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "Forked",
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Forked repository",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun descriptionSection(description: String?) {
    description?.let { desc ->
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = desc,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun topicsSection(topics: List<String>) {
    if (topics.isNotEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            topics.take(3).forEach { topic ->
                topicChip(topic = topic)
            }
            if (topics.size > 3) {
                Text(
                    text = "+${topics.size - 3}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun statsRow(language: String?, stargazersCount: Int, forksCount: Int, updatedAt: java.time.Instant) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repositoryStats(
            language = language,
            stargazersCount = stargazersCount,
            forksCount = forksCount
        )

        Text(
            text =
            DateTimeFormatter
                .ofPattern("MMM d, yyyy")
                .format(updatedAt.atZone(ZoneId.systemDefault()).toLocalDate()),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun repositoryStats(language: String?, stargazersCount: Int, forksCount: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        language?.let { lang ->
            languageInfo(language = lang)
        }

        starsInfo(stargazersCount = stargazersCount)

        forksInfo(forksCount = forksCount)
    }
}

@Composable
private fun languageInfo(language: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        languageDot(language = language)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = language,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun starsInfo(stargazersCount: Int, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Stars",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stargazersCount.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun forksInfo(forksCount: Int, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = "Forks",
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = forksCount.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun topicChip(topic: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(
            text = topic,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun languageDot(language: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.size(8.dp),
        colors =
        CardDefaults.cardColors(
            containerColor = getLanguageColor(language)
        )
    ) {}
}

private fun getLanguageColor(language: String): androidx.compose.ui.graphics.Color = when (language.lowercase()) {
    "kotlin" ->
        androidx.compose.ui.graphics
            .Color(0xFFF18E33)
    "java" ->
        androidx.compose.ui.graphics
            .Color(0xFFB07219)
    "javascript" ->
        androidx.compose.ui.graphics
            .Color(0xFFF1E05A)
    "typescript" ->
        androidx.compose.ui.graphics
            .Color(0xFF2B7489)
    "python" ->
        androidx.compose.ui.graphics
            .Color(0xFF3572A5)
    "swift" ->
        androidx.compose.ui.graphics
            .Color(0xFFFFAC45)
    "go" ->
        androidx.compose.ui.graphics
            .Color(0xFF00ADD8)
    "rust" ->
        androidx.compose.ui.graphics
            .Color(0xFFDEA584)
    "c++" ->
        androidx.compose.ui.graphics
            .Color(0xFFF34B7D)
    "c" ->
        androidx.compose.ui.graphics
            .Color(0xFF555555)
    else -> androidx.compose.ui.graphics.Color.Gray
}
