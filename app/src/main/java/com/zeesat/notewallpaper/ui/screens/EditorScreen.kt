package com.zeesat.notewallpaper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zeesat.notewallpaper.ui.components.BubbleSelector
import com.zeesat.notewallpaper.ui.components.FontSelector
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Wallpaper") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                val frameAspect = if (uiState.screenWidth > 0 && uiState.screenHeight > 0) {
                    uiState.screenWidth.toFloat() / uiState.screenHeight
                } else {
                    9f / 16f
                }
                val maxPreviewHeight = minOf(maxHeight, 420.dp)
                val previewWidth = minOf(maxWidth, maxPreviewHeight * frameAspect)

                WallpaperPreview(
                    sourceBitmap = uiState.sourceBitmap,
                    imageLoaded = uiState.imageUri != null,
                    noteText = uiState.noteText,
                    fontId = uiState.selectedFontId,
                    position = uiState.selectedPosition,
                    cropCenterX = uiState.cropCenterX,
                    cropCenterY = uiState.cropCenterY,
                    cropScale = uiState.cropScale,
                    screenWidth = uiState.screenWidth,
                    screenHeight = uiState.screenHeight,
                    onCropTransformChanged = { x, y, scale -> viewModel.updateCropTransform(x, y, scale) },
                    modifier = Modifier.width(previewWidth)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Customize",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                NoteInput(
                    text = uiState.noteText,
                    onTextChange = { viewModel.updateNoteText(it) }
                )

                FontSelector(
                    selectedFontId = uiState.selectedFontId,
                    onFontSelect = { viewModel.selectFont(it) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BubbleSelector(
                        templates = uiState.availableTemplates,
                        selectedTemplate = uiState.selectedTemplate,
                        onTemplateSelect = { viewModel.selectTemplate(it) },
                        modifier = Modifier.weight(1f)
                    )

                    PositionSelector(
                        selectedPosition = uiState.selectedPosition,
                        onPositionSelect = { viewModel.selectPosition(it) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onNextClick,
                    enabled = uiState.sourceBitmap != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Generate & Preview",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}
