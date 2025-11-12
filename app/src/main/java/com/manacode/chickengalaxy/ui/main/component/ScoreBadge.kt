package com.manacode.chickengalaxy.ui.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.manacode.chickengalaxy.R
import java.text.DecimalFormatSymbols
import java.util.Locale


// ----------------------- Публічний Composable -----------------------
@Composable
fun ScoreBadge(
    points: Int,
    modifier: Modifier = Modifier,
    badgeHeight: Dp = 48.dp,
    eggPainter: Painter = painterResource(id = R.drawable.egg),
    widthScale: Float = 1.5f   // ← робить плашку довшою (~в півтора раза)
) {
    // ----------------------- Форматований текст -----------------------
    val formatted = remember(points) { formatScoreFixed(points) }

    // ----------------------- Параметри форми/градієнтів -----------------------
    val pillShape = RoundedCornerShape(22.dp)
    val pillGradient = Brush.horizontalGradient(listOf(Color(0xFF8A5BFF), Color(0xFF5BC2FF)))
    val pillBorder = Brush.horizontalGradient(listOf(Color(0xFFEAF2FF), Color(0xFF92E4FF)))

    // ----------------------- Розміри іконки/відступів -----------------------
    val eggSize = badgeHeight * 1.7f
    val totalHeight = if (eggSize > badgeHeight) eggSize else badgeHeight
    val eggOverlap = eggSize * 0.50f
    val textPadStart = (eggSize * 0.48f).coerceAtLeast(10.dp)
    val textPadEnd = 14.dp
    val textPadVertical = 4.dp

    // ----------------------- Типографіка від висоти бейджа -----------------------
    val density = LocalDensity.current
    val fontSizeSp = with(density) { (badgeHeight * 0.62f).toSp() }
    val strokeWidthPx = with(density) { (fontSizeSp.value * 0.12f).sp.toPx() }
    val strokeExtraDp = with(density) { (strokeWidthPx).toDp() }

    // ----------------------- Вимір фактичної ширини тексту -----------------------
    val context = LocalContext.current
    val textWidthDp = remember(formatted, fontSizeSp) {
        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            typeface = androidx.core.content.res.ResourcesCompat.getFont(
                context, R.font.poppins_extra_bold
            ) ?: android.graphics.Typeface.DEFAULT_BOLD
            textSize = with(density) { fontSizeSp.toPx() }
            fontFeatureSettings = "tnum"
        }
        with(density) { paint.measureText(formatted).toDp() }
    }

    // ----------------------- Ширина плашки (масштабуємо) -----------------------
    val minContent = 72.dp
    val baseWidth = textPadStart + textWidthDp + strokeExtraDp + textPadEnd
    val desiredWidth = baseWidth * widthScale         // ← головне: розширюємо плашку
    val pillWidth = desiredWidth.coerceAtLeast(minContent)

    // ----------------------- Слои -----------------------
    Box(modifier = modifier.height(totalHeight)) {

        // ----------------------- Плашка знизу -----------------------
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .height(badgeHeight)
                .width(pillWidth)
                .padding(start = eggOverlap)
                .zIndex(1f)
                .shadow(18.dp, pillShape, clip = false, spotColor = Color(0x66000000))
                .clip(pillShape)
                .background(pillGradient)
                .border(2.dp, pillBorder, pillShape)
                .drawGloss(pillShape)
                .padding(
                    start = textPadStart,
                    end = textPadEnd,
                    top = textPadVertical,
                    bottom = textPadVertical
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            GradientOutlinedTextShort(
                text = formatted,
                fontSize = fontSizeSp,
                strokeWidth = strokeWidthPx,
                gradientColors = listOf(Color.White, Color(0xFFEFEFEF)),
                textAlign = TextAlign.Start,
                horizontalPadding = 0.dp,
                modifier = Modifier.wrapContentWidth()
            )
        }

        // ----------------------- Яйце зверху -----------------------
        Image(
            painter = eggPainter,
            contentDescription = null,
            modifier = Modifier
                .size(eggSize)
                .align(Alignment.CenterStart)
                .offset(x = (-eggSize * 0.22f))
                .zIndex(2f)
        )
    }
}

fun formatScoreFixed(value: Int): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    val formatter = java.text.DecimalFormat("#,###", symbols)
    return formatter.format(value.coerceAtLeast(0))
}

// ----------------------- Глянец -----------------------
private fun Modifier.drawGloss(shape: RoundedCornerShape) = this
    .clip(shape)
    .drawBehind {
        val top = Brush.verticalGradient(0f to Color(0x66FFFFFF), 0.35f to Color(0x00FFFFFF))
        drawRoundRect(
            brush = top,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
        )
        val bottom = Brush.verticalGradient(0.7f to Color(0x00000000), 1f to Color(0x1AFFFFFF))
        drawRoundRect(
            brush = bottom,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
        )
    }

