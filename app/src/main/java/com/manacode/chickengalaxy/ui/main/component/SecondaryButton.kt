package com.manacode.feedthechick.ui.main.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun SecondaryBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) =  SecondaryIconButton(
    onClick = onClick,
    modifier = modifier
) {
    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
}

@Composable
fun SecondaryIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 40.dp,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    icon: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(100)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val gradient = if (!pressed) {
        Brush.verticalGradient(
            0f to Color(0xffd9c28d), // Яркий золотой
            1f to Color(0xffff872a)  // Классический золотой
        )
    } else {
        Brush.verticalGradient(
            0f to Color(0xffffc847), // Темный золотой
            1f to Color(0xff893c00)  // Золотисто-коричневый
        )
    }

    Box(
        modifier = modifier
            .shadow(24.dp, shape, spotColor = Color(0x80343434), clip = false)
            .clip(shape)
            .background(gradient)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer { alpha = if (pressed) 0.5f else 1f },
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}

@Composable
fun SecondaryIconButton(
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 40.dp,
    contentPadding: PaddingValues = PaddingValues(8.dp)
) {
    SecondaryIconButton(
        onClick = onClick,
        modifier = modifier,
        iconSize = iconSize,
        contentPadding = contentPadding
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.fillMaxSize()
        )
    }
}