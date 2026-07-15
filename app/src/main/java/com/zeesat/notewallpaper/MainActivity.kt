package com.zeesat.notewallpaper

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.zeesat.notewallpaper.data.BubbleRepositoryImpl
import com.zeesat.notewallpaper.domain.usecase.GenerateWallpaperUseCase
import com.zeesat.notewallpaper.renderer.WallpaperRenderer
import com.zeesat.notewallpaper.ui.screens.EditorScreen
import com.zeesat.notewallpaper.ui.screens.ExportScreen
import com.zeesat.notewallpaper.ui.screens.HomeScreen
import com.zeesat.notewallpaper.ui.screens.PreviewScreen
import com.zeesat.notewallpaper.ui.theme.NoteWallpaperTheme
import com.zeesat.notewallpaper.viewmodel.EditorViewModel
import com.zeesat.notewallpaper.viewmodel.WallpaperViewModel

sealed interface AppScreen {
    object Home : AppScreen
    object Editor : AppScreen
    object Preview : AppScreen
    object Export : AppScreen
}

private const val PREFS_NAME = "note_wallpaper_prefs"
private const val KEY_LAST_IMAGE_URI = "last_image_uri"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Manual DI/Instantiation for simpler codebase structure (perfect for V1)
        val bubbleRepository = BubbleRepositoryImpl()
        val renderer = WallpaperRenderer()
        val editorViewModel = EditorViewModel(applicationContext, bubbleRepository, renderer)
        val generateWallpaperUseCase = GenerateWallpaperUseCase(applicationContext, renderer)
        val wallpaperViewModel = WallpaperViewModel(generateWallpaperUseCase)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        setContent {
            NoteWallpaperTheme {
                val context = LocalContext.current
                var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

                val previousImageUri: Uri? = remember {
                    prefs.getString(KEY_LAST_IMAGE_URI, null)?.let { Uri.parse(it) }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val modifier = Modifier.padding(innerPadding)

                    when (currentScreen) {
                        is AppScreen.Home -> {
                            HomeScreen(
                                onImageSelected = { uri ->
                                    // Persist read access for images selected through Android's file picker.
                                    runCatching {
                                        contentResolver.takePersistableUriPermission(
                                            uri,
                                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        )
                                    }

                                    // Persist the original (un-edited) image URI.
                                    prefs.edit()
                                        .putString(KEY_LAST_IMAGE_URI, uri.toString())
                                        .apply()
                                    editorViewModel.setImageUri(uri)
                                    currentScreen = AppScreen.Editor
                                },
                                previousImageUri = previousImageUri,
                                modifier = modifier
                            )
                        }
                        is AppScreen.Editor -> {
                            EditorScreen(
                                viewModel = editorViewModel,
                                onBackClick = { currentScreen = AppScreen.Home },
                                onNextClick = {
                                    val project = editorViewModel.toProject()
                                    val cropped = editorViewModel.uiState.value.croppedBitmap
                                    if (project != null && cropped != null) {
                                        wallpaperViewModel.generatePreview(project, cropped)
                                        currentScreen = AppScreen.Preview
                                    }
                                },
                                modifier = modifier
                            )
                        }
                        is AppScreen.Preview -> {
                            val context2 = LocalContext.current
                            PreviewScreen(
                                viewModel = wallpaperViewModel,
                                onBackClick = { currentScreen = AppScreen.Editor },
                                onExportClick = { bitmap ->
                                    wallpaperViewModel.exportWallpaper(context2, bitmap)
                                    currentScreen = AppScreen.Export
                                },
                                modifier = modifier
                            )
                        }
                        is AppScreen.Export -> {
                            ExportScreen(
                                viewModel = wallpaperViewModel,
                                onFinishClick = {
                                    currentScreen = AppScreen.Home
                                },
                                modifier = modifier
                            )
                        }
                    }
                }
            }
        }
    }
}
