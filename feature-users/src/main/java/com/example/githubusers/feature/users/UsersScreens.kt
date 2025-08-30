package com.example.githubusers.feature.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage

@Composable
fun UsersListScreen(
    viewModel: UsersListViewModel = hiltViewModel(),
    onUserClick: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val usersLazyItems = viewModel.users.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = state.query,
            onQueryChange = { viewModel.onIntent(UsersIntent.SearchChanged(it)) },
            onSearch = { viewModel.onIntent(UsersIntent.SubmitSearch) },
        )

        if (state.error != null) {
            Text(state.error!!, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(usersLazyItems.itemCount) { index ->
                val user = usersLazyItems[index]
                if (user != null) {
                    UserRow(user = user, onClick = { onUserClick(user.username) })
                }
            }
        }
    }
}

@Composable
fun UserDetailScreen(
    username: String,
    viewModel: UserDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    LaunchedEffect(username) {
        viewModel.onIntent(UserDetailIntent.Load(username))
    }

    when (val s = viewModel.state.collectAsStateWithLifecycle().value) {
        is UserDetailState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        is UserDetailState.Error -> Text("Error: ${s.message}")
        is UserDetailState.Loaded -> UserDetailContent(s)
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
    var tf by remember { mutableStateOf(TextFieldValue(query)) }
    OutlinedTextField(
        value = tf,
        onValueChange = {
            tf = it
            onQueryChange(it.text)
        },
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
        singleLine = true,
        label = { Text("Search users") },
    )
    Button(onClick = onSearch, modifier = Modifier.padding(horizontal = 8.dp)) {
        Text("Search")
    }
}

@Composable
private fun UserRow(
    user: UserUi,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(text = user.username, style = MaterialTheme.typography.titleMedium)
            user.htmlUrl?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
        }
    }
}

@Composable
private fun UserDetailContent(state: UserDetailState.Loaded) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = state.detail.avatarUrl,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(state.detail.username, style = MaterialTheme.typography.titleLarge)
                Text(state.detail.htmlUrl, style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(12.dp))
        state.detail.location?.let { Text("Location: $it") }
        Text("Followers: ${state.detail.followers}")
        Text("Following: ${state.detail.following}")
        state.detail.blog?.let { Text("Blog: $it") }
    }
}
