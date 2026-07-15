package com.zeesat.notewallpaper.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import com.zeesat.notewallpaper.domain.model.WallpaperProject
import com.zeesat.notewallpaper.renderer.WallpaperRenderer
import com.zeesat.notewallpaper.util.ImageUtils
import com.zeesat.notewallpaper.util.ScreenUtils

class GenerateWallpaperUseCase(
    private val context: Context,
    private val renderer: WallpaperRenderer
) {
    operator fun invoke(project: WallpaperProject): Bitmap? {
        val screenMetrics = ScreenUtils.getScreenSize(context)
        val srcBitmap = ImageUtils.loadBitmapFromUri(context, project.imageUri, screenMetrics.first, screenMetrics.second)
            ?: return null

        // Step 1: Crop to wallpaper resolution first
        val croppedBitmap = renderer.cropToWallpaperSize(
            source = srcBitmap,
            targetWidth = screenMetrics.first,
            targetHeight = screenMetrics.second
        )

        // Step 2: Place bubble on the cropped image
        return renderer.render(
            context = context,
            croppedBackground = croppedBitmap,
            note = project.note,
            template = project.bubbleTemplate,
            position = project.position
        )
    }
}
