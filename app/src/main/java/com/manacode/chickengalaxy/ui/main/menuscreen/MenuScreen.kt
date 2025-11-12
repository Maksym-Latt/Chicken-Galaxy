package com.manacode.chickengalaxy.ui.main.menuscreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedText
import com.manacode.chickengalaxy.ui.main.component.OrangePrimaryButton
import com.manacode.chickengalaxy.ui.main.component.SecondaryIconButton
import com.manacode.chickengalaxy.ui.main.component.StartPrimaryButton
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun MenuScreen(
    onStartGame: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenSkins: () -> Unit,
    playerVm: PlayerViewModel = hiltViewModel()
) {
    val state by playerVm.ui.collectAsStateWithLifecycle()
    MenuContent(
        state = state,
        onStartGame = onStartGame,
        onOpenSettings = onOpenSettings,
        onOpenLeaderboard = onOpenLeaderboard,
        onOpenSkins = onOpenSkins
    )
}

@Composable
private fun MenuContent(
    state: PlayerUiState,
    onStartGame: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenSkins: () -> Unit
) {
    val gradient = remember {
        Brush.verticalGradient(
            0f to Color(0xFF1B1F5C),
            0.45f to Color(0xFF22114F),
            1f to Color(0xFF08051E)
        )
    }
    val stars = remember { generateStars(seed = 42, count = 90) }

    Surface(color = Color.Transparent) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            StarLayer(stars)

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TopBar(state.points, onOpenLeaderboard)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ChickenLogo(state.palette)
                    Spacer(Modifier.height(24.dp))
                    HeroShipPreview(state.palette)
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StartPrimaryButton(
                        text = "Start",
                        onClick = onStartGame,
                        modifier = Modifier.fillMaxWidth(0.75f)
                    )
                    OrangePrimaryButton(
                        text = "Skins",
                        onClick = onOpenSkins,
                        modifier = Modifier.fillMaxWidth(0.75f)
                    )
                    OrangePrimaryButton(
                        text = "Settings",
                        onClick = onOpenSettings,
                        modifier = Modifier.fillMaxWidth(0.75f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                StatsBoard(state, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun TopBar(points: Int, onLeaderboard: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SecondaryIconButton(onClick = onLeaderboard) {
            Icon(
                imageVector = Icons.Filled.Leaderboard,
                contentDescription = "Leaderboard",
                tint = Color.White,
                modifier = Modifier.fillMaxSize(0.85f)
            )
        }
        PointsBadge(points)
    }
}

@Composable
private fun PointsBadge(points: Int) {
    val formatted = remember(points) { points.thousands() }
    val shape = CircleShape
    Box(
        modifier = Modifier
            .wrapContentSize()
            .shadow(24.dp, shape, spotColor = Color(0x40000000), clip = false)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFFFF59D), Color(0xFFFFB300))
                )
            )
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "$formatted pts",
            color = Color(0xFF301E00),
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun ChickenLogo(palette: com.manacode.chickengalaxy.data.player.SkinPalette) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GradientOutlinedText(
            text = "CHICKEN",
            fontSize = 42.sp,
            gradientColors = listOf(Color.White, Color(0xFFE3F2FD))
        )
        GradientOutlinedText(
            text = "GALAXY",
            fontSize = 42.sp,
            gradientColors = listOf(Color(0xFFFFF59D), Color(0xFFFFB74D))
        )
    }
}

