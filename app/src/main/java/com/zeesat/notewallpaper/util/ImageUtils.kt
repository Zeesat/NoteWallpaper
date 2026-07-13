package com.zeesat.notewallpaper.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.InputStream

object ImageUtils {
    fun loadBitmapFromUri(context: Context, uri: Uri, maxWidth: Int, maxHeight: Int): Bitmap? {
        var inputStream: InputStream? = null
        return try {
            inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            var sampleSize = 1
            if (options.outHeight > maxHeight || options.outWidth > maxWidth) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while ((halfHeight / sampleSize) >= maxHeight && (halfWidth / sampleSize) >= maxWidth) {
                    sampleSize *= 2
                }
            }

            inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            BitmapFactory.decodeStream(inputStream, null, decodeOptions)
        } catch (e: Exception) {
            null
        } finally {
            inputStream?.close()
        }
    }
}
