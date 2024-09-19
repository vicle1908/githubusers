package com.example.githubusers.presentation.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.githubusers.data.remote.dto.UserDetailDto
import com.example.githubusers.data.remote.dto.toEntity
import com.example.githubusers.domain.entity.UserDetail
import com.example.githubusers.presentation.state.userdetail.UserDetailState
import com.example.githubusers.presentation.viewmodel.UserDetailViewModel
import kotlinx.serialization.json.Json

@Composable
fun UserDetailScreen(
    username: String,
    viewModel: UserDetailViewModel = hiltViewModel(), // Inject HiltViewModel here
) {
    // Call fetchUserDetail when the screen is first loaded
    LaunchedEffect(username) {
        viewModel.fetchUserDetail(username)
    }

    val userDetailState = viewModel.userDetailState.collectAsState()

    when (val state = userDetailState.value) {
        is UserDetailState.Loading -> {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }
        is UserDetailState.Success -> {
            val user = state.userDetail
            if (user != null) {
                DetailContent(user = user)
            } else {
                Text(
                    text = "User not found",
                    modifier = Modifier.fillMaxSize(),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        is UserDetailState.Error -> {
            Text(
                text = state.message,
                modifier = Modifier.fillMaxSize(),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
fun DetailContent(user: UserDetail) {
    val context = LocalContext.current

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Profile Card
        Card(
            shape = MaterialTheme.shapes.medium,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            elevation = CardDefaults.elevatedCardElevation(),
            colors = CardDefaults.cardColors(),
        ) {
            Row(
                modifier =
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Circular avatar
                Image(
                    painter = rememberAsyncImagePainter(user.avatarUrl),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(72.dp)
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    // Bold Username
                    Text(
                        text = user.login,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )

                    // Location with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = user.location ?: "N/A")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Followers and Following section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                )
                Text(text = "${user.followers} Followers", style = MaterialTheme.typography.bodyMedium)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                )
                Text(text = "${user.following} Following", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Blog URL section
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
        ) {
            Text(text = "Blog", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

            Spacer(modifier = Modifier.height(8.dp))

            val annotatedBlogString =
                buildAnnotatedString {
                    val startIndex = length
                    append(user.blog ?: "No Blog")
                    if (user.blog != null) {
                        addStyle(
                            style =
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline,
                                ),
                            start = startIndex,
                            end = length,
                        )
                        addStringAnnotation(
                            tag = "URL",
                            annotation = user.blog,
                            start = startIndex,
                            end = length,
                        )
                    }
                }

            Text(
                text = annotatedBlogString,
                style = MaterialTheme.typography.bodyMedium,
                modifier =
                    Modifier.clickable {
                        user.blog?.let {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                            context.startActivity(intent)
                        }
                    },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserDetailScreen() {
    val mockUserDetailJson =
        """
{
  "login": "defunkt",
  "id": 2,
  "avatar_url": "https://avatars.githubusercontent.com/u/2?v=4",
  "html_url": "https://github.com/defunkt",
  "followers": 22385,
  "following": 215,
  "blog": "http://chriswanstrath.com/",
  "location": "San Francisco",
  "name": "Chris Wanstrath"
}
        """.trimIndent()

    // Function to parse JSON for a user detail
    val mockUserDetail = Json.decodeFromString<UserDetailDto>(mockUserDetailJson).toEntity()
    DetailContent(user = mockUserDetail)
}
