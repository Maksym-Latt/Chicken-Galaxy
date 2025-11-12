package com.manacode.chickengalaxy.ui.main.menuscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manacode.chickengalaxy.audio.AudioController
import com.manacode.chickengalaxy.data.player.PlayerRepository
import com.manacode.chickengalaxy.data.player.SkinCatalog
import com.manacode.chickengalaxy.data.player.SkinPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repo: PlayerRepository,
    private val skins: SkinCatalog,
    private val audio: AudioController
) : ViewModel() {

    data class UpgradeTier(
        val level: Int,
        val price: Int,
        val bonus: String,
        val description: String
    )

    private val eggBlasterPlan: List<UpgradeTier> = listOf(
        UpgradeTier(level = 1, price = 0, bonus = "+0% fire rate", description = "Single yolk launcher"),
        UpgradeTier(level = 2, price = 1200, bonus = "+10% fire rate", description = "Double-yolk barrels"),
        UpgradeTier(level = 3, price = 2600, bonus = "+20% fire rate", description = "Incubator accelerator"),
        UpgradeTier(level = 4, price = 4800, bonus = "+30% fire rate", description = "Solar-heated shells"),
        UpgradeTier(level = 5, price = 8200, bonus = "+45% fire rate", description = "Nebula rail yolks")
    )

    val ui: StateFlow<PlayerUiState> =
        repo.playerFlow.map { state ->
            val palette: SkinPalette = skins.paletteForLevel(state.eggBlasterLevel)
            val eggCurrent = tierForLevel(state.eggBlasterLevel, eggBlasterPlan)
            val eggNext = nextTier(state.eggBlasterLevel, eggBlasterPlan)

            PlayerUiState(
                points = state.points,
                playerLevel = state.playerLevel,
                experience = state.experience,
                required = repo.requiredForLevel(state.playerLevel),
                eggBlasterLevel = state.eggBlasterLevel,
                eggBlasterBonus = eggCurrent.bonus,
                eggBlasterDescription = eggCurrent.description,
                eggBlasterNextCost = eggNext?.price,
                palette = palette
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerUiState())

    fun addGameResult(scorePoints: Int, bonusEggs: Int) {
        viewModelScope.launch {
            val reward = scorePoints + bonusEggs * 50
            repo.addPoints(reward)
            repo.addExperience(scorePoints / 2)
        }
    }

    fun upgradeEggBlaster(onFail: (() -> Unit)? = null, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            val currentLevel = ui.value.eggBlasterLevel
            val next = nextTier(currentLevel, eggBlasterPlan) ?: run {
                onFail?.invoke()
                return@launch
            }
            val ok = repo.trySpend(next.price)
            if (ok) {
                repo.setEggBlasterLevel(next.level)
                audio.playMagnetPurchase()
                onSuccess?.invoke()
            } else {
                audio.playNotEnoughMoney()
                onFail?.invoke()
            }
        }
    }

    fun resetAll() {
        viewModelScope.launch { repo.reset() }
    }

    private fun tierForLevel(level: Int, plan: List<UpgradeTier>): UpgradeTier =
        plan.lastOrNull { it.level <= level } ?: plan.first()

    private fun nextTier(level: Int, plan: List<UpgradeTier>): UpgradeTier? =
        plan.firstOrNull { it.level == level + 1 }
}
