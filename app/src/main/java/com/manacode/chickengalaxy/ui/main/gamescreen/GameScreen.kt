package com.manacode.chickengalaxy.ui.main.gamescreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.consume
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.manacode.chickengalaxy.audio.rememberAudioController
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedText
import com.manacode.chickengalaxy.ui.main.component.SecondaryIconButton
import com.manacode.chickengalaxy.ui.main.gamescreen.overlay.GameSettingsOverlay
import com.manacode.chickengalaxy.ui.main.gamescreen.overlay.IntroOverlay
import com.manacode.chickengalaxy.ui.main.gamescreen.overlay.WinOverlay
import com.manacode.chickengalaxy.ui.main.menuscreen.PlayerViewModel
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun GameScreen(
    onExitToMenu: () -> Unit,
    onOpenSkins: () -> Unit,
    viewModel: GameViewModel = viewModel(),
    playerVm: PlayerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val audio = rememberAudioController()

    LaunchedEffect(Unit) {
        viewModel.showIntroOnEnter()
    }

    LaunchedEffect(audio) {
        viewModel.events.collect { event ->
            when (event) {
                is GameEvent.GameOver -> {
                    audio.playGameLose()
                    playerVm.addGameResult(event.result.score, event.result.bonusEggs)
                }
            }
        }
    }

    BackHandler {
        when (state.phase) {
            GamePhase.Running -> viewModel.pause()
            GamePhase.Paused -> viewModel.resume()
            GamePhase.Result, GamePhase.Intro -> {
                viewModel.exitToMenu()
                onExitToMenu()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GameField(
            state = state,
            onDrag = viewModel::movePlayerBy,
            onTap = viewModel::fire
        )

        Scoreboard(
            state = state,
            onPause = viewModel::pause,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        )

        if (state.phase.isIntro) {
            IntroOverlay(
                onStart = {
                    audio.playGameMusic()
                    viewModel.startRun()
                },
                onExit = {
                    viewModel.exitToMenu()
                    onExitToMenu()
                }
            )
        }

        if (state.phase.isPaused) {
            GameSettingsOverlay(
                onResume = viewModel::resume,
                onRestart = {
                    audio.playGameMusic()
                    viewModel.startRun()
                },
                onMenu = {
                    viewModel.exitToMenu()
                    onExitToMenu()
                }
            )
        }

        if (state.phase.isResult) {
            state.result?.let { result ->
                WinOverlay(
                    result = result,
                    onPlayAgain = {
                        audio.playGameMusic()
                        viewModel.startRun()
                    },
                    onMenu = {
                        viewModel.exitToMenu()
                        onExitToMenu()
                    },
                    onOpenSkins = {
                        viewModel.exitToMenu()
                        onOpenSkins()
                    }
                )
            }
        }
    }
}

@Composable
private fun GameField(
    state: GameUiState,
    onDrag: (Float, Float) -> Unit,
    onTap: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        val basePx = min(widthPx, heightPx)

        val background = remember {
            Brush.verticalGradient(
                0f to Color(0xFF050B27),
                0.5f to Color(0xFF0A163D),
                1f to Color(0xFF050513)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .pointerInput(state.phase) {
                    if (state.phase.isControllable()) {
                        detectDragGestures { change, drag ->
                            change.consume()
                            onDrag(drag.x / widthPx, drag.y / heightPx)
                        }
                    }
                }
                .pointerInput(state.phase) {
                    if (state.phase.isControllable()) {
                        detectTapGestures(onTap = { onTap() })
                    }
                }
        ) {
            StarLayer(state.stars)

            val playerSize = toDp(state.playerSize, basePx, density)
            PlayerShip(
                modifier = Modifier
                    .size(playerSize)
                    .offset { toOffset(state.playerX, state.playerY, state.playerSize, widthPx, heightPx, basePx) }
            )

            state.bullets.forEach { bullet ->
                LaserShot(
                    modifier = Modifier
                        .size(
                            width = toDp(bullet.size * 0.6f, basePx, density),
                            height = toDp(bullet.size * 2.8f, basePx, density)
                        )
                        .offset { toOffset(bullet.x, bullet.y, bullet.size, widthPx, heightPx, basePx) },
                    brush = Brush.verticalGradient(listOf(Color(0xFF81D4FA), Color(0xFF0288D1)))
                )
            }

            state.enemyBullets.forEach { bullet ->
                LaserShot(
                    modifier = Modifier
                        .size(
                            width = toDp(bullet.size * 0.6f, basePx, density),
                            height = toDp(bullet.size * 2.6f, basePx, density)
                        )
                        .offset { toOffset(bullet.x, bullet.y, bullet.size, widthPx, heightPx, basePx) },
                    brush = Brush.verticalGradient(listOf(Color(0xFFFF8A80), Color(0xFFD32F2F)))
                )
            }

            state.eggs.forEach { egg ->
                EggPickup(
                    modifier = Modifier
                        .size(toDp(egg.size, basePx, density))
                        .offset { toOffset(egg.x, egg.y, egg.size, widthPx, heightPx, basePx) }
                )
            }

            state.enemies.forEach { enemy ->
                EnemyShip(
                    modifier = Modifier
                        .size(toDp(enemy.size * 1.4f, basePx, density))
                        .offset { toOffset(enemy.x, enemy.y, enemy.size, widthPx, heightPx, basePx) }
                )
            }
        }
    }
}

private fun toOffset(
    x: Float,
    y: Float,
    size: Float,
    widthPx: Float,
    heightPx: Float,
    basePx: Float
): androidx.compose.ui.unit.IntOffset {
    val px = x * widthPx
    val py = y * heightPx
    val half = (size * basePx) / 2f
    return androidx.compose.ui.unit.IntOffset((px - half).roundToInt(), (py - half).roundToInt())
}

private fun toDp(value: Float, basePx: Float, density: LocalDensity): Dp = with(density) { (value * basePx).toDp() }

@Composable
private fun StarLayer(stars: List<Star>) {
    Canvas(Modifier.fillMaxSize()) {
        stars.forEach { star ->
            drawCircle(
                color = Color.White.copy(alpha = 0.75f),
                radius = size.minDimension * star.size,
                center = Offset(star.x * size.width, star.y * size.height)
            )
        }
    }
}

@Composable
private fun PlayerShip(modifier: Modifier) {
    Canvas(modifier) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2f, height / 2f)
        val bodyWidth = width * 0.45f
        val wingWidth = width * 0.8f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF68E1FF), Color.Transparent)
            ),
            radius = width * 0.55f,
            center = center,
            alpha = 0.35f
        )

        val path = Path().apply {
            moveTo(center.x, height * 0.05f)
            lineTo(center.x + bodyWidth / 2f, height * 0.7f)
            lineTo(center.x, height * 0.95f)
            lineTo(center.x - bodyWidth / 2f, height * 0.7f)
            close()
        }
        drawPath(path, color = Color(0xFFFFF176))

        drawRoundRect(
            color = Color(0xFFFFC107),
            topLeft = Offset((width - wingWidth) / 2f, height * 0.42f),
            size = androidx.compose.ui.geometry.Size(wingWidth, height * 0.22f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(width * 0.18f, width * 0.18f)
        )

        drawCircle(
            color = Color(0xFF0D47A1),
            radius = width * 0.13f,
            center = Offset(center.x, height * 0.48f)
        )
        drawCircle(
            color = Color.White,
            radius = width * 0.07f,
            center = Offset(center.x - width * 0.03f, height * 0.46f)
        )
    }
}

