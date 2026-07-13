package com.zeesat.notewallpaper.ui.screens

import android.app.WallpaperManager
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.zeesat.notewallpaper.viewmodel.ApplyStatus
import com.zeesat.notewallpaper.viewmodel.WallpaperUiState
import com.zeesat.notewallpaper.viewmodel.WallpaperViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    viewModel: WallpaperViewModel,
    onBackClick: () -> Unit,
    onExportClick: (Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val previewState by viewModel.previewState.collectAsState()
    val applyState by viewModel.applyState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Final Wallpaper Preview") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = previewState) {
                is WallpaperUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WallpaperUiState.Success -> {
                    val bitmap = state.bitmap
                    Column(modifier = Modifier.fillMaxSize()) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Composite Preview",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onExportClick(bitmap) },
                                modifier = Modifier.weight(1f).height(56.dp)
                            ) {
                                Text("Export PNG")
                            }
                            Button(
                                onClick = { showDialog = true },
                                modifier = Modifier.weight(1f).height(56.dp)
                            ) {
                                Text("Apply Wallpaper")
                            }
                        }
                    }
                }
                is WallpaperUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                WallpaperUiState.Idle -> {
                    Text(text = "No preview generated", modifier = Modifier.align(Alignment.Center))
                }
            }

            if (showDialog && previewState is WallpaperUiState.Success) {
                val bitmap = (previewState as WallpaperUiState.Success).bitmap
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Apply Wallpaper Target") },
                    text = { Text("Where do you want to apply this wallpaper?") },
                    confirmButton = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    viewModel.applyWallpaper(context, bitmap, WallpaperManager.FLAG_SYSTEM)
                                    showDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Home Screen")
                            }
                            Button(
                                onClick = {
                                    viewModel.applyWallpaper(context, bitmap, WallpaperManager.FLAG_LOCK)
                                    showDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Lock Screen")
                            }
                            Button(
                                onClick = {
                                    viewModel.applyWallpaper(context, bitmap, WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK)
                                    showDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Both Screens")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (applyState is ApplyStatus.Applying) {
                AlertDialog(
                    onDismissRequest = {},
                    confirmButton = {},
                    title = { Text("Applying Wallpaper") },
                    text = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                )
            } else if (applyState is ApplyStatus.Success) {
                LaunchedEffect(applyState) {
                    viewModel.resetStates()
                }
            }
        }
    }
}
