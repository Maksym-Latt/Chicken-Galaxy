package com.manacode.feedthechick.ui.main.gamescreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.filled.Pause
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.manacode.eggmagnet.audio.rememberAudioController
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

// ----------------------- Composable -----------------------
@Composable
fun GameScreen(
    onExitToMenu: (Int) -> Unit,
    viewModel: GameViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    // ----------------------- Enter Intro Every Time -----------------------
    LaunchedEffect(Unit) {
        viewModel.showIntroOnEnter()
    }

    // ----------------------- Context -----------------------
    val context = LocalContext.current

    // ----------------------- Audio -----------------------
    val audio = rememberAudioController()
    

    // ----------------------- State -----------------------
    val state by viewModel.state.collectAsState()

    // ----------------------- FX State -----------------------
    data class LostFx(val id: Long, val at: Offset)
    val lostFx = remember { mutableStateListOf<LostFx>() }
    var fxCounter by remember { mutableLongStateOf(0L) }

    // ----------------------- Audio + FX events -----------------------
    LaunchedEffect(audio) {
        viewModel.events.collect { event ->
            when (event) {
                GameEvent. -> audio.
                GameEvent. -> audio.playGameLose()
                GameEvent.GameWon -> audio.playGameWin()
                is GameEvent.LostSeedOverflow -> {
                    lostFx += LostFx(id = fxCounter++, at = event.at)
                }
            }
        }
    }

    // ----------------------- Pause on App Background -----------------------
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                val s = viewModel.state.value
                if (!s.showWin && !s.showSettings) {
                    viewModel.pauseAndOpenSettings()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // ----------------------- Back button -----------------------
    BackHandler {
        if (!state.showSettings && !state.showWin) {
            viewModel.pauseAndOpenSettings()
        } else {
        }
    }

    // ----------------------- State -----------------------
    LaunchedEffect(state.chickState) {
        if (state.chickState != ChickState.Idle) {
            delay(1000)
            viewModel.acknowledgeChickIdle()
        }
    }

    // ----------------------- Layout -----------------------
    Surface(color = MaterialTheme.colorScheme.background) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

            val density = LocalDensity.current
            val fieldWidth = maxWidth
            val fieldHeight = maxHeight
            val fieldWidthPx = with(density) { fieldWidth.toPx() }
            val fieldHeightPx = with(density) { fieldHeight.toPx() }

            var mouthBounds by remember { mutableStateOf<Rect?>(null) }

            LaunchedEffect(fieldWidthPx, fieldHeightPx) {
                viewModel.bindSpawner(
                    spawnTick = {
                        val vp = Viewport(
                            widthPx = fieldWidthPx,
                            heightPx = fieldHeightPx,
                            horizontalPaddingPx = with(density) { 24.dp.toPx() },
                            verticalPaddingPx = with(density) { 24.dp.toPx() },
                            verticalLimitRatio = 0.7f
                        )
                        viewModel.spawnTick(vp, density)
                    }
                )
            }

            Image(
                painter = painterResource(id = R.drawable.bg_game),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
    }
}


@Composable
fun Scoreboard(
    score: Int,
    lives: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {

    }
}