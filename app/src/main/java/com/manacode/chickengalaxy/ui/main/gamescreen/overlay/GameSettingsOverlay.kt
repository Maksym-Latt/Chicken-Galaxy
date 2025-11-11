package com.manacode.feedthechick.ui.main.gamescreen.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manacode.feedthechick.ui.main.component.GradientOutlinedText
import com.manacode.feedthechick.ui.main.settings.SettingsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import com.manacode.feedthechick.ui.main.component.LabeledSlider
import com.manacode.feedthechick.ui.main.component.SecondaryBackButton
import com.manacode.feedthechick.ui.main.component.SecondaryIconButton

@Composable
fun GameSettingsOverlay(
    onResume: () -> Unit,
    onRetry: () -> Unit,
    onHome: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val orangeTop = Color(0xffffc847)
    val orangeBot = Color(0xff893c00)
    val panelGrad = Brush.verticalGradient(listOf(orangeTop, orangeBot))
    val cardShape = RoundedCornerShape(26.dp)

    val ui by viewModel.ui.collectAsStateWithLifecycle()

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onResume() }
    ) {
        // ----------------------- BACK BUTTON (новое) -----------------------
        SecondaryBackButton(
            onClick = onResume,
            modifier = Modifier
                .padding(start = 24.dp, top = 24.dp)
                .size(62.dp)
        )

        // ----------------------- Card -----------------------
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(300.dp)
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
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    GradientOutlinedText(
                        text = "Pause",
                        fontSize = 40.sp,
                        gradientColors = listOf(Color.White, Color.White)
                    )

                    Spacer(Modifier.height(16.dp))

                    LabeledSlider(
                        title = "Volume",
                        value = ui.musicVolume,
                        onChange = viewModel::setMusicVolume,
                        modifier = Modifier.fillMaxWidth(0.82f)
                    )

                    Spacer(Modifier.height(10.dp))

                    LabeledSlider(
                        title = "Sound",
                        value = ui.soundVolume,
                        onChange = viewModel::setSoundVolume,
                        modifier = Modifier.fillMaxWidth(0.82f)
                    )

                    Spacer(Modifier.height(52.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        SecondaryIconButton(
                            onClick = onRetry,
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize(0.72f)
                            )
                        }

                        SecondaryIconButton(
                            onClick = onHome,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                tint = Color.White,
                                modifier = Modifier.fillMaxSize(0.72f)
                            )
                        }
                    }
                }
            }
        }
    }
}
