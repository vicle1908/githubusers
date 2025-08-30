package com.example.githubusers.navigation.impl

import com.example.githubusers.navigation.api.DeepLinkHandler
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Detector for URI conflicts between different feature modules at build time.
 * Helps prevent runtime issues where multiple handlers claim the same URI pattern.
 */
@Singleton
class UriConflictDetector
    @Inject
    constructor(
        private val handlers: Set<@JvmSuppressWildcards DeepLinkHandler>,
    ) {
        /**
         * Detect URI conflicts between registered handlers.
         *
         * @return List of detected conflicts with details
         */
        fun detectConflicts(): List<UriConflict> {
            val conflicts = mutableListOf<UriConflict>()
            val patternToHandlers = mutableMapOf<String, MutableList<DeepLinkHandler>>()

            // Group patterns by handler
            for (handler in handlers) {
                for (pattern in handler.supportedPatterns()) {
                    val handlersForPattern = patternToHandlers.getOrPut(pattern) { mutableListOf() }
                    handlersForPattern.add(handler)
                }
            }

            // Find patterns with multiple handlers
            for ((pattern, handlerList) in patternToHandlers) {
                if (handlerList.size > 1) {
                    conflicts.add(
                        UriConflict(
                            pattern = pattern,
                            conflictingHandlers = handlerList.map { it.moduleId },
                            message = "Multiple handlers (${handlerList.size}) registered for pattern: $pattern",
                        ),
                    )
                }
            }

            return conflicts
        }

        /**
         * Represents a URI conflict between feature modules.
         */
        data class UriConflict(
            val pattern: String,
            val conflictingHandlers: List<String>,
            val message: String,
        )
    }
