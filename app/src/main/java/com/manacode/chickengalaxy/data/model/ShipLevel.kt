package com.manacode.chickengalaxy.data.model

import androidx.annotation.DrawableRes

data class ShipLevel(
    val level: Int,
    @DrawableRes val imageRes: Int,
    val pricePoints: Int,
    val damage: Int,
)
