package com.example.githubusers.core.analytics

/**
 * Analytics façade to avoid leaking SDK details across modules.
 * Replace this with a real implementation that forwards to your analytics SDK.
 */
interface AnalyticsFacade {
    fun track(event: AnalyticsEvent)
}

/** Envelope for all analytics events with version for evolution. */
data class AnalyticsEvent(val name: String, val payload: Map<String, Any?>, val version: Int = 1)

/** A no-op test/dummy implementation for development and tests. */
class FakeAnalytics : AnalyticsFacade {
    private val _events = mutableListOf<AnalyticsEvent>()
    val events: List<AnalyticsEvent> get() = _events

    override fun track(event: AnalyticsEvent) {
        _events.add(event)
    }
}
