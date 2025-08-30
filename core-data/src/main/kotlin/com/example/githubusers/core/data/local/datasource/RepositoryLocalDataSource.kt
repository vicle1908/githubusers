package com.example.githubusers.core.data.local.datasource

import androidx.paging.PagingSource
import com.example.githubusers.core.data.local.dao.RepositoryDao
import com.example.githubusers.core.data.local.entity.RepositoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RepositoryLocalDataSource {
    fun getRepositoriesPaging(ownerLogin: String): PagingSource<Int, RepositoryEntity>
    fun getRepositories(ownerLogin: String): Flow<List<RepositoryEntity>>
    suspend fun getRepositoriesSync(ownerLogin: String): List<RepositoryEntity>
    suspend fun insertRepositories(repositories: List<RepositoryEntity>)
    suspend fun deleteRepositories(ownerLogin: String)
    suspend fun deleteRepositoriesOlderThan(timestamp: Long)
    suspend fun refreshRepositories(ownerLogin: String, repositories: List<RepositoryEntity>)
}

class RepositoryLocalDataSourceImpl @Inject constructor(
    private val repositoryDao: RepositoryDao
) : RepositoryLocalDataSource {
    
    override fun getRepositoriesPaging(ownerLogin: String): PagingSource<Int, RepositoryEntity> = 
        repositoryDao.getRepositoriesByOwnerPaging(ownerLogin)
        
    override fun getRepositories(ownerLogin: String): Flow<List<RepositoryEntity>> = 
        repositoryDao.getRepositoriesByOwner(ownerLogin)
        
    override suspend fun getRepositoriesSync(ownerLogin: String): List<RepositoryEntity> = 
        repositoryDao.getRepositoriesByOwnerSync(ownerLogin)
        
    override suspend fun insertRepositories(repositories: List<RepositoryEntity>) = 
        repositoryDao.insertRepositories(repositories)
        
    override suspend fun deleteRepositories(ownerLogin: String) = 
        repositoryDao.deleteRepositoriesByOwner(ownerLogin)
        
    override suspend fun deleteRepositoriesOlderThan(timestamp: Long) = 
        repositoryDao.deleteRepositoriesOlderThan(timestamp)
        
    override suspend fun refreshRepositories(ownerLogin: String, repositories: List<RepositoryEntity>) = 
        repositoryDao.refreshRepositories(ownerLogin, repositories)
}
