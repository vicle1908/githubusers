package com.example.githubusers.core.paging

import androidx.paging.PagingConfig

object PagingProfiles {
    fun defaultList(): PagingConfig = PagingConfig(
        pageSize = 30,
        prefetchDistance = 10,
        initialLoadSize = 30,
        enablePlaceholders = false
    )

    fun compactList(): PagingConfig = PagingConfig(
        pageSize = 20,
        prefetchDistance = 5,
        initialLoadSize = 20,
        enablePlaceholders = false
    )
}
