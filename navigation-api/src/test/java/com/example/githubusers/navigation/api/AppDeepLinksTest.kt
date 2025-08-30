package com.example.githubusers.navigation.api

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppDeepLinksTest {
    @Test
    fun buildAndParse_userDetail_appScheme() {
        val link = AppDeepLinks.build(AppDestination.UserDetail("octocat"))
        val parsed = AppDeepLinks.parse(link)
        assertEquals(AppDestination.UserDetail("octocat"), parsed)
    }

    @Test
    fun buildAndParse_userDetail_web() {
        val web = "https://${AppDeepLinks.WEB_HOST}/user/octocat"
        val parsed = AppDeepLinks.parse(web)
        assertEquals(AppDestination.UserDetail("octocat"), parsed)
    }

    @Test
    fun buildAndParse_search_withQuery() {
        val link = AppDeepLinks.build(AppDestination.Search("android ui"))
        val parsed = AppDeepLinks.parse(link)
        assertEquals(AppDestination.Search("android ui"), parsed)
    }

    @Test
    fun buildAndParse_search_noQuery() {
        val link = AppDeepLinks.build(AppDestination.Search(null))
        val parsed = AppDeepLinks.parse(link)
        assertEquals(AppDestination.Search(null), parsed)
    }

    @Test
    fun buildAndParse_settings() {
        val link = AppDeepLinks.build(AppDestination.Settings)
        val parsed = AppDeepLinks.parse(link)
        assertEquals(AppDestination.Settings, parsed)
    }

    @Test
    fun parse_web_user_list() {
        val parsed = AppDeepLinks.parse("https://${AppDeepLinks.WEB_HOST}/users")
        assertEquals(AppDestination.UserList, parsed)
    }

    @Test
    fun parse_web_search_with_query() {
        val parsed = AppDeepLinks.parse("https://${AppDeepLinks.WEB_HOST}/search?q=android")
        assertEquals(AppDestination.Search("android"), parsed)
    }
}
