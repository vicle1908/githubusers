package com.example.githubusers.feature.repository.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubusers.feature.repository.domain.model.RepositoryDetail
import com.example.githubusers.feature.repository.domain.usecase.GetRepositoryDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RepositoryDetailViewModel @Inject constructor(
    private val getRepositoryDetailUseCase: GetRepositoryDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RepositoryDetailUiState>(RepositoryDetailUiState.Loading)
    val uiState: StateFlow<RepositoryDetailUiState> = _uiState.asStateFlow()

    fun loadRepository(owner: String, name: String) {
        viewModelScope.launch {
            _uiState.value = RepositoryDetailUiState.Loading

            getRepositoryDetailUseCase(owner, name)
                .onSuccess { repositoryDetail ->
                    if (repositoryDetail != null) {
                        _uiState.value = RepositoryDetailUiState.Success(repositoryDetail)
                    } else {
                        _uiState.value = RepositoryDetailUiState.Error("Repository not found")
                    }
                }
                .onFailure { throwable ->
                    _uiState.value = RepositoryDetailUiState.Error(throwable.message ?: "Unknown error")
                }
        }
    }
}

sealed interface RepositoryDetailUiState {
    object Loading : RepositoryDetailUiState
    data class Success(val repository: RepositoryDetail) : RepositoryDetailUiState
    data class Error(val message: String) : RepositoryDetailUiState
}
