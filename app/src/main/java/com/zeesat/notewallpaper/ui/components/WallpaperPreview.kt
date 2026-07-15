package com.zeesat.notewallpaper.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.zeesat.notewallpaper.domain.model.BubblePosition
import com.zeesat.notewallpaper.domain.model.BubbleTemplate

@Composable
fun WallpaperPreview(
    croppedBitmap: Bitmap?,
    imageLoaded: Boolean,
    noteText: String,
    bubbleTemplate: BubbleTemplate?,
    position: BubblePosition,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        if (croppedBitmap != null) {
            Image(
                bitmap = croppedBitmap.asImageBitmap(),
                contentDescription = "Selected Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = when (position) {
                    BubblePosition.TOP_LEFT -> Alignment.TopStart
                    BubblePosition.TOP_RIGHT -> Alignment.TopEnd
                    BubblePosition.CENTER -> Alignment.Center
                    BubblePosition.BOTTOM_LEFT -> Alignment.BottomStart
                    BubblePosition.BOTTOM_RIGHT -> Alignment.BottomEnd
                }
            ) {
                if (noteText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = noteText,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else if (imageLoaded) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "No Image Selected",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
