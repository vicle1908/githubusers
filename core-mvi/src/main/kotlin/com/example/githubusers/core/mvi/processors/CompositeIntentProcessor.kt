package com.example.githubusers.core.mvi.processors

import com.example.githubusers.core.mvi.contracts.ViewEffect
import com.example.githubusers.core.mvi.contracts.ViewIntent
import com.example.githubusers.core.mvi.contracts.ViewState
import com.example.githubusers.core.mvi.navigation.NavigationDestination

/**
 * Composite intent processor that delegates to multiple processors.
 * Processes intents in order until one handles the intent.
 */
class CompositeIntentProcessor<I : ViewIntent, S : ViewState, E : ViewEffect>(
    private val processors: List<IntentProcessor<I, S, E>>,
) : IntentProcessor<I, S, E> {
    override suspend fun process(
        intent: I,
        currentState: S,
        updateState: (S.() -> S) -> Unit,
        sendEffect: suspend (E) -> Unit,
        navigate: suspend (NavigationDestination) -> Unit,
    ): Boolean {
        for (processor in processors) {
            if (processor.process(intent, currentState, updateState, sendEffect, navigate)) {
                return true
            }
        }
        return false
    }

    companion object {
        /**
         * Create a composite processor from vararg processors
         */
        fun <I : ViewIntent, S : ViewState, E : ViewEffect> of(
            vararg processors: IntentProcessor<I, S, E>,
        ): CompositeIntentProcessor<I, S, E> = CompositeIntentProcessor(processors.toList())
    }
}
