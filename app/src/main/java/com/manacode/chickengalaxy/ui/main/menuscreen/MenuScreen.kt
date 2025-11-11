package com.manacode.chickengalaxy.ui.main.menuscreen

import android.graphics.Typeface
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.manacode.chickengalaxy.R
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedText
import com.manacode.chickengalaxy.ui.main.component.SecondaryIconButton
import com.manacode.chickengalaxy.ui.main.component.StartPrimaryButton
@Composable
fun MenuScreen(
    onStartGame: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTop: () -> Unit,
    onOpenMagnetShop: () -> Unit,
    playerVm: PlayerViewModel = hiltViewModel()
) {
    val p by playerVm.ui.collectAsStateWithLifecycle()
    MenuScreenUI(
        onStartGame = onStartGame,
        onOpenSettings = onOpenSettings,
        onOpenTop = onOpenTop,
        onOpenMagnetShop = onOpenMagnetShop,
        gameLevel = p.gameLevel
    )
}

@Composable
private fun MenuScreenUI(
    onStartGame: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTop: () -> Unit,
    onOpenMagnetShop: () -> Unit,
    gameLevel: Int
) {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_main_screen),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SecondaryIconButton(onClick = onOpenTop) {
                    Icon(
                        imageVector = Icons.Filled.Leaderboard,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize(0.9f)
                    )
                }
                SecondaryIconButton(
                    iconRes = R.drawable.ic_magnet,
                    onClick = onOpenMagnetShop
                )
            }

            SecondaryIconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp, top = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize(0.9f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GameTitle(modifier = Modifier.padding(bottom = 24.dp))

            StartPrimaryButton(
                text = "START",
                onClick = onStartGame,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(56.dp)
            )

            Spacer(Modifier.height(10.dp))

            LevelLabel(text = "LEVEL $gameLevel")
        }
    }
}

/* --------------------- Components --------------------- */

@Composable
private fun GameTitle(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // логотип — крупнее, но не на всю ширину
        Image(
            painter = painterResource(R.drawable.main_scr_logo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.8f) // ширина ~80% экрана
                .padding(bottom = 16.dp)
        )

        // персонаж чуть меньше, визуально под логотипом
        Image(
            painter = painterResource(R.drawable.main_scr_player),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.6f) // пропорционально меньше
        )
    }
}


@Composable
private fun LevelLabel(text: String) {
    // маленький рельєфний бейдж під кнопкою
    GradientOutlinedText(
        text = text,
        fontSize = 26.sp,
        modifier = Modifier
            .drawBehind {
                // легка тінь під підписом
                drawRect(Color.White.copy(alpha = 0.0f))
            }
    )
}