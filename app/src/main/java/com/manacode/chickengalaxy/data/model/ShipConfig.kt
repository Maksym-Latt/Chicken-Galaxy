package com.manacode.chickengalaxy.data.model

object ShipConfig {
    val levels: List<ShipLevel> = listOf(
        ShipLevel(level = 1, pricePoints = 0, damage = 1),
        ShipLevel(level = 2, pricePoints = 2000, damage = 2),
        ShipLevel(level = 3, pricePoints = 4500, damage = 3),
        ShipLevel(level = 4, pricePoints = 7500, damage = 4),
        ShipLevel(level = 5, pricePoints = 12000, damage = 5)
    )

    val maxLevel = levels.maxOf { it.level }

    fun byLevel(level: Int): ShipLevel =
        levels.firstOrNull { it.level == level } ?: levels.first()

    fun nextOf(level: Int): ShipLevel? =
        levels.firstOrNull { it.level == level + 1 }
}
