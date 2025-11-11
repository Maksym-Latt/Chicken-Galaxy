package com.manacode.feedthechick.ui.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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