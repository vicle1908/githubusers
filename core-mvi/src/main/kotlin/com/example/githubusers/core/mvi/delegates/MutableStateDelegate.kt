package com.example.githubusers.core.mvi.delegates

import com.example.githubusers.core.mvi.base.StateDelegate
import com.example.githubusers.core.mvi.contracts.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Concrete implementation of StateDelegate using MutableStateFlow.
 * Provides thread-safe state management with Kotlin Flow.
 */
class MutableStateDelegate<S : ViewState>(initialState: S) : StateDelegate<S> {
    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<S> = _state.asStateFlow()

    override fun updateState(reducer: S.() -> S) {
        _state.update(reducer)
    }
}
