package com.zeesat.notewallpaper.util

import android.content.Context
import android.os.Build
import android.view.WindowManager

object ScreenUtils {
    fun getScreenSize(context: Context): Pair<Int, Int> {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            val bounds = metrics.bounds
            Pair(bounds.width(), bounds.height())
        } else {
            val display = windowManager.defaultDisplay
            val point = android.graphics.Point()
            display.getRealSize(point)
            Pair(point.x, point.y)
        }
    }
}
