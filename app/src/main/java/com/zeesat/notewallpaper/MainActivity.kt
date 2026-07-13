package com.zeesat.notewallpaper

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Manual DI/Instantiation for simpler codebase structure (perfect for V1)
        val bubbleRepository = BubbleRepositoryImpl()
        val editorViewModel = EditorViewModel(bubbleRepository)

        val renderer = WallpaperRenderer()
        val generateWallpaperUseCase = GenerateWallpaperUseCase(applicationContext, renderer)
        val wallpaperViewModel = WallpaperViewModel(generateWallpaperUseCase)

        setContent {
            NoteWallpaperTheme {
                var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val modifier = Modifier.padding(innerPadding)

                    when (currentScreen) {
                        is AppScreen.Home -> {
                            HomeScreen(
                                onImageSelected = { uri ->
                                    editorViewModel.setImageUri(uri)
                                    currentScreen = AppScreen.Editor
                                },
                                modifier = modifier
                            )
                        }
                        is AppScreen.Editor -> {
                            EditorScreen(
                                viewModel = editorViewModel,
                                onBackClick = { currentScreen = AppScreen.Home },
                                onNextClick = {
                                    val project = editorViewModel.toProject()
                                    if (project != null) {
                                        wallpaperViewModel.generatePreview(project)
                                        currentScreen = AppScreen.Preview
                                    }
                                },
                                modifier = modifier
                            )
                        }
                        is AppScreen.Preview -> {
                            val context = LocalContext.current
                            PreviewScreen(
                                viewModel = wallpaperViewModel,
                                onBackClick = { currentScreen = AppScreen.Editor },
                                onExportClick = { bitmap ->
                                    wallpaperViewModel.exportWallpaper(context, bitmap)
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
