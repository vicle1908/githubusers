package com.example.githubusers.core.mvi.test

import com.example.githubusers.core.mvi.base.EffectDelegate
import com.example.githubusers.core.mvi.contracts.ViewEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Test implementation of EffectDelegate that tracks all emitted effects.
 * Useful for verifying effects in tests.
 */
class TestEffectDelegate<E : ViewEffect> : EffectDelegate<E> {
    private val _effects = MutableSharedFlow<E>(replay = 100)
    override val effects: Flow<E> = _effects.asSharedFlow()

    val effectHistory = mutableListOf<E>()

    override suspend fun sendEffect(effect: E) {
        effectHistory.add(effect)
        _effects.emit(effect)
    }

    /**
     * Get the last emitted effect
     */
    val lastEffect: E? get() = effectHistory.lastOrNull()

    /**
     * Check if a specific effect was emitted
     */
    fun hasEffect(effect: E): Boolean = effect in effectHistory

    /**
     * Clear effect history
     */
    fun clearHistory() {
        effectHistory.clear()
    }

    /**
     * Get effects of a specific type
     */
    inline fun <reified T : E> getEffectsOfType(): List<T> = effectHistory.filterIsInstance<T>()
}
