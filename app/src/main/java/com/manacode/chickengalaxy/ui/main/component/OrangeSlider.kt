package com.manacode.feedthechick.ui.main.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
internal fun OrangeSlider(
    value: Int,                 // 0..100
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val clamped = value.coerceIn(0, 100)
    val heightDp = 36.dp
    val radius by remember { mutableStateOf(heightDp / 2) }

    var widthPx by remember { mutableStateOf(1f) }
    val density = LocalDensity.current
    val insetPx = with(density) { 3.dp.toPx() }

    fun xToValue(x: Float): Int {
        val w = (widthPx - insetPx * 2).coerceAtLeast(1f)
        val v = ((x - insetPx).coerceIn(0f, w) / w) * 100f
        return v.roundToInt().coerceIn(0, 100)
    }

    val dragModifier = Modifier
        .pointerInput(Unit) {
            detectTapGestures { offset -> onValueChange(xToValue(offset.x)) }
        }
        .pointerInput(Unit) {
            detectDragGestures(onDrag = { change, _ ->
                onValueChange(xToValue(change.position.x))
            })
        }

    Box(
        modifier = modifier
            .requiredHeight(heightDp)
            .defaultMinSize(minHeight = heightDp)
            .onGloballyPositioned { widthPx = it.size.width.toFloat() }
            .then(dragModifier)
    ) {
        Canvas(
            Modifier
                .matchParentSize()
                .defaultMinSize(minHeight = heightDp)
        ) {
            val trackR = size.height / 2f

            drawRoundRect(
                color = Color(0xFF595B60),
                cornerRadius = CornerRadius(trackR, trackR)
            )

            val trackLeft = insetPx
            val trackTop = insetPx
            val trackWidth = (size.width - insetPx * 2).coerceAtLeast(0f)
            val trackHeight = (size.height - insetPx * 2).coerceAtLeast(0f)
            val fillW = max(trackHeight, trackWidth * (clamped / 100f))

            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFFFB74D), Color(0xFFFF8F00))
                ),
                topLeft = Offset(trackLeft, trackTop),
                size = androidx.compose.ui.geometry.Size(fillW, trackHeight),
                cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f)
            )

            drawRoundRect(
                brush = Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.35f),
                    0.55f to Color.Transparent
                ),
                topLeft = Offset(trackLeft, trackTop),
                size = androidx.compose.ui.geometry.Size(fillW, trackHeight),
                cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f)
            )

            val thumbR = trackHeight * 0.35f
            val thumbMin = trackLeft + trackHeight / 2f
            val thumbMax = trackLeft + trackWidth - trackHeight / 2f
            val thumbCenterX = (trackLeft + fillW - trackHeight / 2f).coerceIn(thumbMin, thumbMax)

            drawCircle(
                color = Color.White,
                radius = thumbR,
                center = Offset(thumbCenterX, size.height / 2f)
            )
        }
    }
}