@Composable
private fun EnemyShip(modifier: Modifier) {
    Canvas(modifier) {
        val width = size.width
        val height = size.height
        val base = Path().apply {
            moveTo(width / 2f, 0f)
            lineTo(width, height * 0.6f)
            lineTo(width * 0.78f, height)
            lineTo(width * 0.22f, height)
            lineTo(0f, height * 0.6f)
            close()
        }
        drawPath(base, Brush.verticalGradient(listOf(Color(0xFFEF5350), Color(0xFFB71C1C))))
        drawLine(
            color = Color.White.copy(alpha = 0.4f),
            start = Offset(width * 0.2f, height * 0.62f),
            end = Offset(width * 0.8f, height * 0.62f),
            strokeWidth = width * 0.05f
        )
    }
}

@Composable
private fun LaserShot(modifier: Modifier, brush: Brush) {
    Box(
        modifier = modifier
            .clip(RoundedLaserShape)
            .background(brush)
    )
}

private val RoundedLaserShape = androidx.compose.foundation.shape.RoundedCornerShape(50)

@Composable
private fun EggPickup(modifier: Modifier) {
    Canvas(modifier) {
        drawOval(
            color = Color(0xFFFFF9C4),
            topLeft = Offset.Zero,
            size = size
        )
        drawOval(
            color = Color.White.copy(alpha = 0.6f),
            topLeft = Offset(size.width * 0.2f, size.height * 0.15f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.35f, size.height * 0.4f)
        )
    }
}

@Composable
private fun Scoreboard(
    state: GameUiState,
    onPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Score",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                GradientOutlinedText(
                    text = state.score.toString(),
                    fontSize = 34.sp,
                    gradientColors = listOf(Color.White, Color(0xFFFFF9C4))
                )
            }
            SecondaryIconButton(onClick = onPause) {
                Icon(
                    imageVector = Icons.Filled.Pause,
                    contentDescription = "Pause",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }
        }

        LivesRow(lives = state.lives, total = 3)

        EnergyBar(progress = state.energy)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatChip(title = "Time", value = formatTime(state.timeSeconds))
            StatChip(title = "Eggs", value = state.bonusEggs.toString())
            StatChip(title = "Enemies", value = state.enemiesDown.toString())
        }
    }
}

@Composable
private fun LivesRow(lives: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { index ->
            val filled = index < lives
            EggLifeIcon(filled = filled)
        }
    }
}

@Composable
private fun EggLifeIcon(filled: Boolean) {
    val color = if (filled) Color(0xFFFFF59D) else Color(0x33FFF59D)
    Canvas(Modifier.size(28.dp)) {
        drawOval(color = color, topLeft = Offset.Zero, size = size)
        if (filled) {
            drawOval(
                color = Color.White.copy(alpha = 0.5f),
                topLeft = Offset(size.width * 0.25f, size.height * 0.2f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.35f, size.height * 0.4f)
            )
        }
    }
}

@Composable
private fun EnergyBar(progress: Float) {
    val clamped = progress.coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
            .clip(CircleShape)
            .background(Color(0x3300BCD4))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(clamped)
                .background(Brush.horizontalGradient(listOf(Color(0xFF00BCD4), Color(0xFF80DEEA))))
        )
    }
}

@Composable
private fun StatChip(title: String, value: String) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedChipShape)
            .background(Color(0x33050B27))
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFFB0BEC5),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

private val RoundedChipShape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

private fun GamePhase.isControllable(): Boolean = this == GamePhase.Running
