package com.example.githubusers.feature.users.di

import androidx.paging.map
import com.example.githubusers.feature.users.FeatureUsersRepository
import com.example.githubusers.feature.users.UserDetailUi
import com.example.githubusers.feature.users.UserUi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

/**
 * Hilt module to allow the app to bind its repository implementation to the feature interface.
 * By default, throws if not provided by the app, to make missing bindings explicit.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureUsersModule {
    @Binds
    @Singleton
    abstract fun bindFeatureUsersRepository(impl: FeatureUsersRepositoryImpl): FeatureUsersRepository
}

/**
 * Default implementation that delegates to app's repository via adapters.
 * Replace this with a proper binding in the app if needed.
 */
@Singleton
class FeatureUsersRepositoryImpl
    @javax.inject.Inject
    constructor(
        private val appRepo: com.example.githubusers.core.domain.repository.UserRepository,
    ) : FeatureUsersRepository {
        override fun getUsersPaged(query: String) =
            appRepo.getUsersPaged(query).mapPaging { user ->
                UserUi(id = user.id, username = user.login, avatarUrl = user.avatarUrl, htmlUrl = user.htmlUrl)
            }

        override fun getUserDetail(username: String) =
            appRepo.getUserDetail(username).map { result ->
                result.map { detail ->
                    detail?.let {
                        UserDetailUi(
                            id = it.id,
                            username = it.login,
                            avatarUrl = it.avatarUrl,
                            htmlUrl = it.htmlUrl,
                            location = it.location,
                            followers = it.followers,
                            following = it.following,
                            blog = it.blog,
                        )
                    }
                }
            }
    }

// Small helper to map PagingData within a Flow
private fun <T : Any, R : Any> kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<T>>.mapPaging(
    transform: (T) -> R,
): kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<R>> =
    this.map { data: androidx.paging.PagingData<T> ->
        data.map { item: T -> transform(item) }
    }
