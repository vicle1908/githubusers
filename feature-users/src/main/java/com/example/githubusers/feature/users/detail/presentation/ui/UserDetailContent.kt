package com.example.githubusers.feature.users.detail.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.githubusers.feature.users.detail.domain.entity.Repository
import com.example.githubusers.feature.users.detail.domain.entity.UserDetail

/**
 * Content composable for user detail screen.
 */
@Composable
fun UserDetailContent(
    userDetail: UserDetail,
    isFollowing: Boolean,
    isFollowActionInProgress: Boolean,
    isBioExpanded: Boolean,
    repositories: LazyPagingItems<Repository>,
    onFollowClick: () -> Unit,
    onBioClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Repositories")

    Column(modifier = modifier.fillMaxSize()) {
        // User info card
        UserInfoCard(
            userDetail = userDetail,
            isFollowing = isFollowing,
            isFollowActionInProgress = isFollowActionInProgress,
            isBioExpanded = isBioExpanded,
            onFollowClick = onFollowClick,
            onBioClick = onBioClick,
            modifier = Modifier.fillMaxWidth(),
        )

        HorizontalDivider()

        // Tab row
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    },
                )
            }
        }

        // Tab content
        when (selectedTabIndex) {
            0 -> {
                // Overview tab
                UserOverview(
                    userDetail = userDetail,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                )
            }
            1 -> {
                // Repositories tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        count = repositories.itemCount,
                        key = repositories.itemKey { it.id },
                        contentType = repositories.itemContentType { "repository" },
                    ) { index ->
                        val repository = repositories[index]
                        repository?.let {
                            RepositoryItem(
                                repository = it,
                                onClick = { /* Handle repository click */ },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
