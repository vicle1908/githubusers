package com.example.githubusers.feature.users.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubusers.core.mvi.base.EffectDelegate
import com.example.githubusers.core.mvi.base.StateDelegate
import com.example.githubusers.core.mvi.contracts.ViewEffect
import com.example.githubusers.core.mvi.contracts.ViewIntent
import com.example.githubusers.core.mvi.contracts.ViewState
import com.example.githubusers.core.mvi.delegates.ChannelEffectDelegate
import com.example.githubusers.core.mvi.delegates.MutableStateDelegate
import kotlinx.coroutines.launch

/**
 * Local bridge base class to help KSP/Hilt correctly resolve the ViewModel
 * inheritance chain within this module, while keeping core-mvi as an
 * implementation dependency. This avoids relaxing dependencies to api.
 *
 * Remove once upstream Hilt/KSP multi-module resolution improves.
 */
abstract class FeatureMviViewModel<I : ViewIntent, S : ViewState, E : ViewEffect>(initialState: S) :
    ViewModel(),
    StateDelegate<S> by MutableStateDelegate(initialState),
    EffectDelegate<E> by ChannelEffectDelegate() {
    abstract suspend fun processIntent(intent: I)

    fun onIntent(intent: I) {
        viewModelScope.launch { processIntent(intent) }
    }

    protected suspend fun updateStateWithEffect(stateReducer: S.() -> S, effect: E) {
        updateState(stateReducer)
        sendEffect(effect)
    }
}
