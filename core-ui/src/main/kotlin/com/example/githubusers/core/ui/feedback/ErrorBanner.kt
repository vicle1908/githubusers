package com.example.githubusers.core.ui.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Reusable, dismissible error banner for transient list/search errors.
 *
 * - Provides category-aware copy for SearchError when available
 * - Exposes a Retry action and optional Dismiss control
 * - Auto-resets dismissal when the message key changes
 */
@Composable
fun ErrorBanner(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    dismissible: Boolean = true,
    onDismissed: (() -> Unit)? = null
) {
    val messageKey = remember(message) { message }
    var dismissed by rememberSaveable(messageKey) { mutableStateOf(false) }

    LaunchedEffect(messageKey) {
        // New error arrives -> ensure banner is visible again
        dismissed = false
    }

    if (dismissed) return

    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier =
            modifier
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Error banner"
                }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = messageKey, style = MaterialTheme.typography.bodyMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onRetry) { Text("Retry") }
                if (dismissible) {
                    IconButton(
                        onClick = {
                            dismissed = true
                            onDismissed?.invoke()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Dismiss"
                        )
                    }
                }
            }
        }
    }
}
