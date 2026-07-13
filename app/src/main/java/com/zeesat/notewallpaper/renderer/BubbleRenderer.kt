package com.zeesat.notewallpaper.renderer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.zeesat.notewallpaper.domain.model.BubbleTemplate

object BubbleRenderer {
    fun drawBubble(
        context: Context,
        canvas: Canvas,
        template: BubbleTemplate,
        rect: RectF
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            setShadowLayer(12f, 0f, 6f, Color.argb(60, 0, 0, 0))
        }
        canvas.drawRoundRect(rect, 36f, 36f, paint)

        paint.apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 4f
            clearShadowLayer()
        }
        canvas.drawRoundRect(rect, 36f, 36f, paint)
    }
}
