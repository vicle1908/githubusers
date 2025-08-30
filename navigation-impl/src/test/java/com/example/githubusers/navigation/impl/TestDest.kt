package com.example.githubusers.navigation.impl

import com.example.githubusers.navigation.api.NavigationDestination

data class TestDest(
    override val route: String,
    override val deepLink: String,
) : NavigationDestination
