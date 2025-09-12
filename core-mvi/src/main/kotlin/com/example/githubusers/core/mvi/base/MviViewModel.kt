package com.example.githubusers.core.mvi.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubusers.core.mvi.contracts.ViewEffect
import com.example.githubusers.core.mvi.contracts.ViewIntent
import com.example.githubusers.core.mvi.contracts.ViewState
import com.example.githubusers.core.mvi.delegates.ChannelEffectDelegate
import com.example.githubusers.core.mvi.delegates.MutableStateDelegate
import kotlinx.coroutines.launch

/**
 * Base ViewModel for MVI architecture using Kotlin delegation.
 * Composes behavior through delegates rather than inheritance.
 *
 * @param I Intent type for user interactions
 * @param S State type for UI state
 * @param E Effect type for one-time side effects
 */
abstract class MviViewModel<I : ViewIntent, S : ViewState, E : ViewEffect>(initialState: S) :
    ViewModel(),
    StateDelegate<S> by MutableStateDelegate(initialState),
    EffectDelegate<E> by ChannelEffectDelegate() {
    /**
     * Process an intent from the UI.
     * This method should handle the intent and update state/effects accordingly.
     */
    abstract suspend fun processIntent(intent: I)

    /**
     * Public method for UI to send intents.
     * Launches intent processing in the ViewModel scope.
     */
    fun onIntent(intent: I) {
        viewModelScope.launch {
            processIntent(intent) // Test hook update
        }
    }

    /**
     * Helper method to update state and send effect in one operation
     */
    protected suspend fun updateStateWithEffect(stateReducer: S.() -> S, effect: E) {
        updateState(stateReducer)
        sendEffect(effect)
    }
}
