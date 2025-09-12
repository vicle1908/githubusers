package com.example.githubusers.core.mvi.test

import com.example.githubusers.core.mvi.base.StateDelegate
import com.example.githubusers.core.mvi.contracts.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Test implementation of StateDelegate that tracks state history.
 * Useful for verifying state transitions in tests.
 */
class TestStateDelegate<S : ViewState>(initialState: S) : StateDelegate<S> {
    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<S> = _state.asStateFlow()

    private val _stateHistory = mutableListOf(initialState)
    val stateHistory: List<S> get() = _stateHistory.toList()

    override fun updateState(reducer: S.() -> S) {
        val newState = _state.value.reducer()
        _state.value = newState
        _stateHistory.add(newState)
    }

    /**
     * Reset the state history for clean test runs
     */
    fun resetHistory() {
        _stateHistory.clear()
        _stateHistory.add(_state.value)
    }

    /**
     * Get the number of state updates
     */
    val updateCount: Int get() = _stateHistory.size - 1
}
