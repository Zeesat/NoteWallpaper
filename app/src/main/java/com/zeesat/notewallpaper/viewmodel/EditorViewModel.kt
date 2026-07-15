package com.zeesat.notewallpaper.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeesat.notewallpaper.domain.model.BubblePosition
import com.zeesat.notewallpaper.domain.model.BubbleTemplate
import com.zeesat.notewallpaper.domain.model.Note
import com.zeesat.notewallpaper.domain.model.WallpaperProject
import com.zeesat.notewallpaper.domain.repository.BubbleRepository
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
    val sourceBitmap: Bitmap? = null,
    val cropCenterX: Float = 0.5f,
    val cropCenterY: Float = 0.5f,
    val cropScale: Float = 1f,
    val screenWidth: Int = 0,
    val screenHeight: Int = 0,
    val noteText: String = "",
    val availableTemplates: List<BubbleTemplate> = emptyList(),
    val selectedTemplate: BubbleTemplate? = null,
    val selectedPosition: BubblePosition = BubblePosition.CENTER,
    val isGenerating: Boolean = false,
    val error: String? = null
)

class EditorViewModel(
    private val context: Context,
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
        _uiState.update {
            it.copy(
                imageUri = uri,
                sourceBitmap = null,
                cropCenterX = 0.5f,
                cropCenterY = 0.5f,
                cropScale = 1f,
                error = null
            )
        }

        viewModelScope.launch {
            val screenMetrics = ScreenUtils.getScreenSize(context)
            val srcBitmap = withContext(Dispatchers.Default) {
                ImageUtils.loadBitmapFromUri(
                    context, uri, screenMetrics.first * 2, screenMetrics.second * 2
                )
            }
            if (srcBitmap != null) {
                _uiState.update {
                    it.copy(
                        sourceBitmap = srcBitmap,
                        screenWidth = screenMetrics.first,
                        screenHeight = screenMetrics.second,
                        cropCenterX = 0.5f,
                        cropCenterY = 0.5f,
                        cropScale = 1f
                    )
                }
            } else {
                _uiState.update { it.copy(error = "Failed to load image") }
            }
        }
    }

    fun updateCropTransform(centerX: Float, centerY: Float, scale: Float) {
        val state = _uiState.value
        val source = state.sourceBitmap ?: return
        val screenAspect = getScreenAspect(state) ?: return
        val clamped = clampCropTransform(source, screenAspect, centerX, centerY, scale)
        _uiState.update {
            it.copy(
                cropCenterX = clamped.centerX,
                cropCenterY = clamped.centerY,
                cropScale = clamped.scale
            )
        }
    }

    fun createCroppedBitmap(): Bitmap? {
        val state = _uiState.value
        val source = state.sourceBitmap ?: return null
        val screenWidth = state.screenWidth.takeIf { it > 0 } ?: return null
        val screenHeight = state.screenHeight.takeIf { it > 0 } ?: return null
        val cropRect = calculateCropRect(
            source = source,
            targetAspect = screenWidth.toFloat() / screenHeight,
            centerX = state.cropCenterX,
            centerY = state.cropCenterY,
            scale = state.cropScale
        )

        val output = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val sourceRect = Rect(
            cropRect.left.toInt().coerceIn(0, source.width - 1),
            cropRect.top.toInt().coerceIn(0, source.height - 1),
            cropRect.right.toInt().coerceIn(1, source.width),
            cropRect.bottom.toInt().coerceIn(1, source.height)
        )
        canvas.drawBitmap(
            source,
            sourceRect,
            Rect(0, 0, screenWidth, screenHeight),
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
        return output
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

    private data class CropTransform(
        val centerX: Float,
        val centerY: Float,
        val scale: Float
    )

    private fun getScreenAspect(state: EditorUiState): Float? {
        if (state.screenWidth <= 0 || state.screenHeight <= 0) return null
        return state.screenWidth.toFloat() / state.screenHeight
    }

    private fun clampCropTransform(
        source: Bitmap,
        screenAspect: Float,
        centerX: Float,
        centerY: Float,
        scale: Float
    ): CropTransform {
        val clampedScale = scale.coerceIn(1f, 5f)
        val cropSize = calculateCropSize(source, screenAspect, clampedScale)

        val centerXPx = centerX.coerceIn(0f, 1f) * source.width
        val centerYPx = centerY.coerceIn(0f, 1f) * source.height
        val minX = cropSize.first / 2f
        val maxX = source.width - cropSize.first / 2f
        val minY = cropSize.second / 2f
        val maxY = source.height - cropSize.second / 2f

        val clampedXPx = if (minX <= maxX) centerXPx.coerceIn(minX, maxX) else source.width / 2f
        val clampedYPx = if (minY <= maxY) centerYPx.coerceIn(minY, maxY) else source.height / 2f

        return CropTransform(
            centerX = clampedXPx / source.width,
            centerY = clampedYPx / source.height,
            scale = clampedScale
        )
    }

    private fun calculateCropSize(source: Bitmap, targetAspect: Float, scale: Float): Pair<Float, Float> {
        val sourceAspect = source.width.toFloat() / source.height
        val baseWidth: Float
        val baseHeight: Float
        if (sourceAspect > targetAspect) {
            baseHeight = source.height.toFloat()
            baseWidth = baseHeight * targetAspect
        } else {
            baseWidth = source.width.toFloat()
            baseHeight = baseWidth / targetAspect
        }
        return Pair(baseWidth / scale, baseHeight / scale)
    }

    private fun calculateCropRect(
        source: Bitmap,
        targetAspect: Float,
        centerX: Float,
        centerY: Float,
        scale: Float
    ): RectF {
        val clamped = clampCropTransform(source, targetAspect, centerX, centerY, scale)
        val cropSize = calculateCropSize(source, targetAspect, clamped.scale)
        val centerXPx = clamped.centerX * source.width
        val centerYPx = clamped.centerY * source.height
        return RectF(
            centerXPx - cropSize.first / 2f,
            centerYPx - cropSize.second / 2f,
            centerXPx + cropSize.first / 2f,
            centerYPx + cropSize.second / 2f
        )
    }
}
