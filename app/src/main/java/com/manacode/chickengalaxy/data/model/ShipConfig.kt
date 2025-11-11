package com.manacode.chickengalaxy

import com.manacode.chickengalaxy.data.model.ShipLevel
import kotlin.collections.first

object ShipConfig {
    val levels: List<ShipLevel> = listOf(
        ShipLevel(1, R.drawable.ship_lvl1, 2000,  damage = 1),
        ShipLevel(2, R.drawable.ship_lvl1, 3000,  damage = 2),
        ShipLevel(3, R.drawable.ship_lvl1, 7000,  damage = 3),
        ShipLevel(4, R.drawable.ship_lvl1, 11000, damage = 4),
        ShipLevel(5, R.drawable.ship_lvl1, -1,    damage = 5),
    )

    val maxLevel = levels.maxOf { it.level }

    fun byLevel(level: Int): ShipLevel =
        levels.firstOrNull { it.level == level } ?: levels.first()

    fun nextOf(level: Int): ShipLevel? =
        levels.firstOrNull { it.level == level + 1 }
}