package com.zeesat.notewallpaper.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream

object BitmapUtils {
    fun fitBitmap(src: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val output = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val srcRatio = src.width.toFloat() / src.height
        val targetRatio = targetWidth.toFloat() / targetHeight

        val srcRect = if (srcRatio > targetRatio) {
            val newWidth = (src.height * targetRatio).toInt()
            val left = (src.width - newWidth) / 2
            Rect(left, 0, left + newWidth, src.height)
        } else {
            val newHeight = (src.width / targetRatio).toInt()
            val top = (src.height - newHeight) / 2
            Rect(0, top, src.width, top + newHeight)
        }

        canvas.drawBitmap(src, srcRect, Rect(0, 0, targetWidth, targetHeight), android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG))
        return output
    }

    fun saveBitmapToStorage(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/NoteWallpaperMaker")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (imageUri != null) {
            try {
                val outputStream: OutputStream? = resolver.openOutputStream(imageUri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                }
            } catch (e: Exception) {
                resolver.delete(imageUri, null, null)
                return null
            }
        }
        return imageUri
    }
}
