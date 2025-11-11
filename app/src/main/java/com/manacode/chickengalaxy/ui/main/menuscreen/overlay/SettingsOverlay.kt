package com.manacode.eggmagnet.ui.main.menuscreen.overlay

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
import com.manacode.eggmagnet.ui.main.component.OrangePrimaryButton
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
import com.manacode.eggmagnet.ui.main.component.GradientOutlinedText
import com.manacode.eggmagnet.ui.main.component.GradientOutlinedTextShort
import com.manacode.eggmagnet.ui.main.component.SecondaryBackButton
import com.manacode.eggmagnet.ui.main.settings.SettingsViewModel
import kotlin.math.max

@Composable
fun SettingsOverlay(
    onClose: () -> Unit,
    onPrivacy: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val cardShape = RoundedCornerShape(18.dp)

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClose() }
    ) {
        SecondaryBackButton(
            onClick = onClose,
            modifier = Modifier
                .padding(start = 16.dp, top = 24.dp)

        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(300.dp)
                .wrapContentHeight()
                .clip(cardShape)
                .background(Color(0xFF3DE3F8))
                .border(2.dp, Color(0xFF101010), cardShape)
                .padding(vertical = 20.dp, horizontal = 16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GradientOutlinedText(
                    text = "Settings",
                    fontSize = 28.sp,
                    gradientColors = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF)),
                )
                Spacer(Modifier.height(12.dp))

                LabeledSlider(
                    title = "Volume",
                    value = ui.musicVolume,
                    onChange = viewModel::setMusicVolume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Spacer(Modifier.height(8.dp))

                LabeledSlider(
                    title = "Sound",
                    value = ui.soundVolume,
                    onChange = viewModel::setSoundVolume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                Spacer(Modifier.height(30.dp))

                OrangePrimaryButton(
                    text = "Privacy",
                    onClick = onPrivacy,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }
        }
    }
}

/* ---------- Слайдер с заголовком ---------- */
@Composable
public fun LabeledSlider(
    title: String,
    value: Int,                 // 0..100
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GradientOutlinedTextShort(
                text = title,
                fontSize = 18.sp,
                gradientColors = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF)),
            )
            GradientOutlinedText(
                text = "${value}%",
                fontSize = 18.sp,
                gradientColors = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF)),
            )
        }
        Spacer(Modifier.height(6.dp))

        OrangeSlider(
            value = value,
            onValueChange = onChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
        )
    }
}

@Composable
private fun OrangeSlider(
    value: Int,                 // 0..100
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val clamped = value.coerceIn(0, 100)
    val heightDp = 22.dp
    val radius by remember { mutableStateOf(heightDp / 2) }

    // жесты: тап + перетягивание
    var widthPx by remember { mutableStateOf(1f) }
    val density = LocalDensity.current

    fun xToValue(x: Float): Int {
        val padPx = with(density) { 0.dp.toPx() }
        val w = (widthPx - padPx * 2).coerceAtLeast(1f)
        val v = ((x - padPx).coerceIn(0f, w) / w) * 100f
        return v.roundToInt().coerceIn(0, 100)
    }

    val dragModifier = Modifier
        .pointerInput(Unit) {
            detectTapGestures { offset ->
                onValueChange(xToValue(offset.x))
            }
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDrag = { change, _ ->
                    onValueChange(xToValue(change.position.x))
                }
            )
        }

    Box(
        modifier = modifier
            .onGloballyPositioned { widthPx = it.size.width.toFloat() }
            .then(dragModifier)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.height / 2f

            drawRoundRect(
                color = Color(0xFF595B60),
                cornerRadius = CornerRadius(r, r)
            )

            val fillW = max(size.height, size.width * (clamped / 100f))

            drawRoundRect(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFFFB74D), Color(0xFFFF8F00))
                ),
                size = Size(fillW, size.height),
                cornerRadius = CornerRadius(r, r)
            )

            drawRoundRect(
                brush = Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.35f),
                    0.55f to Color.Transparent
                ),
                size = Size(fillW, size.height),
                cornerRadius = CornerRadius(r, r)
            )

            val thumbX = fillW.coerceIn(r, size.width - r)
            drawCircle(
                color = Color.White,
                radius = r * 0.35f,
                center = Offset(thumbX - r * 0.65f, size.height / 2f)
            )
        }
    }
}