package com.example.githubusers.feature.users.presentation.list.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.githubusers.core.ui.list.StandardUserList
import com.example.githubusers.core.ui.list.StandardUserListLayout
import com.example.githubusers.core.ui.list.UserListItem
import com.example.githubusers.core.users.domain.UserSummary
import com.example.githubusers.feature.users.shared.ui.ColumnCenteredAction
import com.example.githubusers.feature.users.shared.ui.ColumnCenteredMessage
import com.example.githubusers.feature.users.shared.ui.TextButtonLink
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
                action = ColumnCenteredAction(
                    label = "Retry",
                    onClick = retry,
                    isError = true
                )
            )
        },
        appendErrorContent = { _, retry ->
            TextButtonLink(label = "Tap to retry", onClick = retry)
        },
        itemContent = { user ->
            UserListItem(
                user = user,
                onClick = { onUserClick(user) }
            )
        }
    )
}
