package com.manacode.chickengalaxy.ui.main.gamescreen.overlay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedText
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedTextShort
import com.manacode.chickengalaxy.ui.main.component.OrangePrimaryButton
import com.manacode.chickengalaxy.ui.main.component.ScoreBadge
import com.manacode.chickengalaxy.ui.main.component.StartPrimaryButton
import com.manacode.chickengalaxy.ui.main.component.formatScoreFixed
import com.manacode.chickengalaxy.ui.main.gamescreen.GameResult
import com.manacode.chickengalaxy.R
import com.manacode.chickengalaxy.ui.main.component.SecondaryIconButton

// ----------------------- Win Overlay -----------------------
@Composable
fun WinOverlay(
    result: GameResult,
    totalPoints: Int,
    gainedPoints: Int,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit,
    onOpenSkins: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC02020A))
    ) {

        // ----------------------- Top HUD: full-width -----------------------
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(horizontal = 25.dp, vertical = 24.dp),
            verticalAlignment = Alignment.Top
        ) {
            Spacer(Modifier.weight(1f)) // ← штовхає контент у правий край

            val density = LocalDensity.current
            var badgeWidthPx by remember { mutableStateOf(0) }
            val badgeWidthDp = with(density) { badgeWidthPx.toDp() }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ScoreBadge(
                    points = totalPoints,
                    widthScale = 1.4f,
                    modifier = Modifier.onGloballyPositioned { badgeWidthPx = it.size.width }
                )
                EggsGainRow(
                    amount = gainedPoints,
                    modifier = Modifier
                        .width(badgeWidthDp)
                        .padding(end = 4.dp)
                )
            }
        }


        // ----------------------- Center content -----------------------
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GradientOutlinedText(
                text = "GOOD JOB!",
                fontSize = 40.sp,
                gradientColors = listOf(Color.White, Color(0xFFFFE082))
            )

            Image(
                painter = painterResource(id = R.drawable.player_win),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )

            OrangePrimaryButton(
                text = "Restart",
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth(0.6f)
            )

            StartPrimaryButton(
                text = "MENU",
                onClick = onMenu,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }
    }
}

// ----------------------- +Eggs compact row -----------------------
@Composable
private fun EggsGainRow(
    amount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        GradientOutlinedTextShort(
            text = "+${formatScoreFixed(amount)}",
            fontSize = 18.sp,
            strokeWidth = 3f,
            gradientColors = listOf(Color.White, Color(0xFFFFF59D)),
            modifier = Modifier.wrapContentWidth()
        )
        Spacer(Modifier.width(6.dp))
        Image(
            painter = painterResource(id = R.drawable.egg),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
    }
}