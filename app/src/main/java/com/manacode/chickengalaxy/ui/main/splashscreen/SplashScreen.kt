package com.manacode.chickengalaxy.ui.main.splashscreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.manacode.chickengalaxy.R

@Composable
internal fun SplashScreen(progress: Float) {
    val gradient = Brush.verticalGradient(
        listOf(Color(0x55000000), Color(0xAA050414))
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_main),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(gradient)
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension * 0.35f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF6A1B9A).copy(alpha = 0.4f), Color.Transparent)
                ),
                radius = radius,
                center = Offset(size.width / 2f, size.height / 2f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.title),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.7f),
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = Modifier.height(24.dp))
            AnimatedLoadingText(modifier = Modifier.padding(bottom = 24.dp))
            GradientProgressBar(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
