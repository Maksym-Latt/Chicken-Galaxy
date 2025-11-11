package com.manacode.eggmagnet.ui.main.menuscreen

import androidx.annotation.DrawableRes

data class PlayerUiState(
    val points: Int = 0,
    val ShipLevel: Int = 1,
    val gameLevel: Int = 1,
    val exp: Int = 0,
    val required: Int = 100,
    @DrawableRes val magnetSkinRes: Int? = null,
    @DrawableRes val playerSkinRes: Int? = null
)