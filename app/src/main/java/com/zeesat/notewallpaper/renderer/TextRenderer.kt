package com.zeesat.notewallpaper.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint

object TextRenderer {
    fun drawText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        maxWidth: Int,
        textColor: Int = Color.BLACK,
        textSize: Float = 48f,
        typeface: Typeface = Typeface.SANS_SERIF
    ): Int {
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            this.textSize = textSize
            this.typeface = typeface
        }

        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, maxWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .build()

        canvas.save()
        canvas.translate(x, y)
        staticLayout.draw(canvas)
        canvas.restore()

        return staticLayout.height
    }

    fun measureTextHeight(
        text: String,
        maxWidth: Int,
        textSize: Float = 48f,
        typeface: Typeface = Typeface.SANS_SERIF
    ): Int {
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.textSize = textSize
            this.typeface = typeface
        }
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, maxWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .build()
        return staticLayout.height
    }

    fun measureTextWidth(
        text: String,
        textSize: Float = 48f,
        typeface: Typeface = Typeface.SANS_SERIF
    ): Float {
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.textSize = textSize
            this.typeface = typeface
        }
        return textPaint.measureText(text)
    }
}
