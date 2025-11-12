package com.manacode.chickengalaxy.ui.main.gamescreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.manacode.chickengalaxy.R
import com.manacode.chickengalaxy.audio.rememberAudioController
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedText
import com.manacode.chickengalaxy.ui.main.component.ScoreBadge
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
    val playerState by playerVm.ui.collectAsStateWithLifecycle()
    val audio = rememberAudioController()

    LaunchedEffect(Unit) {
        viewModel.showIntroOnEnter()
    }

    // ----------------------- Pause on App Background -----------------------
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {

                val current = viewModel.state.value
                if (current.phase == GamePhase.Running) {
                    viewModel.pause()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(audio) {
        viewModel.events.collect { event ->
            when (event) {
                GameEvent.PlayerShot -> audio.playShot()
                GameEvent.PlayerHit -> audio.playGetHit()
                GameEvent.EnemyDestroyed -> audio.playExplosion()
                GameEvent.EggCollected -> audio.playCollect()
                is GameEvent.GameOver -> {
                    audio.playWin()
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
                    totalPoints = playerState.points,
                    gainedPoints = result.score + result.bonusEggs * 50,
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

        Box(
            modifier = Modifier
                .fillMaxSize()
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
            Image(
                painter = painterResource(id = R.drawable.bg_game),
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            StarLayer(state.stars)

            val playerSize = toDp(state.playerSize, basePx, density)
            PlayerShip(
                modifier = Modifier
                    .size(playerSize)
                    .offset { toOffset(state.playerX, state.playerY, state.playerSize, widthPx, heightPx, basePx) }
            )

            state.bullets.forEach { bullet ->
                ShotSprite(
                    modifier = Modifier
                        .size(
                            width = toDp(bullet.size * 1.5f, basePx, density),
                            height = toDp(bullet.size * 2f, basePx, density)
                        )
                        .offset { toOffset(bullet.x, bullet.y, bullet.size, widthPx, heightPx, basePx) },
                    resId = R.drawable.our_shot,
                    rotation = 90f
                )
            }

            state.enemyBullets.forEach { bullet ->
                ShotSprite(
                    modifier = Modifier
                        .size(
                            width = toDp(bullet.size * 1.5f, basePx, density),
                            height = toDp(bullet.size * 2f, basePx, density)
                        )
                        .offset { toOffset(bullet.x, bullet.y, bullet.size, widthPx, heightPx, basePx) },
                    resId = R.drawable.enemy_shot,
                    rotation =  270f
                )
            }

            state.eggs.forEach { egg ->
                EggPickup(
                    modifier = Modifier
                        .size(toDp(egg.size * 1.1f, basePx, density))
                        .offset { toOffset(egg.x, egg.y, egg.size, widthPx, heightPx, basePx) }
                )
            }

            state.enemies.forEach { enemy ->
                EnemyShip(
                    modifier = Modifier
                        .size(toDp(enemy.size * 1.6f, basePx, density))
                        .offset { toOffset(enemy.x, enemy.y, enemy.size, widthPx, heightPx, basePx) }
                )
            }

            state.explosions.forEach { explosion ->
                val spriteSize = explosion.size * 2.2f
                ExplosionEffect(
                    modifier = Modifier
                        .size(toDp(spriteSize, basePx, density))
                        .offset { toOffset(explosion.x, explosion.y, spriteSize, widthPx, heightPx, basePx) },
                    progress = explosion.progress
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

private fun toDp(value: Float, basePx: Float, density: Density): Dp = with(density) { (value * basePx).toDp() }

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
    Image(
        painter = painterResource(id = R.drawable.player_ship),
        contentDescription = null,
        modifier = modifier,
        contentScale = androidx.compose.ui.layout.ContentScale.Fit
    )
}

@Composable
private fun EnemyShip(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.enemy),
        contentDescription = null,
        modifier = modifier.graphicsLayer { rotationZ = 180f },
        contentScale = androidx.compose.ui.layout.ContentScale.Fit
    )
}

@Composable
private fun ShotSprite(modifier: Modifier, resId: Int, rotation: Float = 0f) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = modifier.graphicsLayer { rotationZ = rotation },
        contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
    )
}

@Composable
private fun EggPickup(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.egg),
        contentDescription = null,
        modifier = modifier,
        contentScale = androidx.compose.ui.layout.ContentScale.Fit
    )
}

@Composable
private fun ExplosionEffect(modifier: Modifier, progress: Float) {
    val clamped = progress.coerceIn(0f, 1f)
    val scale = 0.6f + (1f - clamped) * 0.6f
    val alpha = 1f - clamped
    Image(
        painter = painterResource(id = R.drawable.blast),
        contentDescription = null,
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        },
        contentScale = androidx.compose.ui.layout.ContentScale.Fit
    )
}
// ----------------------- Scoreboard -----------------------
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
        // ----------------------- Верхній ряд: Пауза + Очки -----------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryIconButton(onClick = onPause) {
                Icon(
                    imageVector = Icons.Filled.Pause,
                    contentDescription = "Pause",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize(0.85f)
                )
            }
            ScoreBadge(points = state.score, widthScale = 1.4f)
        }

        // ----------------------- ПРАВА панель: життя, енергія, чіпи -----------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Spacer(Modifier.weight(1f)) // штовхаємо панель у правий край
            RightStatsPanel(state = state)
        }
    }
}

// ----------------------- Права панель -----------------------
@Composable
private fun RightStatsPanel(
    state: GameUiState,
    panelWidth: Dp = 200.dp
) {
    Column(
        modifier = Modifier.width(panelWidth),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Lives — вирівнюємо вправо через контейнер
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            LivesRow(lives = state.lives, total = 3)
        }

        // Energy — всередині свого контейнера fillMaxWidth() дає ширину панелі
        Box(modifier = Modifier.fillMaxWidth()) {
            EnergyBar(progress = state.energy)
        }

        // Чіпи — щільно справа
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            StatChip(title = "Time",    value = formatTime(state.timeSeconds))
            StatChip(title = "Eggs",    value = state.bonusEggs.toString())
            StatChip(title = "Enemies", value = state.enemiesDown.toString())
        }
    }
}

@Composable
private fun LivesRow(lives: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { index ->
            val filled = index < lives
            HeartIcon(filled = filled)
        }
    }
}

@Composable
private fun HeartIcon(filled: Boolean) {
    Image(
        painter = painterResource(id = R.drawable.heart),
        contentDescription = null,
        modifier = Modifier
            .size(28.dp)
            .graphicsLayer { alpha = if (filled) 1f else 0.35f },
        contentScale = androidx.compose.ui.layout.ContentScale.Fit
    )
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
