package com.manacode.chickengalaxy.ui.main.upgrades

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedText
import com.manacode.chickengalaxy.ui.main.component.OrangePrimaryButton
import com.manacode.chickengalaxy.ui.main.component.SecondaryBackButton
import com.manacode.chickengalaxy.ui.main.component.StartPrimaryButton
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

    val background = remember {
        Brush.verticalGradient(
            0f to Color(0xFF101531),
            0.4f to Color(0xFF15103A),
            1f to Color(0xFF040313)
        )
    }
    val stars = remember { generateUpgradeStars() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        StarBackdrop(stars)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(onBack = onBack, points = state.points)
            Spacer(Modifier.height(16.dp))
            GradientOutlinedText(
                text = "Hangar Upgrades",
                fontSize = 32.sp,
                gradientColors = listOf(Color.White, Color(0xFFB3E5FC))
            )
            Spacer(Modifier.height(18.dp))

            UpgradeCard(
                title = "Egg Blaster",
                level = state.eggBlasterLevel,
                bonus = state.eggBlasterBonus,
                description = state.eggBlasterDescription,
                cost = state.eggBlasterNextCost,
                accent = Color(0xFFFFD54F),
                onUpgrade = {
                    if (state.eggBlasterNextCost == null) return@UpgradeCard
                    playerVm.upgradeEggBlaster(
                        onFail = { showNoMoney = true }
                    )
                }
            )
            Spacer(Modifier.height(16.dp))
            UpgradeCard(
                title = "Feather Shield",
                level = state.featherShieldLevel,
                bonus = state.featherShieldBonus,
                description = state.featherShieldDescription,
                cost = state.featherShieldNextCost,
                accent = Color(0xFF80CBC4),
                onUpgrade = {
                    if (state.featherShieldNextCost == null) return@UpgradeCard
                    playerVm.upgradeFeatherShield(
                        onFail = { showNoMoney = true }
                    )
                }
            )
            Spacer(Modifier.height(24.dp))
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
private fun TopBar(onBack: () -> Unit, points: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SecondaryBackButton(onClick = onBack)
        PointsDisplay(points)
    }
}

@Composable
private fun PointsDisplay(points: Int) {
    val formatted = remember(points) { points.thousands() }
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFFFF59D), Color(0xFFFFB300))
                )
            )
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = "$formatted pts",
            color = Color(0xFF3E2723),
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun UpgradeCard(
    title: String,
    level: Int,
    bonus: String,
    description: String,
    cost: Int?,
    accent: Color,
    onUpgrade: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0x33212B6B))
            .border(2.dp, Color(0x5588A4FF), shape)
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$title",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Level $level",
                        color = Color(0xFFB3C6FF)
                    )
                }
                UpgradeBadge(accent)
            }
            Text(
                text = bonus,
                color = accent,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = description,
                color = Color(0xFFCFD8DC),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(4.dp))
            if (cost != null) {
                OrangePrimaryButton(
                    text = "Upgrade — ${cost.thousands()} pts",
                    onClick = onUpgrade,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            } else {
                StartPrimaryButton(
                    text = "Max level",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
        }
    }
}

@Composable
private fun UpgradeBadge(accent: Color) {
    Canvas(Modifier.size(72.dp)) {
        val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2.6f
        drawCircle(color = accent.copy(alpha = 0.4f), radius = radius * 1.5f, center = center)
        drawCircle(color = accent, radius = radius, center = center)
        drawCircle(color = Color.White, radius = radius * 0.5f, center = center, style = Stroke(width = radius * 0.15f))
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
    Canvas(Modifier.matchParentSize()) {
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
