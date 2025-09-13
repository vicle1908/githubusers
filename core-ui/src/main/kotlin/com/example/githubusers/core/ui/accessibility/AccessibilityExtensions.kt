package com.example.githubusers.core.ui.accessibility

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Accessibility extensions for improving app accessibility across all screens.
 * Provides utilities for screen readers, voice control, and assistive technologies.
 */

/**
 * Standard touch target size following Material Design accessibility guidelines.
 * Ensures minimum 48dp touch targets for better accessibility.
 */
val MinimumTouchTargetSize: Dp = 48.dp

/**
 * Accessibility-optimized modifier for interactive elements.
 * Ensures proper touch target size and semantic information.
 */
fun Modifier.accessibleInteraction(
    contentDescription: String,
    role: Role = Role.Button,
    stateDescription: String? = null,
    testTag: String? = null
): Modifier = this
    .size(MinimumTouchTargetSize)
    .semantics {
        this.contentDescription = contentDescription
        this.role = role
        stateDescription?.let { this.stateDescription = it }
        testTag?.let { this.testTag = it }
    }

/**
 * Enhanced content description that provides context-aware information.
 * Useful for list items and complex UI components.
 */
fun Modifier.contextualContentDescription(
    description: String,
    position: Int? = null,
    totalItems: Int? = null,
    additionalInfo: String? = null
): Modifier = this.semantics {
    val fullDescription = buildString {
        append(description)

        if (position != null && totalItems != null) {
            append(". Item ${position + 1} of $totalItems")
        }

        additionalInfo?.let { append(". $it") }
    }

    contentDescription = fullDescription
}

/**
 * Accessibility-optimized search field with proper announcements.
 */
fun Modifier.accessibleSearchField(query: String, isActive: Boolean, resultCount: Int? = null): Modifier =
    this.semantics {
        val searchState = if (isActive) "Search field active" else "Search field inactive"
        val results = resultCount?.let { ", $it results" } ?: ""

        contentDescription = "Search field. Current query: ${query.ifEmpty { "empty" }}$results"
        stateDescription = searchState
        role = Role.Button // TextField role
    }

/**
 * Creates accessibility-optimized list item semantics.
 */
fun Modifier.accessibleListItem(
    itemContent: String,
    position: Int,
    totalItems: Int,
    hasAction: Boolean = true,
    additionalInfo: String? = null
): Modifier = this
    .contextualContentDescription(
        description = itemContent,
        position = position,
        totalItems = totalItems,
        additionalInfo = additionalInfo
    )
    .semantics {
        role = if (hasAction) Role.Button else Role.Image
    }

/**
 * Collection of accessibility constants and guidelines.
 */
