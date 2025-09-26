package com.example.githubusers.feature.users.shared.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class ColumnCenteredAction(val label: String, val onClick: () -> Unit, val isError: Boolean = false)

@Composable
fun ColumnCenteredMessage(message: String, action: ColumnCenteredAction, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = if (action.isError) MaterialTheme.colorScheme.error else Color.Unspecified
        )
        Button(onClick = action.onClick) {
            Text(action.label)
        }
    }
}

@Composable
fun TextButtonLink(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier,
        content = { Text(label) }
    )
}
