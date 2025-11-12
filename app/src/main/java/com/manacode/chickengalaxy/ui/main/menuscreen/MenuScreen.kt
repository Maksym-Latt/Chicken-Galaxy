package com.manacode.chickengalaxy.ui.main.menuscreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manacode.chickengalaxy.R
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedText
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedTextShort
import com.manacode.chickengalaxy.ui.main.component.OrangePrimaryButton
import com.manacode.chickengalaxy.ui.main.component.ScoreBadge
import com.manacode.chickengalaxy.ui.main.component.SecondaryIconButton
import com.manacode.chickengalaxy.ui.main.component.StartPrimaryButton
import com.manacode.chickengalaxy.ui.main.component.formatScoreFixed
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
    val overlay = remember {
        Brush.verticalGradient(
            0f to Color(0x55000000),
            0.6f to Color(0x33000000),
            1f to Color(0xAA050414)
        )
    }

    Surface(color = Color.Transparent) {
        Box(
            modifier = Modifier.fillMaxSize()
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
                    .background(overlay)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TopBar(state.points, onOpenSettings)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GameTitle()
                    Spacer(Modifier.height(12.dp))
                    PlayerPortrait()
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
                        text = "Update",
                        onClick = onOpenSkins,
                        modifier = Modifier.fillMaxWidth(0.65f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                StatsBoard(state, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun TopBar(points: Int, onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SecondaryIconButton(onClick = onOpenSettings) {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.fillMaxSize(0.85f)
            )
        }
        ScoreBadge(points)
    }
}

@Composable
private fun GameTitle() {
    Image(
        painter = painterResource(id = R.drawable.title),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth(0.8f),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
private fun PlayerPortrait() {
    Image(
        painter = painterResource(id = R.drawable.player),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth(0.55f),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
private fun StatsBoard(state: PlayerUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(Color(0x331A237E))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        GradientOutlinedText(
            text = "Pilot Level ${state.playerLevel}",
            fontSize = 18.sp,
        )
    }
}