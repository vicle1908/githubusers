package com.example.githubusers.feature.repository.domain.usecase

import com.example.githubusers.feature.repository.domain.model.RepositoryDetail
import com.example.githubusers.feature.repository.domain.repository.RepositoryRepository
import javax.inject.Inject

class GetRepositoryDetailUseCase @Inject constructor(private val repository: RepositoryRepository) {
    suspend operator fun invoke(owner: String, name: String): Result<RepositoryDetail?> =
        repository.getRepository(owner, name)
}
