package com.manacode.chickengalaxy.ui.main.gamescreen.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedText
import com.manacode.chickengalaxy.ui.main.component.OrangePrimaryButton
import com.manacode.chickengalaxy.ui.main.component.StartPrimaryButton
import com.manacode.chickengalaxy.ui.main.gamescreen.GameResult

@Composable
fun WinOverlay(
    result: GameResult,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit,
    onOpenSkins: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC02020A)),
        contentAlignment = Alignment.Center
    ) {
        val shape = RoundedCornerShape(28.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF251444), Color(0xFF0D0824))
                    )
                )
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GradientOutlinedText(
                    text = "Good Job!",
                    fontSize = 36.sp,
                    gradientColors = listOf(Color.White, Color(0xFFFFE082))
                )

                ResultRow(label = "Score", value = result.score.toString())
                ResultRow(label = "Survival", value = formatTime(result.timeSeconds))
                ResultRow(label = "Eggs", value = result.bonusEggs.toString())
                ResultRow(label = "Enemies down", value = result.enemiesDown.toString())

                Spacer(Modifier.height(8.dp))

                StartPrimaryButton(
                    text = "Play Again",
                    onClick = onPlayAgain,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                OrangePrimaryButton(
                    text = "Return to Menu",
                    onClick = onMenu,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                OrangePrimaryButton(
                    text = "Visit Skins",
                    onClick = onOpenSkins,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFFB3C6FF), fontSize = 16.sp)
        Text(text = value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
