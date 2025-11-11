package com.manacode.chickengalaxy.ui.main.gamescreen.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedText
import com.manacode.chickengalaxy.ui.main.component.OrangePrimaryButton
import com.manacode.chickengalaxy.ui.main.component.StartPrimaryButton

@Composable
fun IntroOverlay(
    onStart: () -> Unit,
    onExit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xB3000516)),
        contentAlignment = Alignment.Center
    ) {
        val shape = RoundedCornerShape(28.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF17234D), Color(0xFF0B1030))
                    )
                )
                .padding(vertical = 28.dp, horizontal = 24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                GradientOutlinedText(
                    text = "Mission Briefing",
                    fontSize = 32.sp,
                    gradientColors = listOf(Color.White, Color(0xFFB3E5FC))
                )

                InstructionRow(
                    color = Color(0xFFFFD54F),
                    title = "Swipe to fly",
                    description = "Drag anywhere to steer the chicken ship."
                )
                InstructionRow(
                    color = Color(0xFF80DEEA),
                    title = "Tap to fire",
                    description = "Launch egg lasers to crack alien raiders."
                )
                InstructionRow(
                    color = Color(0xFFFFAB91),
                    title = "Collect bonus eggs",
                    description = "Eggs recharge energy and add extra points."
                )

                Text(
                    text = "Survive as long as you can. Protect the Galactic Farm!",
                    color = Color(0xFFECEFF1),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                StartPrimaryButton(
                    text = "Launch!",
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                OrangePrimaryButton(
                    text = "Back to menu",
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
        }
    }
}

@Composable
private fun InstructionRow(color: Color, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = Color(0xFFE0F2F1),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}
