package com.manacode.eggmagnet.ui.main.menuscreen.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manacode.eggmagnet.ui.main.component.GradientOutlinedText
import com.manacode.eggmagnet.ui.main.component.SecondaryBackButton
import com.manacode.eggmagnet.ui.main.menuscreen.PlayerViewModel


@Immutable
data class LeaderboardRow(val name: String, val score: Int)

@Composable
fun LeaderboardOverlay(
    onClose: () -> Unit,
    items: List<LeaderboardRow>,
    youName: String = "You",
    playerVm: PlayerViewModel = hiltViewModel()
) {
    val u by playerVm.ui.collectAsStateWithLifecycle()
    val playerPoints = u.points

    val panelShape = RoundedCornerShape(26.dp)
    val itemShape  = RoundedCornerShape(18.dp)

    val merged = remember(items, playerPoints, youName) {
        val base = items.filterNot { it.name.equals(youName, ignoreCase = true) }
        (base + LeaderboardRow(youName, playerPoints))
            .sortedByDescending { it.score }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(remember { MutableInteractionSource() }, indication = null) { onClose() }
    ) {
        SecondaryBackButton(
            onClick = onClose,
            modifier = Modifier.padding(start = 16.dp, top = 24.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp)
                .widthIn(max = 380.dp)
                .clickable(remember { MutableInteractionSource() }, indication = null) {},
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GradientOutlinedText(
                text = "LEADERBOARD",
                fontSize = 34.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(panelShape)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFFEAFBFF), Color(0xFFD4F6FF)))
                    )
                    .border(3.dp, Color(0xFF0BD1FF), panelShape)
                    .padding(horizontal = 12.dp, vertical = 14.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 470.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 2.dp)
                ) {
                    itemsIndexed(merged) { index, row ->
                        val isYou = row.name.equals(youName, ignoreCase = true)
                        LeaderboardItem(
                            shape = itemShape,
                            rank = index + 1,
                            name = if (isYou) youName else row.name,
                            score = row.score,
                            highlight = isYou
                        )
                    }
                }
            }
        }
    }
}

/* ---------- Item ---------- */

@Composable
private fun LeaderboardItem(
    shape: RoundedCornerShape,
    rank: Int,
    name: String,
    score: Int,
    highlight: Boolean = false
) {
    val bg = if (highlight)
        Brush.verticalGradient(listOf(Color(0xFFB9FFF4), Color(0xFF5EE6D9), Color(0xFF23C4B7)))
    else
        Brush.verticalGradient(listOf(Color(0xFFFFE9B8), Color(0xFFFFB24A), Color(0xFFFF9625)))

    val border = if (highlight) Color(0xFF0E7E7A) else Color(0xFFB45C0F)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .clip(shape)
            .background(bg)
            .border(2.dp, border, shape)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Medal(rank, highlight)
        Spacer(Modifier.width(10.dp))

        Text(
            text = name,
            color = if (highlight) Color(0xFF083C3A) else Color(0xFF3C2400),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        ScorePill(score)
    }
}

@Composable
private fun Medal(rank: Int, highlight: Boolean = false) {
    val (bg, border, iconTint) = if (highlight) {
        Triple(Color(0xFFE6FFFB), Color(0xFF23C4B7), Color(0xFF0E7E7A))
    } else when (rank) {
        1 -> Triple(Color(0xFFFFF3C4), Color(0xFFFFD54F), Color(0xFFE65100))
        2 -> Triple(Color(0xFFE8F0FF), Color(0xFF90CAF9), Color(0xFF0D47A1))
        3 -> Triple(Color(0xFFFBE9E7), Color(0xFFFFAB91), Color(0xFFBF360C))
        else -> Triple(Color(0xFFFFF3D6), Color(0xFFB55E10), Color(0xFFB55E10))
    }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(bg)
            .border(2.dp, border, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
        if (rank in 1..3 && !highlight) {
            Text(
                text = rank.toString(),
                color = Color(0xFF000000).copy(alpha = 0.65f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
            )
        }
    }
}

@Composable
private fun ScorePill(score: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xFF2AF1FF), Color(0xFF14C7E3)))
            )
            .border(2.dp, Color(0xFF117A8F), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = score.formatThousands(),
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(Color(0x80000000), offset = Offset(0f, 2f), blurRadius = 6f)
            )
        )
    }
}

/* ---------- Утилиты ---------- */
private fun Int.formatThousands(): String = "%,d".format(this).replace(',', ' ')
private fun Int.thousands(): String = formatThousands()