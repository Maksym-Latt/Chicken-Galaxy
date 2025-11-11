package com.manacode.chickengalaxy.ui.main.menuscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manacode.chickengalaxy.audio.AudioController
import com.manacode.chickengalaxy.data.model.MagnetLevel
import com.manacode.chickengalaxy.data.model.ShipLevel
import com.manacode.chickengalaxy.data.player.PlayerRepository
import com.manacode.chickengalaxy.data.player.SkinCatalog
import com.manacode.eggmagnet.ui.main.menuscreen.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.getOrNull
import kotlin.collections.indexOfFirst

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repo: PlayerRepository,
    private val skins: SkinCatalog,
    private val audio: AudioController
) : ViewModel() {

    val ui: StateFlow<PlayerUiState> =
        repo.playerFlow.map { s ->
            val pair = skins.skinsForMagnet(s.shipLevel)
            PlayerUiState(
                points = s.points,
                ShipLevel = s.shipLevel,
                gameLevel = s.gameLevel,
                exp = s.exp,
                required = repo.requiredForLevel(s.gameLevel),
                magnetSkinRes = pair.magnetSkinRes,
                playerSkinRes = pair.playerSkinRes
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerUiState())

    /* ---------- Логіка івентів ---------- */

    /** Додаємо очки після гри + ці очки ідуть у досвід рівня */
    fun addGameResult(scorePoints: Int) {
        viewModelScope.launch {
            repo.addPoints(scorePoints)
            repo.addExperience(scorePoints)
        }
    }

    /** Чи вистачає очок на покупку */
    fun canAfford(cost: Int): Boolean = (ui.value.points >= cost)

    /** Списати очки (з перевіркою) */
    fun spend(cost: Int, onFail: (() -> Unit)? = null, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            val ok = repo.trySpend(cost)
            if (ok) {
                onSuccess?.invoke()
            } else {
                onFail?.invoke()
                audio.playNotEnoughMoney()
            }
        }
    }

    /** Спроба апгрейду магніта до наступного рівня за прайс зі списку levels */
    fun buyNextMagnet(levels: List<ShipLevel>, onFail: (() -> Unit)? = null) {
        viewModelScope.launch {
            val s = ui.value
            val currIdx = levels.indexOfFirst { it.level == s.ShipLevel }.coerceAtLeast(0)
            val next = levels.getOrNull(currIdx + 1) ?: run { onFail?.invoke(); return@launch }
            val price = next.pricePoints
            if (price <= 0) { onFail?.invoke(); return@launch }

            val ok = repo.trySpend(price)
            if (ok) {
                repo.setShipLevel(next.level)
                audio.playShipPurchase()
            } else {
                onFail?.invoke()
                audio.playNotEnoughMoney()
            }
        }
    }

    /** Скинути прогрес (опціонально) */
    fun resetAll() {
        viewModelScope.launch { repo.reset() }
    }
}