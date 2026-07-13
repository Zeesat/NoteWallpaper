package com.zeesat.notewallpaper.domain.model

import android.net.Uri

data class WallpaperProject(
    val imageUri: Uri,
    val note: Note,
    val bubbleTemplate: BubbleTemplate,
    val position: BubblePosition
)
