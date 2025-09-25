package com.example.githubusers.feature.repository.presentation.state

import com.example.githubusers.feature.repository.domain.model.Repository

data class RepositoryListState(val selectedRepository: Repository? = null)
