package com.manacode.chickengalaxy.data.player

import androidx.compose.ui.graphics.Color
import javax.inject.Inject
import javax.inject.Singleton

data class SkinPalette(
    val primary: Color,
    val accent: Color
)

interface SkinCatalog {
    fun paletteForLevel(level: Int): SkinPalette
}

@Singleton
class DefaultSkinCatalog @Inject constructor() : SkinCatalog {
    private val palettes: List<SkinPalette> = listOf(
        SkinPalette(primary = Color(0xFFFFD54F), accent = Color(0xFFFFB300)),
        SkinPalette(primary = Color(0xFF81D4FA), accent = Color(0xFF0288D1)),
        SkinPalette(primary = Color(0xFFA5D6A7), accent = Color(0xFF2E7D32)),
        SkinPalette(primary = Color(0xFFFFAB91), accent = Color(0xFFE64A19)),
        SkinPalette(primary = Color(0xFFD1C4E9), accent = Color(0xFF673AB7))
    )

    override fun paletteForLevel(level: Int): SkinPalette {
        val index = (level - 1).coerceIn(0, palettes.lastIndex)
        return palettes[index]
    }
}
