package com.example.githubusers.feature.users.list.presentation.analytics

import com.example.githubusers.core.analytics.AnalyticsEvent
import com.example.githubusers.core.analytics.AnalyticsFacade
import com.example.githubusers.core.users.domain.UserSummary
import javax.inject.Inject
import timber.log.Timber

interface UserListAnalytics {
    fun onScreenDisplayed(viewModel: String)
    fun onQueryChanged(query: String)
    fun onRetry()
    fun onRefresh()
    fun onUserSelected(user: UserSummary)
    fun onOpenSearch(query: String?)
    fun onOpenSettings()
}

class DefaultUserListAnalytics @Inject constructor(private val analytics: AnalyticsFacade) : UserListAnalytics {

    override fun onScreenDisplayed(viewModel: String) {
        Timber.tag(TAG).d("Screen displayed with VM=%s", viewModel)
        analytics.track(
            AnalyticsEvent(
                name = "user_list_screen_displayed",
                payload = mapOf("view_model" to viewModel)
            )
        )
    }

    override fun onQueryChanged(query: String) {
        Timber.tag(TAG).d("Query changed to='%s'", query)
        analytics.track(
            AnalyticsEvent(
                name = "user_list_query_changed",
                payload = mapOf(
                    "query" to query,
                    "is_blank" to query.isBlank()
                )
            )
        )
    }

    override fun onRetry() {
        Timber.tag(TAG).d("Retry requested")
        analytics.track(AnalyticsEvent(name = "user_list_retry", payload = emptyMap()))
    }

    override fun onRefresh() {
        Timber.tag(TAG).d("Manual refresh requested")
        analytics.track(AnalyticsEvent(name = "user_list_refresh", payload = emptyMap()))
    }

    override fun onUserSelected(user: UserSummary) {
        Timber.tag(TAG).d("User selected: %s", user.login)
        analytics.track(
            AnalyticsEvent(
                name = "user_list_user_selected",
                payload = mapOf(
                    "login" to user.login,
                    "id" to user.id,
                    "type" to user.type
                )
            )
        )
    }

    override fun onOpenSearch(query: String?) {
        Timber.tag(TAG).d("Advanced search opened; query='%s'", query)
        analytics.track(
            AnalyticsEvent(
                name = "user_list_open_search",
                payload = mapOf("query" to query)
            )
        )
    }

    override fun onOpenSettings() {
        Timber.tag(TAG).d("Settings opened")
        analytics.track(AnalyticsEvent(name = "user_list_open_settings", payload = emptyMap()))
    }

    companion object {
        private const val TAG = "UserListAnalytics"
    }
}
