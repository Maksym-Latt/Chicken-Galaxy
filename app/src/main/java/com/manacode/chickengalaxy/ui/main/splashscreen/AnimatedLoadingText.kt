package com.manacode.eggmagnet.ui.main.splashscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.manacode.eggmagnet.R
import com.manacode.eggmagnet.ui.main.component.GradientOutlinedText
import kotlinx.coroutines.delay

@Composable
fun AnimatedLoadingText(
    modifier: Modifier = Modifier,
    base: String = "LOADING",
    stepMs: Long = 300L
) {
    val order = intArrayOf(1, 2, 3, 0)
    var idx by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(stepMs)
            idx = (idx + 1) % order.size
        }
    }
    val dots = order[idx]

    val display = buildString {
        append(base)
        if (dots > 0) append(" ")
        repeat(dots) { append(".") }
    }

    GradientOutlinedText(
        text = display,
        modifier = modifier,
        fontSize = 44.sp,
        strokeWidth = 10f
    )
}