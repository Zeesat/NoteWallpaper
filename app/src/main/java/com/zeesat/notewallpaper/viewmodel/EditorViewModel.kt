package com.zeesat.notewallpaper.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeesat.notewallpaper.domain.model.BubblePosition
import com.zeesat.notewallpaper.domain.model.BubbleTemplate
import com.zeesat.notewallpaper.domain.model.Note
import com.zeesat.notewallpaper.domain.model.WallpaperProject
import com.zeesat.notewallpaper.domain.repository.BubbleRepository
import com.zeesat.notewallpaper.renderer.WallpaperRenderer
import com.zeesat.notewallpaper.util.ImageUtils
import com.zeesat.notewallpaper.util.ScreenUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class EditorUiState(
    val imageUri: Uri? = null,
    val croppedBitmap: Bitmap? = null,
    val noteText: String = "",
    val availableTemplates: List<BubbleTemplate> = emptyList(),
    val selectedTemplate: BubbleTemplate? = null,
    val selectedPosition: BubblePosition = BubblePosition.CENTER,
    val isGenerating: Boolean = false,
    val error: String? = null
)

class EditorViewModel(
    private val context: Context,
    private val bubbleRepository: BubbleRepository,
    private val renderer: WallpaperRenderer
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        val templates = bubbleRepository.getTemplates()
        _uiState.update { it.copy(
            availableTemplates = templates,
            selectedTemplate = templates.firstOrNull()
        ) }
    }

    fun setImageUri(uri: Uri) {
        _uiState.update { it.copy(imageUri = uri, croppedBitmap = null) }

        // Immediately crop to wallpaper resolution
        viewModelScope.launch {
            val screenMetrics = ScreenUtils.getScreenSize(context)
            val cropped = withContext(Dispatchers.Default) {
                val srcBitmap = ImageUtils.loadBitmapFromUri(
                    context, uri, screenMetrics.first, screenMetrics.second
                ) ?: return@withContext null
                renderer.cropToWallpaperSize(srcBitmap, screenMetrics.first, screenMetrics.second)
            }
            if (cropped != null) {
                _uiState.update { it.copy(croppedBitmap = cropped) }
            }
        }
    }

    fun updateNoteText(text: String) {
        if (text.length <= 500) {
            _uiState.update { it.copy(noteText = text, error = null) }
        } else {
            _uiState.update { it.copy(error = "Note text exceeds 500 characters") }
        }
    }

    fun selectTemplate(template: BubbleTemplate) {
        _uiState.update { it.copy(selectedTemplate = template) }
    }

    fun selectPosition(position: BubblePosition) {
        _uiState.update { it.copy(selectedPosition = position) }
    }

    fun toProject(): WallpaperProject? {
        val state = _uiState.value
        val uri = state.imageUri ?: return null
        val template = state.selectedTemplate ?: return null
        return WallpaperProject(
            imageUri = uri,
            note = Note(state.noteText),
            bubbleTemplate = template,
            position = state.selectedPosition
        )
    }
}
