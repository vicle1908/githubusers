package com.example.githubusers.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
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
import com.example.githubusers.presentation.ui.screens.UserDetailScreen
import com.example.githubusers.presentation.ui.screens.UserListScreen
import com.example.githubusers.presentation.ui.theme.GithubUsersTheme
import com.example.githubusers.presentation.viewmodel.UserDetailViewModel
import com.example.githubusers.presentation.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GithubUsersTheme {
                val navController = rememberNavController()
                Scaffold { paddingValues ->
                    MainNavGraph(navController = navController, modifier = Modifier.padding(paddingValues))
                }
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
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
                modifier = Modifier.fillMaxWidth(), // Removed explicit height
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "userList",
            modifier = Modifier.padding(paddingValues),
        ) {
            composable("userList") {
                val viewModel: UserListViewModel = hiltViewModel()
                UserListScreen(
                    onUserClick = { user ->
                        navController.navigate("userDetail/${user.login}")
                    },
                    viewModel = viewModel,
                    scrollBehavior = scrollBehavior,
                )
            }
            composable("userDetail/{username}") { backStackEntry ->
                val viewModel: UserDetailViewModel = hiltViewModel()
                val username = backStackEntry.arguments?.getString("username")
                if (username == null) {
                    navController.navigateUp()
                    return@composable
                }
                UserDetailScreen(username = username, viewModel = viewModel)
            }
        }
    }
}
