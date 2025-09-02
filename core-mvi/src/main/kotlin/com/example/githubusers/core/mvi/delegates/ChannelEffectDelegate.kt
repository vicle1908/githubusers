package com.example.githubusers.core.mvi.delegates

import com.example.githubusers.core.mvi.base.EffectDelegate
import com.example.githubusers.core.mvi.contracts.ViewEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Concrete implementation of EffectDelegate using Channel.
 * Ensures effects are delivered exactly once and not lost during configuration changes.
 */
class ChannelEffectDelegate<E : ViewEffect> : EffectDelegate<E> {
    private val _effects = Channel<E>(Channel.UNLIMITED)
    override val effects: Flow<E> = _effects.receiveAsFlow()

    override suspend fun sendEffect(effect: E) {
        _effects.send(effect)
    }
}
