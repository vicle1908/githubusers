package com.example.githubusers.feature.users.di

import androidx.paging.map
import com.example.githubusers.feature.users.FeatureUsersRepository
import com.example.githubusers.feature.users.UserDetailUi
import com.example.githubusers.feature.users.UserUi
import com.example.githubusers.feature.users.data.repository.UserRepositoryImpl
import com.example.githubusers.feature.users.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureUsersModule {
    @Binds
    @Singleton
    abstract fun bindFeatureUsersRepository(impl: FeatureUsersRepositoryImpl): FeatureUsersRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}

@Singleton
class FeatureUsersRepositoryImpl @javax.inject.Inject constructor(private val appRepo: UserRepository) :
    FeatureUsersRepository {
    override fun getUsersPaged() = appRepo.getUsersPaged().mapPaging { user ->
        UserUi(id = user.id.toInt(), username = user.login, avatarUrl = user.avatarUrl, htmlUrl = user.htmlUrl)
    }

    override fun getUserDetail(username: String) = flow {
        val result = appRepo.getUserDetail(username)
        emit(
            result.map { detail ->
                detail?.let {
                    UserDetailUi(
                        id = it.id.toInt(),
                        username = it.login,
                        avatarUrl = it.avatarUrl,
                        htmlUrl = it.htmlUrl,
                        location = it.location,
                        followers = it.followers,
                        following = it.following,
                        blog = it.blog
                    )
                }
            }
        )
    }
}

private fun <T : Any, R : Any> kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<T>>.mapPaging(
    transform: (T) -> R
): kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<R>> = this.map { data: androidx.paging.PagingData<T> ->
    data.map { item: T -> transform(item) }
}
