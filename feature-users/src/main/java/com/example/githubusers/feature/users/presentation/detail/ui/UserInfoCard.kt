package com.example.githubusers.feature.users.presentation.detail.ui

import android.R
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.githubusers.feature.users.domain.model.UserDetail

/**
 * Card displaying user information.
 */
@Composable
fun UserInfoCard(
    userDetail: UserDetail,
    isFollowing: Boolean,
    isFollowActionInProgress: Boolean,
    isBioExpanded: Boolean,
    onFollowClick: () -> Unit,
    onBioClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // User avatar and basic info
            UserAvatarAndBasicInfo(
                userDetail = userDetail,
                isFollowing = isFollowing,
                isFollowActionInProgress = isFollowActionInProgress,
                onFollowClick = onFollowClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bio
            UserBio(
                bio = userDetail.bio,
                isBioExpanded = isBioExpanded,
                onBioClick = onBioClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stats row
            UserStats(
                followers = userDetail.followers,
                following = userDetail.following,
                publicRepos = userDetail.publicRepos
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Additional info
            UserAdditionalInfo(
                company = userDetail.company,
                location = userDetail.location
            )
        }
    }
}

@Composable
private fun UserAvatarAndBasicInfo(
    userDetail: UserDetail,
    isFollowing: Boolean,
    isFollowActionInProgress: Boolean,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model =
            ImageRequest
                .Builder(LocalContext.current)
                .data(userDetail.avatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = "${userDetail.login}'s avatar",
            placeholder = painterResource(R.drawable.ic_menu_gallery),
            error = painterResource(R.drawable.ic_menu_report_image),
            contentScale = ContentScale.Crop,
            modifier =
            Modifier
                .size(80.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Name
            Text(
                text = userDetail.name ?: userDetail.login,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Username
            if (userDetail.name != null) {
                Text(
                    text = "@${userDetail.login}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Follow button
            FollowButton(
                isFollowing = isFollowing,
                isFollowActionInProgress = isFollowActionInProgress,
                onFollowClick = onFollowClick
            )
        }
    }
}

@Composable
private fun FollowButton(
    isFollowing: Boolean,
    isFollowActionInProgress: Boolean,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isFollowActionInProgress) {
        CircularProgressIndicator(
            modifier = modifier.size(24.dp)
        )
    } else {
        if (isFollowing) {
            OutlinedButton(
                onClick = onFollowClick,
                modifier = modifier.fillMaxWidth()
            ) {
                Text("Unfollow")
            }
        } else {
            Button(
                onClick = onFollowClick,
                modifier = modifier.fillMaxWidth()
            ) {
                Text("Follow")
            }
        }
    }
}

@Composable
private fun UserBio(bio: String?, isBioExpanded: Boolean, onBioClick: () -> Unit, modifier: Modifier = Modifier) {
    bio?.let { bioText ->
        Text(
            text = bioText,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (isBioExpanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            modifier =
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable { onBioClick() }
        )
    }
}

@Composable
private fun UserStats(followers: Int, following: Int, publicRepos: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            count = followers,
            label = "Followers"
        )
        StatItem(
            count = following,
            label = "Following"
        )
        StatItem(
            count = publicRepos,
            label = "Repos"
        )
    }
}

@Composable
private fun UserAdditionalInfo(company: String?, location: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        company?.let { companyValue ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Company",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = companyValue,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        location?.let { locationValue ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = locationValue,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatItem(count: Int, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
