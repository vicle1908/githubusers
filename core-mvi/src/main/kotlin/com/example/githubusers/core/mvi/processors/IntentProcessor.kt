package com.example.githubusers.core.mvi.processors

import com.example.githubusers.core.mvi.contracts.ViewEffect
import com.example.githubusers.core.mvi.contracts.ViewIntent
import com.example.githubusers.core.mvi.contracts.ViewState

/**
 * Interface for processing intents in a modular way.
 * Allows composition of intent handling logic.
 */
interface IntentProcessor<I : ViewIntent, S : ViewState, E : ViewEffect> {
    /**
     * Process the given intent.
     *
     * @param intent The intent to process
     * @param currentState Current state
     * @param updateState Function to update state
     * @param sendEffect Function to send effects
     * @param navigateDeepLink Function to navigate
     * @return true if the intent was handled, false otherwise
     */
    suspend fun process(
        intent: I,
        currentState: S,
        updateState: (S.() -> S) -> Unit,
        sendEffect: suspend (E) -> Unit,
        navigateDeepLink: suspend (String) -> Unit
    ): Boolean
}
