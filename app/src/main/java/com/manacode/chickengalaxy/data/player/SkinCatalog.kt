package com.manacode.chickengalaxy.data.player

import androidx.annotation.DrawableRes
import com.manacode.chickengalaxy.R
import javax.inject.Inject
import javax.inject.Singleton

interface SkinCatalog {
    fun skinsForMagnet(level: Int): SkinPair
}

data class SkinPair(
    @DrawableRes val magnetSkinRes: Int,
    @DrawableRes val playerSkinRes: Int
)

@Singleton
class DefaultSkinCatalog @Inject constructor() : SkinCatalog {
    private val map: Map<Int, SkinPair> = mapOf(
        1 to SkinPair(magnetSkinRes = R.drawable.magnet_lvl1, playerSkinRes = R.drawable.player_skin_lvl1),
        2 to SkinPair(magnetSkinRes = R.drawable.magnet_lvl2, playerSkinRes = R.drawable.player_skin_lvl2),
        3 to SkinPair(magnetSkinRes = R.drawable.magnet_lvl3, playerSkinRes = R.drawable.player_skin_lvl3),
        4 to SkinPair(magnetSkinRes = R.drawable.magnet_lvl4, playerSkinRes = R.drawable.player_skin_lvl4),
        5 to SkinPair(magnetSkinRes = R.drawable.magnet_lvl5, playerSkinRes = R.drawable.player_skin_lvl5),
    )

    override fun skinsForMagnet(level: Int): SkinPair =
        map[level] ?: map.getValue(1)
}