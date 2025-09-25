package com.example.githubusers.feature.users.navigation

import com.example.githubusers.core.analytics.AnalyticsEvent
import com.example.githubusers.core.analytics.AnalyticsFacade
import com.example.githubusers.feature.users.detail.presentation.navigation.UserDetailNavigator
import javax.inject.Inject

fun interface UserDetailNavigatorFactory {
    fun create(
        username: String,
        navigateBack: () -> Unit,
        openRepository: (owner: String, repo: String) -> Unit,
        openUrl: (String) -> Unit
    ): UserDetailNavigator
}

class DefaultUserDetailNavigatorFactory @Inject constructor(private val analyticsFacade: AnalyticsFacade) :
    UserDetailNavigatorFactory {
    override fun create(
        username: String,
        navigateBack: () -> Unit,
        openRepository: (owner: String, repo: String) -> Unit,
        openUrl: (String) -> Unit
    ): UserDetailNavigator = object : UserDetailNavigator {
        override fun navigateBack() {
            analyticsFacade.track(
                AnalyticsEvent(
                    name = "user_detail_back",
                    payload = mapOf("username" to username)
                )
            )
            navigateBack()
        }

        override fun navigateToRepository(owner: String, repo: String) {
            analyticsFacade.track(
                AnalyticsEvent(
                    name = "user_detail_repository_open",
                    payload = mapOf(
                        "username" to username,
                        "owner" to owner,
                        "repo" to repo
                    )
                )
            )
            openRepository(owner, repo)
        }

        override fun openUrl(url: String) {
            analyticsFacade.track(
                AnalyticsEvent(
                    name = "user_detail_open_url",
                    payload = mapOf(
                        "username" to username,
                        "url" to url
                    )
                )
            )
            openUrl(url)
        }
    }
}
