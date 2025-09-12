package com.example.githubusers.core.ui.performance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.sample

/**
 * Battery-optimized extensions for reducing unnecessary operations and improving efficiency.
 * These utilities help reduce CPU usage, network calls, and memory allocations.
 */

/**
 * Battery-optimized flow collection that only collects when the lifecycle is STARTED.
 * This prevents unnecessary background processing when the app is not visible.
 */
@Composable
fun <T> Flow<T>.collectAsEffectWithLifecycle(key1: Any? = null, action: suspend CoroutineScope.(value: T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentAction by rememberUpdatedState(action)

    LaunchedEffect(this, lifecycleOwner, key1) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@collectAsEffectWithLifecycle.collectLatest { value ->
                currentAction(value)
            }
        }
    }
}

/**
 * Battery-optimized flow that reduces emission frequency for better battery life.
 * Useful for UI updates that don't need to be real-time.
 */
fun <T> Flow<T>.batteryOptimized(samplePeriodMillis: Long = 100L): Flow<T> = this.distinctUntilChanged()
    .sample(samplePeriodMillis)

/**
 * Extension for performing expensive operations only when necessary.
 * Includes automatic debouncing to prevent excessive calls.
 */
inline fun <T> T.withBatteryOptimization(key: Any? = null, noinline operation: T.() -> Unit) {
    // Simple key-based deduplication to prevent redundant operations
    val operationKey = "${this?.javaClass?.simpleName}_${key}_${operation.hashCode()}"
    BatteryOptimizationManager.executeIfNotRecent(operationKey) {
        operation()
    }
}

/**
 * Manager for battery optimization strategies.
 */
object BatteryOptimizationManager {
    private val recentOperations = mutableMapOf<String, Long>()
    private const val OPERATION_COOLDOWN_MS = 1000L // 1 second cooldown

    fun executeIfNotRecent(key: String, operation: () -> Unit) {
        val now = System.currentTimeMillis()
        val lastExecution = recentOperations[key] ?: 0L

        if (now - lastExecution > OPERATION_COOLDOWN_MS) {
            recentOperations[key] = now
            operation()

            // Clean up old entries to prevent memory leaks
            if (recentOperations.size > 100) {
                val cutoff = now - (OPERATION_COOLDOWN_MS * 10)
                recentOperations.entries.removeAll { it.value < cutoff }
            }
        }
    }
}
