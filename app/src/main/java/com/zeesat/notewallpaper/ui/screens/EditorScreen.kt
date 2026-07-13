package com.zeesat.notewallpaper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zeesat.notewallpaper.ui.components.BubbleSelector
import com.zeesat.notewallpaper.ui.components.NoteInput
import com.zeesat.notewallpaper.ui.components.PositionSelector
import com.zeesat.notewallpaper.ui.components.WallpaperPreview
import com.zeesat.notewallpaper.viewmodel.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Note Wallpaper") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            WallpaperPreview(
                imageUri = uiState.imageUri,
                noteText = uiState.noteText,
                bubbleTemplate = uiState.selectedTemplate,
                position = uiState.selectedPosition
            )

            NoteInput(
                text = uiState.noteText,
                onTextChange = { viewModel.updateNoteText(it) }
            )

            BubbleSelector(
                templates = uiState.availableTemplates,
                selectedTemplate = uiState.selectedTemplate,
                onTemplateSelect = { viewModel.selectTemplate(it) }
            )

            PositionSelector(
                selectedPosition = uiState.selectedPosition,
                onPositionSelect = { viewModel.selectPosition(it) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNextClick,
                enabled = uiState.imageUri != null && uiState.noteText.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Generate & Preview")
            }
        }
    }
}
