package com.manacode.eggmagnet.ui.main.splashscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun GradientProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Int = 22
) {
    val clamped = progress.coerceIn(0f, 1f)

    Canvas(
        modifier = modifier
            .height(height.dp)
            .fillMaxWidth()
    ) {
        val radius = size.height / 2f

        drawRoundRect(
            color = Color(0xFF595B60),
            cornerRadius = CornerRadius(radius, radius)
        )

        if (clamped > 0f) {
            val fillW = max(size.height, size.width * clamped)

            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFB74D), Color(0xFFFF8F00))
                ),
                size = Size(fillW, size.height),
                cornerRadius = CornerRadius(radius, radius)
            )

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = size.height * 0.55f
                ),
                size = Size(fillW, size.height),
                cornerRadius = CornerRadius(radius, radius)
            )
        }
    }
}