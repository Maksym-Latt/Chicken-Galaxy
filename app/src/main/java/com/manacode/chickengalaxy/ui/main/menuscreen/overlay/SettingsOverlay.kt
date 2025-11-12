package com.manacode.chickengalaxy.ui.main.menuscreen.overlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.manacode.chickengalaxy.ui.main.component.OrangePrimaryButton
import kotlin.math.roundToInt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedText
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedTextShort
import com.manacode.chickengalaxy.ui.main.component.LabeledSlider
import com.manacode.chickengalaxy.ui.main.component.SecondaryBackButton
import com.manacode.chickengalaxy.ui.main.settings.SettingsViewModel
import kotlin.math.max

@Composable
fun SettingsOverlay(
    onClose: () -> Unit,
    onPrivacy: () -> Unit = {},
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
            ) { onClose() }
    ) {
        SecondaryBackButton(
            onClick = onClose,
            modifier = Modifier
                .padding(start = 24.dp, top = 42.dp)
        )

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
                GradientOutlinedText(
                    text = "Paused",
                    fontSize = 38.sp,
                    gradientColors = listOf(Color.White, Color.White)
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
                    modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OrangePrimaryButton(
                        text = "Privacy",
                        onClick = onPrivacy,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )
                }
            }
        }
    }
}