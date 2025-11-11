package com.manacode.chickengalaxy.data.player

import com.manacode.chickengalaxy.data.model.PlayerState
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    val playerFlow: Flow<PlayerState>

    suspend fun setPoints(value: Int)
    suspend fun addPoints(delta: Int)
    suspend fun trySpend(cost: Int): Boolean

    suspend fun setEggBlasterLevel(level: Int)
    suspend fun setFeatherShieldLevel(level: Int)

    suspend fun setPlayerLevel(level: Int)
    suspend fun addExperience(expDelta: Int): LevelUpResult

    fun requiredForLevel(level: Int): Int

    suspend fun reset()
}

data class LevelUpResult(
    val leveledUp: Boolean,
    val newLevel: Int,
    val leftoverExp: Int
)
