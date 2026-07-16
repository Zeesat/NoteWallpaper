package com.zeesat.notewallpaper.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NoteInput(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxCharCount: Int = 500
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text("Note text") },
        placeholder = { Text("Write your reminder or quote...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Notes,
                contentDescription = null
            )
        },
        maxLines = 4,
        shape = MaterialTheme.shapes.medium,
        supportingText = {
            Text(
                text = "${text.length} / $maxCharCount",
                style = MaterialTheme.typography.bodySmall
            )
        }
    )
}
