package com.zeesat.notewallpaper.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import com.zeesat.notewallpaper.domain.model.WallpaperProject
import com.zeesat.notewallpaper.renderer.WallpaperRenderer

class GenerateWallpaperUseCase(
    private val context: Context,
    private val renderer: WallpaperRenderer
) {
    /**
     * Place the bubble on an already-cropped wallpaper bitmap.
     */
    operator fun invoke(project: WallpaperProject, croppedBitmap: Bitmap): Bitmap {
        return renderer.render(
            context = context,
            croppedBackground = croppedBitmap,
            note = project.note,
            template = project.bubbleTemplate,
            position = project.position
        )
    }
}
