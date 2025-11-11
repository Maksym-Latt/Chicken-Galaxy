package com.manacode.chickengalaxy.data.player

import android.content.Context
import android.content.SharedPreferences
import com.manacode.chickengalaxy.data.model.PlayerState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PlayerRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("player_prefs", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(readState())
    override val playerFlow: Flow<PlayerState> = _state.asStateFlow()

    private fun readState(): PlayerState = PlayerState(
        points = prefs.getInt("points", 0),        // демо-значення
        ship = prefs.getInt("shipLevel", 1),
        gameLevel = prefs.getInt("gameLevel", 1),
        exp = prefs.getInt("exp", 0)
    )

    private fun writeState(s: PlayerState) {
        prefs.edit()
            .putInt("points", s.points)
            .putInt("shipLevel", s.ship)
            .putInt("gameLevel", s.gameLevel)
            .putInt("exp", s.exp)
            .apply()
    }

    private fun update(transform: (PlayerState) -> PlayerState) {
        val new = transform(_state.value)
        _state.value = new
        writeState(new)
    }

    override suspend fun setPoints(value: Int) = withContext(Dispatchers.IO) {
        update { it.copy(points = max(0, value)) }
    }

    override suspend fun addPoints(delta: Int) = withContext(Dispatchers.IO) {
        if (delta == 0) return@withContext
        update { it.copy(points = max(0, it.points + delta)) }
    }

    override suspend fun trySpend(cost: Int): Boolean = withContext(Dispatchers.IO) {
        if (cost <= 0) return@withContext true
        val ok = _state.value.points >= cost
        if (ok) update { it.copy(points = it.points - cost) }
        ok
    }

    override suspend fun setShipLevel(level: Int) = withContext(Dispatchers.IO) {
        update { it.copy(ship = level.coerceAtLeast(1)) }
    }

    override suspend fun setGameLevel(level: Int) = withContext(Dispatchers.IO) {
        update { it.copy(gameLevel = level.coerceAtLeast(1), exp = 0) }
    }

    // EXP: 1lvl=100, 2lvl=120, 3lvl=140, ... => 100 + (level-1)*20
    override fun requiredForLevel(level: Int): Int =
        100 + (level - 1) * 20

    override suspend fun addExperience(expDelta: Int): LevelUpResult =
        withContext(Dispatchers.IO) {
            val s = _state.value

            var leveledUp = false
            var exp = (s.exp + max(0, expDelta))
            var lvl = s.gameLevel

            while (exp >= requiredForLevel(lvl)) {
                exp -= requiredForLevel(lvl)
                lvl += 1
                leveledUp = true
            }

            update { it.copy(gameLevel = lvl, exp = exp) }

            LevelUpResult(
                leveledUp = leveledUp,
                newLevel = lvl,
                leftoverExp = exp,
            )
        }

    override suspend fun reset() = withContext(Dispatchers.IO) {
        val base = PlayerState(points = 0, ship = 1, gameLevel = 1, exp = 0)
        _state.value = base
        writeState(base)
    }
}