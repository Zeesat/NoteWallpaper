package com.zeesat.notewallpaper.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import com.zeesat.notewallpaper.domain.model.BubblePosition
import com.zeesat.notewallpaper.domain.model.BubbleTemplate
import com.zeesat.notewallpaper.domain.model.Note
import com.zeesat.notewallpaper.util.BitmapUtils

class WallpaperRenderer {
    fun render(
        context: Context,
        background: Bitmap,
        note: Note,
        template: BubbleTemplate,
        position: BubblePosition,
        targetWidth: Int,
        targetHeight: Int
    ): Bitmap {
        val outputBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)

        val fittedBg = BitmapUtils.fitBitmap(background, targetWidth, targetHeight)
        canvas.drawBitmap(fittedBg, 0f, 0f, null)

        if (note.text.isNotBlank()) {
            val paddingHorizontal = 64
            val paddingVertical = 48
            val maxTextWidth = (targetWidth * 0.7f).toInt()

            val textHeight = TextRenderer.measureTextHeight(note.text, maxTextWidth)
            val textPaint = android.text.TextPaint().apply { textSize = 48f }
            val textWidth = textPaint.measureText(note.text).coerceAtMost(maxTextWidth.toFloat()).toInt()

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
                maxWidth = maxTextWidth
            )
        }

        return outputBitmap
    }
}
