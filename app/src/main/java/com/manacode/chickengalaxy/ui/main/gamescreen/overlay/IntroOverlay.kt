package com.manacode.feedthechick.ui.main.gamescreen.overlay

import android.R.attr.maxWidth
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manacode.feedthechick.ui.main.component.GradientOutlinedText
import com.manacode.feedthechick.ui.main.component.SecondaryBackButton
import com.manacode.feedthechick.ui.main.component.StartPrimaryButton

@Composable
fun IntroOverlay(
    onStart: () -> Unit,
) {
    // ----------------------- Colors -----------------------
    val orangeTop = Color(0xffffc847)
    val orangeBot = Color(0xff893c00)
    val panelGrad = Brush.verticalGradient(listOf(orangeTop, orangeBot))
    val cardShape = RoundedCornerShape(26.dp)

    // ----------------------- Overlay -----------------------
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
    ) {
        val titleSize = if (maxWidth < 360.dp) 32.sp else 40.sp
        val bodyLineHeight = if (maxWidth < 360.dp) 20.sp else 22.sp
        val cardWidth = (maxWidth * 0.9f).coerceAtMost(360.dp)

        // ----------------------- Card -----------------------
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(cardWidth)
                .wrapContentHeight()
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { shadowElevation = 18f }
            )
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(cardShape)
                    .background(panelGrad)
                    .padding(vertical = 22.dp, horizontal = 18.dp)
            ) {
                // ----------------------- Content -----------------------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GradientOutlinedText(
                        text = "Feed the chick!",
                        fontSize = titleSize,
                        gradientColors = listOf(Color.White, Color.White)
                    )
                    Text(
                        text = "Drag corn into its beak and don't let the chick eat stones or frogs.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF704117),
                            textAlign = TextAlign.Center,
                            lineHeight = bodyLineHeight
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    StartPrimaryButton(
                        text = "Start",
                        onClick = onStart
                    )
                }
            }
        }
    }
}
