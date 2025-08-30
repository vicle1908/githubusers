package com.example.githubusers.navigation.generated

/** Provider interface discovered via ServiceLoader to supply deep link ownership maps. */
interface DeepLinkOwnersProvider {
    fun owners(): Map<String, String>
}
