package com.manacode.chickengalaxy.ui.main.upgrades

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manacode.chickengalaxy.R
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedText
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedTextShort
import com.manacode.chickengalaxy.ui.main.component.OrangePrimaryButton
import com.manacode.chickengalaxy.ui.main.component.ScoreBadge
import com.manacode.chickengalaxy.ui.main.component.SecondaryBackButton
import com.manacode.chickengalaxy.ui.main.component.StartPrimaryButton
import com.manacode.chickengalaxy.ui.main.component.formatScoreFixed
import com.manacode.chickengalaxy.ui.main.menuscreen.PlayerViewModel
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun UpgradeScreen(
    onBack: () -> Unit,
    playerVm: PlayerViewModel = hiltViewModel()
) {
    val state by playerVm.ui.collectAsStateWithLifecycle()
    var showNoMoney by remember { mutableStateOf(false) }

    val overlay = remember {
        Brush.verticalGradient(
            0f to Color(0x66000000),
            0.55f to Color(0x33000000),
            1f to Color(0xAA050414)
        )
    }
    val stars = remember { generateUpgradeStars() }

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

        StarBackdrop(stars)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(onBack = onBack, points = state.points)
            Spacer(Modifier.height(36.dp))
            // ----------------------- Заголовок + корабель -----------------------
            GradientOutlinedText(
                text = "Hangar Upgrades",
                fontSize = 36.sp,
                gradientColors = listOf(Color.White, Color(0xFFB3E5FC))
            )
            Spacer(Modifier.height(24.dp))

            HangarShipArt()

            Spacer(Modifier.height(24.dp))

        // ----------------------- Egg Blaster -----------------------
            EggUpgradeCard(
                title = "Egg Blaster",
                level = state.eggBlasterLevel,
                bonus = state.eggBlasterBonus,
                description = state.eggBlasterDescription,
                cost = state.eggBlasterNextCost,
                currentPoints = state.points,
                onUpgrade = {
                    if (state.eggBlasterNextCost == null) return@EggUpgradeCard
                    playerVm.upgradeEggBlaster(
                        onFail = { showNoMoney = true }
                    )
                }
            )

            Spacer(Modifier.height(54.dp))

            Text(
                text = "Collect eggs in missions to earn more upgrade points.",
                color = Color(0xFFB3C6FF),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        if (showNoMoney) {
            NoMoneyDialog(onDismiss = { showNoMoney = false })
        }
    }
}

@Composable
private fun HangarShipArt() {
    Box(
        modifier = Modifier
            .size(220.dp)
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.matchParentSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x6688C6FF), Color.Transparent)
                ),
                radius = size.minDimension / 2.1f,
                center = center
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ship),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun TopBar(onBack: () -> Unit, points: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SecondaryBackButton(onClick = onBack)

        ScoreBadge(points = points)
    }
}

// ----------------------- Картка апгрейду (оновлена) -----------------------
@Composable
private fun EggUpgradeCard(
    title: String,
    level: Int,
    bonus: String,
    description: String,
    cost: Int?,
    currentPoints: Int,
    onUpgrade: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)

    // ----------------------- Фони/бордери -----------------------
    val glass = Brush.verticalGradient(
        0f to Color(0x332B3C8F),
        1f to Color(0x1A0E1438)
    )
    val innerGlow = Brush.verticalGradient(
        0f to Color(0x1AFFFFFF),
        1f to Color(0x00000000)
    )
    val borderGradient = Brush.linearGradient(
        colors = listOf(Color(0x99B3E5FC), Color(0x66B388FF)),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    // ----------------------- Контейнер -----------------------
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(20.dp, shape, clip = false, spotColor = Color(0x55000000))
            .clip(shape)
            .background(glass)
            .border(1.5.dp, borderGradient, shape)
            .drawBehind {
                drawRoundRect(
                    brush = innerGlow,
                    cornerRadius = CornerRadius(22.dp.toPx(), 22.dp.toPx())
                )
            }
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

            // ----------------------- Заголовок + рівень -----------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    GradientOutlinedTextShort(
                        text = title,
                        fontSize = 22.sp,
                        strokeWidth = 4f,
                        gradientColors = listOf(Color(0xFFFFF59D), Color(0xFFFFB300)),
                        modifier = Modifier.fillMaxWidth(0.82f)
                    )

                    Spacer(Modifier.height(10.dp))

                    LevelChip(level = level)
                }
            }

            // ----------------------- Бонус -----------------------
            BonusChip(text = bonus)

            // ----------------------- Опис -----------------------
            GradientOutlinedTextShort(
                text = description,
                gradientColors = listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF)),
                fontSize = 12.sp,
            )

            // ----------------------- Ціна / залишок / кнопка -----------------------
            if (cost != null) {
                val canAfford = currentPoints >= cost
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OrangePrimaryButton(
                        text = if (canAfford) "Upgrade" else "Not enough eggs",
                        onClick = if (canAfford) onUpgrade else ({ }),
                        modifier = Modifier
                            .align(Alignment.End)
                            .fillMaxWidth()
                    )
                }
            } else {
                StartPrimaryButton(
                    text = "Max level",
                    onClick = {},
                    modifier = Modifier
                        .align(Alignment.End)
                        .fillMaxWidth()
                )
            }
        }
    }
}

// ----------------------- Чип рівня -----------------------
@Composable
private fun LevelChip(level: Int) {
    val s = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .clip(s)
            .background(Brush.horizontalGradient(listOf(Color(0x3346C2FF), Color(0x3315E1FF))))
            .border(1.dp, Color(0x55B3E5FC), s)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Level $level",
            color = Color(0xFFB3E5FC),
            fontSize = 12.sp
        )
    }
}

// ----------------------- Чип бонусу -----------------------
@Composable
private fun BonusChip(text: String) {
    val s = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .clip(s)
            .background(Brush.horizontalGradient(listOf(Color(0x1AFFD54F), Color(0x1AFFB300))))
            .border(1.dp, Color(0x66FFD54F), s)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        GradientOutlinedTextShort(
            text = text,
            fontSize = 16.sp,
            strokeWidth = 3f,
            gradientColors = listOf(Color(0xFFFFF59D), Color(0xFFFFB300))
        )
    }
}

private data class UpgradeStar(val x: Float, val y: Float, val size: Float, val alpha: Float)

private fun generateUpgradeStars(count: Int = 70, seed: Int = 73): List<UpgradeStar> {
    val random = Random(seed)
    return List(count) {
        UpgradeStar(
            x = random.nextFloat(),
            y = random.nextFloat(),
            size = random.nextFloat().coerceIn(0.002f, 0.01f),
            alpha = random.nextFloat().coerceIn(0.35f, 0.9f)
        )
    }
}

@Composable
private fun StarBackdrop(stars: List<UpgradeStar>) {
    Canvas(Modifier) {
        stars.forEach { star ->
            drawCircle(
                color = Color.White.copy(alpha = star.alpha),
                radius = size.minDimension * star.size,
                center = androidx.compose.ui.geometry.Offset(star.x * size.width, star.y * size.height)
            )
        }
    }
}

private fun Int.thousands(): String {
    val absValue = kotlin.math.abs(this)
    val formatted = when {
        absValue >= 1_000_000 -> "${(this / 1_000_000f).roundToOne()}M"
        absValue >= 1_000 -> "${(this / 1_000f).roundToOne()}K"
        else -> this.toString()
    }
    return formatted
}

private fun Float.roundToOne(): String {
    val rounded = (this * 10).roundToInt() / 10f
    return if (rounded % 1f == 0f) rounded.toInt().toString() else rounded.toString()
}
