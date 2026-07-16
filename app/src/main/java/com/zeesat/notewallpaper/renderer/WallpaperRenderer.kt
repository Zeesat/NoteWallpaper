package com.zeesat.notewallpaper.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import com.zeesat.notewallpaper.domain.model.BubblePosition
import com.zeesat.notewallpaper.domain.model.BubbleTemplate
import com.zeesat.notewallpaper.domain.model.Note
import com.zeesat.notewallpaper.util.BitmapUtils
import com.zeesat.notewallpaper.util.FontManager

class WallpaperRenderer {

    fun cropToWallpaperSize(
        source: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        offsetX: Float = 0.5f,
        offsetY: Float = 0.5f
    ): Bitmap {
        return BitmapUtils.fitBitmap(source, targetWidth, targetHeight, offsetX, offsetY)
    }

    fun render(
        context: Context,
        croppedBackground: Bitmap,
        note: Note,
        template: BubbleTemplate,
        position: BubblePosition
    ): Bitmap {
        val targetWidth = croppedBackground.width
        val targetHeight = croppedBackground.height
        val outputBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)

        canvas.drawBitmap(croppedBackground, 0f, 0f, null)

        if (note.text.isNotBlank()) {
            val fontOption = FontManager.getOptionById(note.fontId)
            val typeface = FontManager.resolveTypeface(context, fontOption)

            val paddingHorizontal = 64
            val paddingVertical = 48
            val maxTextWidth = (targetWidth * 0.7f).toInt()

            val textHeight = TextRenderer.measureTextHeight(note.text, maxTextWidth, typeface = typeface)
            val textWidth = TextRenderer.measureTextWidth(note.text, typeface = typeface)
                .coerceAtMost(maxTextWidth.toFloat()).toInt()

            val bubbleWidth = textWidth + (paddingHorizontal * 2)
            val bubbleHeight = textHeight + (paddingVertical * 2)

            val coordinates = LayoutCalculator.calculatePosition(
                position = position,
                containerWidth = targetWidth,
                containerHeight = targetHeight,
                contentWidth = bubbleWidth,
                contentHeight = bubbleHeight
            )

            val bubbleRect = RectF(
                coordinates.x,
                coordinates.y,
                coordinates.x + bubbleWidth,
                coordinates.y + bubbleHeight
            )
            BubbleRenderer.drawBubble(context, canvas, template, bubbleRect)

            TextRenderer.drawText(
                canvas = canvas,
                text = note.text,
                x = coordinates.x + paddingHorizontal,
                y = coordinates.y + paddingVertical,
                maxWidth = maxTextWidth,
                typeface = typeface
            )
        }

        return outputBitmap
    }
}
