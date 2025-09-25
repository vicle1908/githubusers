package com.example.githubusers.core.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Aggregates the callbacks required to run a keyset-based [RemoteMediator].
 */
data class KeysetRemoteMediatorCallbacks<Key : Any, Value : Any>(
    val loadCurrentKey: suspend () -> Key?,
    val fetch: suspend (loadType: LoadType, key: Key?, pageSize: Int) -> List<Value>,
    val store: suspend (loadType: LoadType, items: List<Value>, nextKey: Key?) -> Unit,
    val clear: suspend () -> Unit,
    val resolveNextKey: suspend (items: List<Value>, previousKey: Key?) -> Key?
)

/**
 * Generic keyset-based [RemoteMediator] (e.g., GitHub "since" pagination).
 */
@OptIn(ExperimentalPagingApi::class)
class KeysetRemoteMediator<Key : Any, Value : Any>(
    private val callbacks: KeysetRemoteMediatorCallbacks<Key, Value>,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val initializeActionProvider: suspend () -> InitializeAction = { InitializeAction.LAUNCH_INITIAL_REFRESH }
) : RemoteMediator<Key, Value>() {

    override suspend fun initialize(): InitializeAction = initializeActionProvider()

    override suspend fun load(loadType: LoadType, state: PagingState<Key, Value>): MediatorResult =
        withContext(ioDispatcher) {
            if (loadType == LoadType.PREPEND) {
                return@withContext MediatorResult.Success(endOfPaginationReached = true)
            }

            runCatching {
                if (loadType == LoadType.REFRESH) {
                    callbacks.clear()
                }

                val currentKey = if (loadType == LoadType.REFRESH) null else callbacks.loadCurrentKey()
                val items = callbacks.fetch(loadType, currentKey, state.config.pageSize)
                val nextKey = callbacks.resolveNextKey(items, currentKey)
                callbacks.store(loadType, items, nextKey)
                MediatorResult.Success(endOfPaginationReached = items.isEmpty())
            }.getOrElse { throwable ->
                MediatorResult.Error(throwable)
            }
        }
}
