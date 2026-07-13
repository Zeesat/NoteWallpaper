package com.zeesat.notewallpaper.viewmodel

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeesat.notewallpaper.domain.model.WallpaperProject
import com.zeesat.notewallpaper.domain.usecase.GenerateWallpaperUseCase
import com.zeesat.notewallpaper.util.BitmapUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface WallpaperUiState {
    object Idle : WallpaperUiState
    object Loading : WallpaperUiState
    data class Success(val bitmap: Bitmap) : WallpaperUiState
    data class Error(val message: String) : WallpaperUiState
}

sealed interface ApplyStatus {
    object Idle : ApplyStatus
    object Applying : ApplyStatus
    object Success : ApplyStatus
    data class Error(val message: String) : ApplyStatus
}

sealed interface ExportStatus {
    object Idle : ExportStatus
    object Exporting : ExportStatus
    data class Success(val uri: Uri) : ExportStatus
    data class Error(val message: String) : ExportStatus
}

class WallpaperViewModel(
    private val generateWallpaperUseCase: GenerateWallpaperUseCase
) : ViewModel() {

    private val _previewState = MutableStateFlow<WallpaperUiState>(WallpaperUiState.Idle)
    val previewState: StateFlow<WallpaperUiState> = _previewState.asStateFlow()

    private val _applyState = MutableStateFlow<ApplyStatus>(ApplyStatus.Idle)
    val applyState: StateFlow<ApplyStatus> = _applyState.asStateFlow()

    private val _exportState = MutableStateFlow<ExportStatus>(ExportStatus.Idle)
    val exportState: StateFlow<ExportStatus> = _exportState.asStateFlow()

    fun generatePreview(project: WallpaperProject) {
        viewModelScope.launch {
            _previewState.value = WallpaperUiState.Loading
            val bitmap = withContext(Dispatchers.Default) {
                generateWallpaperUseCase(project)
            }
            if (bitmap != null) {
                _previewState.value = WallpaperUiState.Success(bitmap)
            } else {
                _previewState.value = WallpaperUiState.Error("Failed to render wallpaper bitmap")
            }
        }
    }

    fun applyWallpaper(context: Context, bitmap: Bitmap, flag: Int) {
        viewModelScope.launch {
            _applyState.value = ApplyStatus.Applying
            val success = withContext(Dispatchers.IO) {
                try {
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap, null, true, flag)
                    } else {
                        wallpaperManager.setBitmap(bitmap)
                    }
                    true
                } catch (e: Exception) {
                    false
                }
            }
            if (success) {
                _applyState.value = ApplyStatus.Success
            } else {
                _applyState.value = ApplyStatus.Error("Failed to apply wallpaper")
            }
        }
    }

    fun exportWallpaper(context: Context, bitmap: Bitmap) {
        viewModelScope.launch {
            _exportState.value = ExportStatus.Exporting
            val uri = withContext(Dispatchers.IO) {
                val fileName = "NoteWallpaper_${System.currentTimeMillis()}"
                BitmapUtils.saveBitmapToStorage(context, bitmap, fileName)
            }
            if (uri != null) {
                _exportState.value = ExportStatus.Success(uri)
            } else {
                _exportState.value = ExportStatus.Error("Failed to save wallpaper image to storage")
            }
        }
    }

    fun resetStates() {
        _applyState.value = ApplyStatus.Idle
        _exportState.value = ExportStatus.Idle
    }
}
