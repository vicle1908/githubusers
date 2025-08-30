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
    
    @PublishedApi
    internal val _effectHistory = mutableListOf<E>()
    val effectHistory: List<E> get() = _effectHistory.toList()
    
    override suspend fun sendEffect(effect: E) {
        _effectHistory.add(effect)
        _effects.emit(effect)
    }
    
    /**
     * Get the last emitted effect
     */
    val lastEffect: E? get() = _effectHistory.lastOrNull()
    
    /**
     * Check if a specific effect was emitted
     */
    fun hasEffect(effect: E): Boolean = effect in _effectHistory
    
    /**
     * Clear effect history
     */
    fun clearHistory() {
        _effectHistory.clear()
    }
    
    /**
     * Get effects of a specific type
     */
    inline fun <reified T : E> getEffectsOfType(): List<T> {
        return _effectHistory.filterIsInstance<T>()
    }
}