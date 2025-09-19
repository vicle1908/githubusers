package com.example.githubusers.core.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.githubusers.core.ui.accessibility.accessibleListItem
import com.example.githubusers.core.users.domain.UserSummary

@Composable
fun UserListItem(
    user: UserSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    position: Int? = null,
    totalItems: Int? = null
) {
    Card(
        modifier =
        modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 0.dp)
            .let { cardModifier ->
                if (position != null && totalItems != null) {
                    cardModifier.accessibleListItem(
                        itemContent = "GitHub user ${user.login}${if (user.type != "User") ", ${user.type}" else ""}",
                        position = position,
                        totalItems = totalItems,
                        hasAction = true,
                        additionalInfo = "Double tap to view user details"
                    )
                } else {
                    cardModifier.semantics {
                        contentDescription =
                            "GitHub user ${user.login}${if (user.type != "User") ", ${user.type}" else ""}. Double tap to view details"
                        role = Role.Button
                    }
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(user.avatarUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "${user.login}'s avatar",
                placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                error = painterResource(android.R.drawable.ic_menu_report_image),
                contentScale = ContentScale.Crop,
                modifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = user.login,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            if (user.type != "User") {
                Text(
                    text = user.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}
