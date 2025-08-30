package com.example.githubusers.navigation.impl.guard

/** Read-only navigation gate used as a kill switch for navigate/pop. */
class ReadOnlyNavGate(
    private val enabled: Boolean = false,
) {
    fun allowMutation(): Boolean = !enabled
}
