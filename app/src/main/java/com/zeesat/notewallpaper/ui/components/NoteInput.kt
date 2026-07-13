package com.zeesat.notewallpaper.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NoteInput(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxCharCount: Int = 500
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Write your reminder or quote...") },
            maxLines = 5,
            supportingText = {
                Text(
                    text = "${text.length} / $maxCharCount",
                    modifier = Modifier.align(Alignment.End),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        )
    }
}
