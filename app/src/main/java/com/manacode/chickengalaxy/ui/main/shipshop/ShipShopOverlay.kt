package com.manacode.chickengalaxy.ui.main.magnetshop

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manacode.chickengalaxy.ShipConfig
import com.manacode.chickengalaxy.R
import com.manacode.chickengalaxy.data.model.MagnetConfig
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedText
import com.manacode.chickengalaxy.ui.main.component.OrangePrimaryButton
import com.manacode.chickengalaxy.ui.main.component.SecondaryBackButton
import com.manacode.chickengalaxy.ui.main.gamescreen.engine.SpawnWeights
import com.manacode.chickengalaxy.ui.main.gamescreen.spawnWeightsForMagnet
import com.manacode.chickengalaxy.ui.main.menuscreen.PlayerViewModel


@Composable
fun MagnetShopScreen(
    onBack: () -> Unit,
    playerVm: PlayerViewModel = hiltViewModel()
) {
    val u by playerVm.ui.collectAsStateWithLifecycle()
    var showNoMoney by remember { mutableStateOf(false) }

    val curr = ShipConfig.byLevel(u.ShipLevel)
    val next = ShipConfig.nextOf(u.ShipLevel)
    val hasNext = next != null && next.pricePoints > 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Фон
        Image(
            painter = painterResource(R.drawable.ship),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        // ===== ВЕРХ: Back / Points =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                SecondaryBackButton(onClick = onBack)
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                NonClickableOrangeBadge(text = "${u.points.thousands()} points")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GradientOutlinedText(
                text = "LEVEL ${u.ShipLevel}",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            u.magnetSkinRes?.let {
                Image(
                    painter = painterResource(it),
                    contentDescription = "Magnet Level ${curr.level}",
                    modifier = Modifier
                        .fillMaxWidth(0.86f)
                        .wrapContentHeight()
                        .padding(vertical = 4.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(16.dp))
            ShipStatsCard(
                currLevel = u.ShipLevel,
                nextLevel = next?.level
            )
            Spacer(Modifier.height(16.dp))
            // Покупка
            if (hasNext && next != null) {
                OrangePrimaryButton(
                    text = "${next.pricePoints.thousands()} points",
                    onClick = {
                        playerVm.buyNextShip(
                            levels = ShipConfig.levels,  // ← используем один источник правды
                            onFail = { showNoMoney = true }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            } else {
                NonClickableOrangeBadge(
                    text = "MAX LEVEL",
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
            }
        }

        // ===== Диалог "Недостаточно очков" =====
        if (showNoMoney) {
            NoMoneyDialog(onDismiss = { showNoMoney = false })
        }
    }
}

/* ---------- Бейдж поинтов без клика ---------- */
@Composable
private fun NonClickableOrangeBadge(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        OrangePrimaryButton(text = text, onClick = {})
        Box(
            Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope { while (true) awaitPointerEvent() }
                }
        )
    }
}
@Composable
private fun ShipStatsCard(
    currLevel: Int,
    nextLevel: Int?
) {
    val shape = RoundedCornerShape(16.dp)

    // берем проценты из весов
    val currW = remember(currLevel) { spawnWeightsForShip(currLevel).toPercents() }
    val nextW = remember(nextLevel) { nextLevel?.let { spawnWeightsForShip(it).toPercents() } }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(shape)
            .background(Color(0xFFEAFBFF))
            .border(2.dp, Color(0xFF0BD1FF), shape)
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Text(
            "SPAWN CHANCES",
            color = Color(0xFF0E3E49),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(10.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFFFFF).copy(alpha = 0.85f))
                .border(1.dp, Color(0x330BD1FF), RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            SpawnRow("Damage",  currW.,  nextW?.)
        }
    }
}

@Composable
private fun SpawnRow(
    label: String,
    curr: Float,
    next: Float?,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                modifier = Modifier.weight(1f),
                color = Color(0xFF0E3E49),
                fontSize = 13.sp
            )
            Text(
                "${curr.round1()}%",
                color = Color(0xFF0E3E49),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            if (next != null) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF0E3E49),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                val better = next > curr
                Text(
                    "${next.round1()}%",
                    color = if (better) Color(0xFF2E7D32) else Color(0xFFC62828),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}


private fun Float.round1(): String = "%,.1f".format(this).replace(',', '.')


/* ---------- Утилити ---------- */
private fun Int.thousands(): String = "%,d".format(this).replace(',', ' ')