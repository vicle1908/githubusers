package com.example.githubusers.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.githubusers.feature.search.presentation.navigation.SearchRoute
import com.example.githubusers.feature.users.detail.presentation.ui.UserDetailScreen
import com.example.githubusers.feature.users.detail.presentation.viewmodel.UserDetailViewModel
import com.example.githubusers.feature.users.list.presentation.ui.UserListScreen
import com.example.githubusers.feature.users.list.presentation.viewmodel.UserListViewModel
import com.example.githubusers.navigation.api.AppDestination
import com.example.githubusers.navigation.api.Navigation3Controller
import com.example.githubusers.navigation.impl.Navigation3Host
import com.example.githubusers.presentation.theme.GithubUsersTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var controller: Navigation3Controller
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GithubUsersTheme {
                MainNavGraph(
                    controller = controller,
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
    controller: Navigation3Controller,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by controller.currentEntry.collectAsState(null)
    val currentDestination = navBackStackEntry?.destination
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Github Users",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                },
                navigationIcon = {
                },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { padding ->
        Navigation3Host(
            controller = controller,
            startDestination = AppDestination.UserList,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) { entry ->
            when (entry.destination) {
                is AppDestination.UserList -> {
                    val viewModel: UserListViewModel = hiltViewModel()
                    UserListScreen(
                        viewModel = viewModel,
                    )
                }
                is AppDestination.UserDetail -> {
                    val destination = entry.destination as AppDestination.UserDetail
                    // Use Navigation3Entry arguments for proper Navigation 3 integration
                    val viewModel: UserDetailViewModel = hiltViewModel()
                    val uiState by viewModel.uiState.collectAsState()
                    val repositoriesFlow = viewModel.repositoriesFlow.collectAsLazyPagingItems()

                    UserDetailScreen(
                        uiState = uiState,
                        repositoriesFlow = repositoriesFlow,
                        onIntent = { intent ->
                            viewModel.onIntent(intent)
                        },
                        onBackClick = {
                            controller.navigateUp()
                        },
                    )
                }
                is AppDestination.Search -> {
                    SearchRoute(
                        navigator = object : com.example.githubusers.feature.search.presentation.navigation.SearchNavigator {
                            override fun navigateToUserDetail(username: String) {
                                controller.navigate(AppDestination.UserDetail(username))
                            }
                            
                            override fun navigateBack() {
                                controller.navigateUp()
                            }
                        }
                    )
                }
            }
        }
    }
}
