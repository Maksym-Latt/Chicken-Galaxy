package com.manacode.chickengalaxy.ui.main.menuscreen

import androidx.lifecycle.ViewModel
import com.manacode.chickengalaxy.audio.AudioController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor(
    private val audio: AudioController
) : ViewModel() {

    enum class Screen { Menu, Game }
    enum class MenuOverlay { None, Leaderboard, Settings, MagnetShop, Privacy }

    data class UiState(
        val screen: Screen = Screen.Menu,
        val menuOverlay: MenuOverlay = MenuOverlay.None,
        val playerPoints: Int = 11_932,
        val magnetLevel: Int = 1             // 1..N
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    /* ---------- Navigation ---------- */
    init {
        audio.playMenuMusic()
    }

    fun startGame() = update {
        audio.playGameMusic()
        copy(screen = Screen.Game, menuOverlay = MenuOverlay.None)
    }


    fun backFromGameToMenu() = update {
        audio.playMenuMusic()
        copy(screen = Screen.Menu)
    }

    fun backFromGameToMenuWithMagnetShop() = update {
        audio.playMenuMusic()
        copy(screen = Screen.Menu, menuOverlay = MenuOverlay.MagnetShop)
    }

    /* ---------- Menu overlays ---------- */
    fun openPrivacy() {
        updateIfMenu { copy(menuOverlay = MenuOverlay.Privacy) }
    }
    fun openLeaderboard() = updateIfMenu { copy(menuOverlay = MenuOverlay.Leaderboard) }
    fun openSettings()    = updateIfMenu { copy(menuOverlay = MenuOverlay.Settings) }
    fun openMagnetShop()  = updateIfMenu { copy(menuOverlay = MenuOverlay.MagnetShop) }
    fun closeOverlay()    = update { copy(menuOverlay = MenuOverlay.None) }

    fun onBackPressed(): Boolean {
        val s = _ui.value
        return when {
            s.menuOverlay != MenuOverlay.None -> { closeOverlay(); true }
            s.screen == Screen.Game -> { backFromGameToMenu(); true }
            else -> false
        }
    }

    /* ---------- helpers ---------- */
    private inline fun update(block: UiState.() -> UiState) {
        _ui.update(block)
    }
    private inline fun updateIfMenu(block: UiState.() -> UiState) {
        _ui.update { if (it.screen == Screen.Menu) block(it) else it }
    }

    override fun onCleared() {
        super.onCleared()
        audio.stopMusic()
    }
}
