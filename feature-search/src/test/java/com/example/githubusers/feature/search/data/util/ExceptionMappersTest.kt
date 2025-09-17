package com.example.githubusers.feature.search.data.util

import com.example.githubusers.core.search.domain.SearchError
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.test.assertEquals
import org.junit.Test

class ExceptionMappersTest {
    @Test
    fun `IOException maps to Network`() {
        val e = IOException("io")
        val mapped = e.toSearchError()
        assertEquals(SearchError.Network, mapped)
    }

    @Test
    fun `SocketTimeoutException maps to Timeout`() {
        val e = SocketTimeoutException("timeout")
        val mapped = e.toSearchError()
        assertEquals(SearchError.Timeout, mapped)
    }

    @Test
    fun `HttpRequestTimeoutException maps to Timeout`() {
        val e = HttpRequestTimeoutException("timeout")
        val mapped = e.toSearchError()
        assertEquals(SearchError.Timeout, mapped)
    }
}