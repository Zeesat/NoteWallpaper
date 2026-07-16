package com.zeesat.notewallpaper.ui.components

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.zeesat.notewallpaper.domain.model.BubblePosition
import com.zeesat.notewallpaper.util.FontManager

@Composable
fun WallpaperPreview(
    sourceBitmap: Bitmap?,
    imageLoaded: Boolean,
    noteText: String,
    fontId: String,
    position: BubblePosition,
    cropCenterX: Float,
    cropCenterY: Float,
    cropScale: Float,
    screenWidth: Int,
    screenHeight: Int,
    onCropTransformChanged: (Float, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val latestCenterX by rememberUpdatedState(cropCenterX)
    val latestCenterY by rememberUpdatedState(cropCenterY)
    val latestScale by rememberUpdatedState(cropScale)
    val latestOnTransformChanged by rememberUpdatedState(onCropTransformChanged)
    val paint = remember { Paint(Paint.FILTER_BITMAP_FLAG) }
    val fontOption = remember(fontId) { FontManager.getOptionById(fontId) }
    val fontFamily = remember(fontOption) { FontManager.getFontFamily(fontOption) }
    val frameAspect = if (screenWidth > 0 && screenHeight > 0) {
        screenWidth.toFloat() / screenHeight
    } else {
        9f / 16f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(frameAspect)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (sourceBitmap != null) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(sourceBitmap, frameAspect) {
                        detectTransformGestures { centroid, pan, zoom, _ ->
                            val frameWidth = size.width.toFloat()
                            val frameHeight = size.height.toFloat()
                            if (frameWidth <= 0f || frameHeight <= 0f) return@detectTransformGestures

                            val currentRect = calculateCropRect(
                                source = sourceBitmap,
                                targetAspect = frameAspect,
                                centerX = latestCenterX,
                                centerY = latestCenterY,
                                scale = latestScale
                            )
                            val nextScale = (latestScale * zoom).coerceIn(1f, 5f)
                            val anchorX = currentRect.left + centroid.x / frameWidth * currentRect.width()
                            val anchorY = currentRect.top + centroid.y / frameHeight * currentRect.height()
                            val nextSize = calculateCropSize(sourceBitmap, frameAspect, nextScale)

                            var nextLeft = anchorX - centroid.x / frameWidth * nextSize.first
                            var nextTop = anchorY - centroid.y / frameHeight * nextSize.second

                            // Dragging the image right/down reveals content left/up, so the crop rect moves opposite.
                            nextLeft -= pan.x * nextSize.first / frameWidth
                            nextTop -= pan.y * nextSize.second / frameHeight

                            val nextCenterX = (nextLeft + nextSize.first / 2f) / sourceBitmap.width
                            val nextCenterY = (nextTop + nextSize.second / 2f) / sourceBitmap.height
                            val clamped = clampCropTransform(
                                source = sourceBitmap,
                                targetAspect = frameAspect,
                                centerX = nextCenterX,
                                centerY = nextCenterY,
                                scale = nextScale
                            )
                            latestOnTransformChanged(clamped.centerX, clamped.centerY, clamped.scale)
                        }
                    }
            ) {
                val cropRect = calculateCropRect(
                    source = sourceBitmap,
                    targetAspect = frameAspect,
                    centerX = cropCenterX,
                    centerY = cropCenterY,
                    scale = cropScale
                )
                val srcRect = Rect(
                    cropRect.left.toInt().coerceIn(0, sourceBitmap.width - 1),
                    cropRect.top.toInt().coerceIn(0, sourceBitmap.height - 1),
                    cropRect.right.toInt().coerceIn(1, sourceBitmap.width),
                    cropRect.bottom.toInt().coerceIn(1, sourceBitmap.height)
                )
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawBitmap(
                        sourceBitmap,
                        srcRect,
                        RectF(0f, 0f, size.width, size.height),
                        paint
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = when (position) {
                    BubblePosition.TOP_LEFT -> Alignment.TopStart
                    BubblePosition.TOP_RIGHT -> Alignment.TopEnd
                    BubblePosition.CENTER -> Alignment.Center
                    BubblePosition.BOTTOM_LEFT -> Alignment.BottomStart
                    BubblePosition.BOTTOM_RIGHT -> Alignment.BottomEnd
                }
            ) {
                if (noteText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.92f))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = noteText,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = fontFamily)
                        )
                    }
                }
            }
        } else if (imageLoaded) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Text(
                text = "No Image Selected",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private data class CropTransform(
    val centerX: Float,
    val centerY: Float,
    val scale: Float
)

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

private fun clampCropTransform(
    source: Bitmap,
    targetAspect: Float,
    centerX: Float,
    centerY: Float,
    scale: Float
): CropTransform {
    val clampedScale = scale.coerceIn(1f, 5f)
    val cropSize = calculateCropSize(source, targetAspect, clampedScale)
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
