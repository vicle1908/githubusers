package com.example.githubusers.core.mvi.base

import com.example.githubusers.core.mvi.contracts.ViewState
import kotlinx.coroutines.flow.StateFlow

/**
 * Delegation interface for state management in MVI architecture.
 * Provides a clean API for state updates and observation.
 */
interface StateDelegate<S : ViewState> {
    /**
     * Current state as a StateFlow for observation
     */
    val state: StateFlow<S>

    /**
     * Update the state using a reducer function.
     * The reducer receives the current state and returns the new state.
     */
    fun updateState(reducer: S.() -> S)

    /**
     * Get the current state value synchronously
     */
    val currentState: S
        get() = state.value
}
