package com.example.githubusers.core.paging

import timber.log.Timber
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Lightweight base [RemoteMediator] that centralises paging mechanics
 * while delegating storage concerns to the caller via hooks.
 */
@OptIn(ExperimentalPagingApi::class)
abstract class BaseRemoteMediator<Value : Any>(
    private val startingPage: Int = 1,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteMediator<Int, Value>() {

    private var nextPage = startingPage

    /** Fetch a page of data from the network. */
    protected abstract suspend fun fetch(page: Int, pageSize: Int): List<Value>

    /** Persist the fetched page. */
    protected abstract suspend fun store(items: List<Value>, loadType: LoadType)

    /** Clear local cache before a refresh. */
    protected open suspend fun clear() {}

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Value>): MediatorResult =
        withContext(ioDispatcher) {
            runCatching {
                when (loadType) {
                    LoadType.REFRESH -> {
                        nextPage = startingPage
                        clear()
                        val items = fetch(nextPage, state.config.pageSize)
                        store(items, loadType)
                        if (items.isNotEmpty()) {
                            nextPage++
                        }
                        MediatorResult.Success(endOfPaginationReached = items.isEmpty())
                    }

                    LoadType.APPEND -> {
                        val items = fetch(nextPage, state.config.pageSize)
                        store(items, loadType)
                        if (items.isNotEmpty()) {
                            nextPage++
                        }
                        MediatorResult.Success(endOfPaginationReached = items.isEmpty())
                    }

                    LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)
                }
            }.getOrElse { throwable ->
                Timber.e(
                    throwable,
                    "Remote mediator load failed for type %s, page %d",
                    loadType,
                    nextPage
                )
                MediatorResult.Error(throwable)
            }
        }
}
