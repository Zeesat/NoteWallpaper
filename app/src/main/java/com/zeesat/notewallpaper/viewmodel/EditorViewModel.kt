package com.zeesat.notewallpaper.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.zeesat.notewallpaper.domain.model.BubblePosition
import com.zeesat.notewallpaper.domain.model.BubbleTemplate
import com.zeesat.notewallpaper.domain.model.Note
import com.zeesat.notewallpaper.domain.model.WallpaperProject
import com.zeesat.notewallpaper.domain.repository.BubbleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EditorUiState(
    val imageUri: Uri? = null,
    val noteText: String = "",
    val availableTemplates: List<BubbleTemplate> = emptyList(),
    val selectedTemplate: BubbleTemplate? = null,
    val selectedPosition: BubblePosition = BubblePosition.CENTER,
    val isGenerating: Boolean = false,
    val error: String? = null
)

class EditorViewModel(
    private val bubbleRepository: BubbleRepository
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
        _uiState.update { it.copy(imageUri = uri) }
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
