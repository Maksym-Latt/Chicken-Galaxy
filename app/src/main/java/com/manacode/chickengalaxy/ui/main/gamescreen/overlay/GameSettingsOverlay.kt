package com.manacode.chickengalaxy.ui.main.gamescreen.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manacode.chickengalaxy.ui.main.component.LabeledSlider
import com.manacode.chickengalaxy.ui.main.component.OrangePrimaryButton
import com.manacode.chickengalaxy.ui.main.component.SecondaryIconButton
import com.manacode.chickengalaxy.ui.main.settings.SettingsViewModel

@Composable
fun GameSettingsOverlay(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val shape = RoundedCornerShape(26.dp)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA030414))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onResume() }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.85f)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1E2B5A), Color(0xFF10153A))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Paused",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                LabeledSlider(
                    title = "Music",
                    value = ui.musicVolume,
                    onChange = viewModel::setMusicVolume,
                    modifier = Modifier.fillMaxWidth()
                )
                LabeledSlider(
                    title = "Sound",
                    value = ui.soundVolume,
                    onChange = viewModel::setSoundVolume,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SecondaryIconButton(onClick = onResume) {
                        Icon(Icons.Default.ThumbUp, contentDescription = "Resume", tint = Color.White)
                    }
                    SecondaryIconButton(onClick = onRestart) {
                        Icon(Icons.Default.Replay, contentDescription = "Restart", tint = Color.White)
                    }
                    SecondaryIconButton(onClick = onMenu) {
                        Icon(Icons.Default.Home, contentDescription = "Menu", tint = Color.White)
                    }
                }

                OrangePrimaryButton(
                    text = "Resume",
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
        }
    }
}
