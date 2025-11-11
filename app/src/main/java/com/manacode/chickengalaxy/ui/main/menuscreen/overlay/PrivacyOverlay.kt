package com.manacode.chickengalaxy.ui.main.menuscreen.overlay

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manacode.chickengalaxy.ui.main.component.GradientOutlinedText
import com.manacode.chickengalaxy.ui.main.component.OrangePrimaryButton
import com.manacode.chickengalaxy.ui.main.component.SecondaryBackButton

@Composable
fun PrivacyOverlay(
    onClose: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    // затемнение
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClose() }
    ) {
        // Back
        SecondaryBackButton(
            onClick = onClose,
            modifier = Modifier
                .padding(start = 16.dp, top = 24.dp)
        )

        // карточка по центру (клики поглощаем)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 360.dp)
                .wrapContentHeight()
                .clip(shape)
                .background(Color(0xFFE6FAFF))
                .border(2.dp, Color(0xFF10829A), shape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
                .padding(vertical = 18.dp, horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GradientOutlinedText(
                    text = "Privacy Policy",
                    fontSize = 28.sp,
                    gradientColors = listOf(Color.White, Color.White)
                )

                Spacer(Modifier.height(10.dp))

                // скролл содержимого
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 240.dp, max = 420.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFECFDFF))
                        .border(1.dp, Color(0xFF93E9F6), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    val scroll = rememberScrollState()
                    Column(Modifier.verticalScroll(scroll)) {
                        SectionTitle("What we collect")
                        Paragraph(
                            "We may store gameplay stats (level, points, magnet upgrades) " +
                                    "and basic device data needed for app functionality."
                        )

                        SectionTitle("How we use data")
                        Paragraph(
                            "Data is used to save progress, balance gameplay, and improve stability. " +
                                    "We don’t sell personal data."
                        )

                        SectionTitle("Third-party services")
                        Bullet("Analytics/Crash reporting (e.g., Firebase).")
                        Bullet("Attribution/notifications only if you enabled them in settings.")
                        Paragraph(
                            "See providers’ policies for details."
                        )

                        SectionTitle("Your choices")
                        Bullet("You can reset progress in Settings.")
                        Bullet("You can disable analytics/notifications in system settings.")

                        SectionTitle("Contact")
                        ClickableLink(
                            text = "support@chickengalaxy.app",
                            url = "mailto:support@chickengalaxy.app"
                        )
                        ClickableLink(
                            text = "Website",
                            url = "https://example.com/privacy"
                        )

                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Last updated: 2025-11-06",
                            color = Color(0xFF0E3E49),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                OrangePrimaryButton(
                    text = "Close",
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }
        }
    }
}

/* ---------- маленькие приватные составные ---------- */

@Composable private fun SectionTitle(text: String) {
    GradientOutlinedText(
        text = text,
        fontSize = 20.sp,
        gradientColors = listOf(Color(0xFF132E35), Color(0xFF132E35)),
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable private fun Paragraph(text: String) {
    Text(
        text = text,
        color = Color(0xFF0E3E49),
        fontSize = 14.sp,
        lineHeight = 18.sp
    )
    Spacer(Modifier.height(6.dp))
}

@Composable private fun Bullet(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("•  ", color = Color(0xFF0E3E49), fontSize = 14.sp)
        Text(
            text = text,
            color = Color(0xFF0E3E49),
            fontSize = 14.sp,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(Modifier.height(4.dp))
}

@Composable private fun ClickableLink(text: String, url: String) {
    val ctx = LocalContext.current
    val annotated = buildAnnotatedString {
        val tag = "URL"
        val start = length
        append(text)
        addStyle(
            style = SpanStyle(
                color = Color(0xFF007965),
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline
            ),
            start = start,
            end = start + text.length
        )
        addStringAnnotation(tag, url, start, start + text.length)
    }
    ClickableText(
        text = annotated,
        onClick = { offset ->
            annotated.getStringAnnotations("URL", offset, offset)
                .firstOrNull()?.let { ann ->
                    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ann.item)))
                }
        }
    )
}