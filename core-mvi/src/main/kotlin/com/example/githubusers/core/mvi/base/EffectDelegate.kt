package com.example.githubusers.core.mvi.base

import com.example.githubusers.core.mvi.contracts.ViewEffect
import kotlinx.coroutines.flow.Flow

/**
 * Delegation interface for handling side effects in MVI architecture.
 * Effects are one-time events that should be consumed by the UI.
 */
interface EffectDelegate<E : ViewEffect> {
    /**
     * Flow of effects to be observed by the UI
     */
    val effects: Flow<E>
    
    /**
     * Send a new effect to be consumed by the UI
     */
    suspend fun sendEffect(effect: E)
}