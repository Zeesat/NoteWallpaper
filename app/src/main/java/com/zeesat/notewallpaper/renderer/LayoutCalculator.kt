package com.zeesat.notewallpaper.renderer

import android.graphics.PointF
import com.zeesat.notewallpaper.domain.model.BubblePosition

object LayoutCalculator {
    fun calculatePosition(
        position: BubblePosition,
        containerWidth: Int,
        containerHeight: Int,
        contentWidth: Int,
        contentHeight: Int,
        margin: Float = 60f
    ): PointF {
        val x = when (position) {
            BubblePosition.TOP_LEFT, BubblePosition.BOTTOM_LEFT -> margin
            BubblePosition.TOP_RIGHT, BubblePosition.BOTTOM_RIGHT -> containerWidth - contentWidth - margin
            BubblePosition.CENTER -> (containerWidth - contentWidth) / 2f
        }
        val y = when (position) {
            BubblePosition.TOP_LEFT, BubblePosition.TOP_RIGHT -> margin
            BubblePosition.BOTTOM_LEFT, BubblePosition.BOTTOM_RIGHT -> containerHeight - contentHeight - margin
            BubblePosition.CENTER -> (containerHeight - contentHeight) / 2f
        }
        return PointF(x, y)
    }
}
