package com.manacode.chickengalaxy.ui.main.menuscreen

import androidx.compose.ui.graphics.Color
import com.manacode.chickengalaxy.data.player.SkinPalette

data class PlayerUiState(
    val points: Int = 0,
    val playerLevel: Int = 1,
    val experience: Int = 0,
    val required: Int = 100,
    val eggBlasterLevel: Int = 1,
    val eggBlasterBonus: String = "+0% fire rate",
    val eggBlasterEnergyMultiplier: Float = 1f,
    val eggBlasterDescription: String = "Starter cannon",
    val eggBlasterNextCost: Int? = null,
    val palette: SkinPalette = SkinPalette(primary = Color(0xFFFFD54F), accent = Color(0xFFFFB300))
)
