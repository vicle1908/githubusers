@file:Suppress("ktlint:standard:function-naming")

package com.example.githubusers.feature.users.list.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.githubusers.core.ui.list.StandardUserList
import com.example.githubusers.core.ui.list.StandardUserListLayout
import com.example.githubusers.core.ui.list.StandardUserRow
import com.example.githubusers.core.users.domain.UserSummary
import timber.log.Timber

/**
 * Content component displaying the list of users with adaptive layout.
 */
@Composable
fun UserListContent(
    pagingUsers: LazyPagingItems<UserSummary>,
    onUserClick: (UserSummary) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Timber.tag(
        "UserListContent"
    ).d("Load state: ${pagingUsers.loadState.refresh}, Item count: ${pagingUsers.itemCount}")

    StandardUserList(
        pagingItems = pagingUsers,
        modifier = modifier,
        layout = StandardUserListLayout.LandscapeGrid(
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
            horizontalSpacing = 4.dp,
            verticalSpacing = 2.dp
        ),
        onRefresh = onRefresh,
        emptyContent = {
            Text(
                text = "No users found",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        errorContent = { error, retry ->
            ColumnCenteredMessage(
                message = "Error: ${error.localizedMessage ?: "Unknown"}",
                buttonLabel = "Retry",
                onRetry = retry,
                isError = true
            )
        },
        appendErrorContent = { _, retry ->
            TextButtonLink(label = "Tap to retry", onClick = retry)
        },
        itemContent = { user ->
            StandardUserRow(
                user = user,
                onClick = { onUserClick(user) }
            )
        }
    )
}

@Composable
private fun ColumnCenteredMessage(message: String, buttonLabel: String, onRetry: () -> Unit, isError: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        TextButtonLink(label = buttonLabel, onClick = onRetry)
    }
}

@Composable
private fun TextButtonLink(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(label.uppercase())
    }
}