@Composable
private fun HeroShipPreview(palette: com.manacode.chickengalaxy.data.player.SkinPalette) {
    val bodyColor = palette.primary
    val accent = palette.accent
    val glow = Brush.radialGradient(
        listOf(accent.copy(alpha = 0.35f), Color.Transparent)
    )
    Box(
        modifier = Modifier
            .size(220.dp)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2.2f
            drawCircle(brush = glow, center = center, radius = radius)
        }
        Canvas(
            Modifier
                .size(180.dp)
        ) {
            val width = size.width
            val height = size.height
            val noseWidth = width * 0.14f
            val bodyWidth = width * 0.4f
            val bodyHeight = height * 0.6f
            val wingWidth = width * 0.7f

            withTransform({ rotate(degrees = -3f, pivot = Offset(width / 2f, height / 2f)) }) {
                drawRoundRect(
                    color = bodyColor,
                    topLeft = Offset((width - bodyWidth) / 2f, height * 0.25f),
                    size = androidx.compose.ui.geometry.Size(bodyWidth, bodyHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(bodyWidth / 2f, bodyWidth / 1.4f)
                )
                drawRoundRect(
                    color = accent,
                    topLeft = Offset(width / 2f - noseWidth / 2f, height * 0.12f),
                    size = androidx.compose.ui.geometry.Size(noseWidth, height * 0.25f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(noseWidth / 2f, noseWidth)
                )
                drawRoundRect(
                    color = accent.copy(alpha = 0.8f),
                    topLeft = Offset((width - wingWidth) / 2f, height * 0.45f),
                    size = androidx.compose.ui.geometry.Size(wingWidth, height * 0.18f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(wingWidth / 2f, height * 0.18f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = width * 0.1f,
                    center = Offset(width / 2f, height * 0.45f)
                )
            }
        }
    }
}

@Composable
private fun StatsBoard(state: PlayerUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(Color(0x331A237E))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Pilot Level ${state.playerLevel}",
            color = Color(0xFFE8EAF6),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        StatRow(
            title = "Egg Blaster",
            level = state.eggBlasterLevel,
            bonus = state.eggBlasterBonus,
            description = state.eggBlasterDescription,
            nextCost = state.eggBlasterNextCost
        )
        Spacer(Modifier.height(12.dp))
        StatRow(
            title = "Feather Shield",
            level = state.featherShieldLevel,
            bonus = state.featherShieldBonus,
            description = state.featherShieldDescription,
            nextCost = state.featherShieldNextCost
        )
    }
}

@Composable
private fun StatRow(
    title: String,
    level: Int,
    bonus: String,
    description: String,
    nextCost: Int?
) {
    Column {
        Text(
            text = "$title · Lv.$level",
            color = Color(0xFFE1F5FE),
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
        Text(
            text = bonus,
            color = Color(0xFFFFF176),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = description,
            color = Color(0xFFB3C6FF),
            fontSize = 13.sp
        )
        nextCost?.let {
            Text(
                text = "Next upgrade: ${it.thousands()} pts",
                color = Color(0xFF90CAF9),
                fontSize = 12.sp
            )
        } ?: Text(
            text = "Maxed out",
            color = Color(0xFF80CBC4),
            fontSize = 12.sp
        )
    }
}

private data class StarPoint(
    val x: Float,
    val y: Float,
    val radius: Float,
    val phase: Float
)

private fun generateStars(seed: Int, count: Int): List<StarPoint> {
    val random = Random(seed)
    return List(count) {
        StarPoint(
            x = random.nextFloat(),
            y = random.nextFloat(),
            radius = random.nextFloat().coerceIn(0.002f, 0.01f),
            phase = random.nextFloat()
        )
    }
}

@Composable
private fun StarLayer(stars: List<StarPoint>) {
    val infinite = rememberInfiniteTransition(label = "stars")
    val pulse by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    Canvas(Modifier) {
        stars.forEach { star ->
            val x = star.x * size.width
            val y = star.y * size.height
            val base = size.minDimension * star.radius
            val scale = 0.6f + 0.4f * kotlin.math.sin((pulse + star.phase) * 6.2831f)
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = base * scale,
                center = Offset(x, y)
            )
        }
    }
}

private fun Int.thousands(): String {
    val value = this
    val absValue = kotlin.math.abs(value)
    val formatted = when {
        absValue >= 1_000_000 -> "${(value / 1_000_000f).roundToOne()}M"
        absValue >= 1_000 -> "${(value / 1_000f).roundToOne()}K"
        else -> value.toString()
    }
    return formatted
}

private fun Float.roundToOne(): String {
    val rounded = (this * 10).roundToInt() / 10f
    return if (rounded % 1f == 0f) rounded.toInt().toString() else rounded.toString()
}
