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
        points = prefs.getInt("points", 0),
        eggBlasterLevel = prefs.getInt("eggBlasterLevel", 1),
        featherShieldLevel = prefs.getInt("featherShieldLevel", 1),
        playerLevel = prefs.getInt("playerLevel", 1),
        experience = prefs.getInt("playerExperience", 0)
    )

    private fun writeState(s: PlayerState) {
        prefs.edit()
            .putInt("points", s.points)
            .putInt("eggBlasterLevel", s.eggBlasterLevel)
            .putInt("featherShieldLevel", s.featherShieldLevel)
            .putInt("playerLevel", s.playerLevel)
            .putInt("playerExperience", s.experience)
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

    override suspend fun setEggBlasterLevel(level: Int) = withContext(Dispatchers.IO) {
        update { it.copy(eggBlasterLevel = level.coerceAtLeast(1)) }
    }

    override suspend fun setFeatherShieldLevel(level: Int) = withContext(Dispatchers.IO) {
        update { it.copy(featherShieldLevel = level.coerceAtLeast(1)) }
    }

    override suspend fun setPlayerLevel(level: Int) = withContext(Dispatchers.IO) {
        update { it.copy(playerLevel = level.coerceAtLeast(1), experience = 0) }
    }

    override fun requiredForLevel(level: Int): Int =
        100 + (level - 1) * 20

    override suspend fun addExperience(expDelta: Int): LevelUpResult =
        withContext(Dispatchers.IO) {
            val s = _state.value

            var leveledUp = false
            var exp = (s.experience + max(0, expDelta))
            var lvl = s.playerLevel

            while (exp >= requiredForLevel(lvl)) {
                exp -= requiredForLevel(lvl)
                lvl += 1
                leveledUp = true
            }

            update { it.copy(playerLevel = lvl, experience = exp) }

            LevelUpResult(
                leveledUp = leveledUp,
                newLevel = lvl,
                leftoverExp = exp,
            )
        }

    override suspend fun reset() = withContext(Dispatchers.IO) {
        val base = PlayerState()
        _state.value = base
        writeState(base)
    }
}
