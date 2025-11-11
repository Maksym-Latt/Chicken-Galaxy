package com.manacode.chickengalaxy.ui.main.upgrades

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manacode.chickengalaxy.ui.main.component.OrangePrimaryButton

@Composable
fun NoMoneyDialog(onDismiss: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xAA05040F))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
    ) {
        val shape = RoundedCornerShape(20.dp)
        val gradient = Brush.verticalGradient(
            listOf(Color(0xFFFFF8E1), Color(0xFFFFD180))
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(300.dp)
                .wrapContentHeight()
                .clip(shape)
                .background(gradient)
                .border(2.dp, Color(0xFF4E342E), shape)
                .padding(all = 20.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Out of corn!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4E342E)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Collect more eggs in battle to afford this upgrade.",
                    fontSize = 16.sp,
                    color = Color(0xFF4E342E),
                    lineHeight = 20.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                OrangePrimaryButton(
                    text = "Got it",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(0.6f)
                )
            }
        }
    }
}
