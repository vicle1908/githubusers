package com.example.githubusers.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.githubusers.feature.users.detail.presentation.ui.UserDetailScreen
import com.example.githubusers.feature.users.detail.presentation.viewmodel.UserDetailViewModel
import com.example.githubusers.feature.users.list.presentation.ui.UserListScreen
import com.example.githubusers.feature.users.list.presentation.viewmodel.UserListViewModel
import com.example.githubusers.presentation.theme.GithubUsersTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GithubUsersTheme {
                val navController = rememberNavController()
                MainNavGraph(
                    navController = navController,
                    modifier =
                        Modifier
                            .fillMaxSize(),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (currentDestination == "userDetail/{username}") "User Details" else "Github Users",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                },
                navigationIcon = {
                    if (currentDestination == "userDetail/{username}") {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "userList",
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            composable("userList") {
                val viewModel: UserListViewModel = hiltViewModel()
                val state by viewModel.state.collectAsState()

                // Navigate when a user is selected
                LaunchedEffect(state.selectedUser) {
                    state.selectedUser?.let { user ->
                        navController.navigate("userDetail/${user.login}")
                    }
                }

                UserListScreen(
                    viewModel = viewModel,
                )
            }
            composable("userDetail/{username}") { backStackEntry ->
                val viewModel: UserDetailViewModel = hiltViewModel()
                val username = backStackEntry.arguments?.getString("username")
                if (username == null) {
                    navController.navigateUp()
                    return@composable
                }
                val uiState by viewModel.uiState.collectAsState()
                val repositoriesFlow = viewModel.repositoriesFlow.collectAsLazyPagingItems()

                UserDetailScreen(
                    uiState = uiState,
                    repositoriesFlow = repositoriesFlow,
                    onIntent = { intent ->
                        viewModel.onIntent(intent)
                    },
                    onBackClick = {
                        navController.navigateUp()
                    },
                )
            }
        }
    }
}
