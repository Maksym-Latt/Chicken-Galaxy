package com.manacode.feedthechick.ui.main.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manacode.feedthechick.R

// ---------- Public API ----------
@Composable
fun StartPrimaryButton(
    text: String = "START",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = PrimaryButton(
    text = text,
    onClick = onClick,
    modifier = modifier,
    variant = PrimaryVariant.StartGreen
)

@Composable
fun OrangePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = PrimaryButton(
    text = text,
    onClick = onClick,
    modifier = modifier,
    variant = PrimaryVariant.Orange
)

// ---------- Internal ----------
private enum class PrimaryVariant { StartGreen, Orange }

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PrimaryVariant
) {
    val shape = RoundedCornerShape(100.dp)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    // Градиенты из макета
    val gradient = when (variant) {
        PrimaryVariant.StartGreen ->
            if (!pressed) Brush.verticalGradient(0f to Color(0xFFA7FF4A), 0.89f to Color(0xFF156D00))
            else          Brush.verticalGradient(0f to Color(0xFF99B978), 0.89f to Color(0xFF0C3F00))

        PrimaryVariant.Orange ->
            if (!pressed) Brush.verticalGradient(0f to Color(0xFFFFE3A1), 1f to Color(0xFFF56B00))
            else          Brush.verticalGradient(0f to Color(0xFFFFC847), 1f to Color(0xFFAA4A00))
    }

    // Параметры типографики/паддингов из макетов
    val params: ButtonParams = when (variant) {
        PrimaryVariant.StartGreen -> {
            val fam = remember {
                FontFamily(Font(resId = R.font.poppins_extra_bold, weight = FontWeight.ExtraBold))
            }
            ButtonParams(
                family = fam,
                weight = FontWeight.ExtraBold,
                size = 36.sp,
                line = 44.sp,
                padH = 44.dp
            )
        }
        PrimaryVariant.Orange -> {
            val fam = remember {
                runCatching {
                    FontFamily(Font(resId = R.font.poppins_extra_bold, weight = FontWeight.ExtraBold))
                }.getOrElse {
                    FontFamily(Font(resId = R.font.poppins_extra_bold, weight = FontWeight.ExtraBold))
                }
            }
            ButtonParams(
                family = fam,
                weight = FontWeight.Bold,
                size = 24.sp,
                line = 32.sp,
                padH = 24.dp
            )
        }
    }

    val baseText = Color(0xFFF5F5F5)
    val pressedText = Color(
        red = (baseText.red * 0.85f).coerceIn(0f, 1f),
        green = (baseText.green * 0.85f).coerceIn(0f, 1f),
        blue = (baseText.blue * 0.85f).coerceIn(0f, 1f),
        alpha = 1f
    )
    val textColor = if (pressed) pressedText else baseText

    Box(
        modifier = modifier
            .shadow(24.dp, shape, clip = false, spotColor = Color(0x80343434))
            .clip(shape)
            .background(gradient)
            .padding(vertical = 8.dp, horizontal = params.padH)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        GradientOutlinedText(
            text = text,
            fontSize = params.size,
            gradientColors = listOf(textColor, textColor),
        )
    }
}

/** Параметры кнопки (типографика + горизонтальный паддинг) */
@Stable
private data class ButtonParams(
    val family: FontFamily,
    val weight: FontWeight,
    val size: TextUnit,
    val line: TextUnit,
    val padH: Dp
)