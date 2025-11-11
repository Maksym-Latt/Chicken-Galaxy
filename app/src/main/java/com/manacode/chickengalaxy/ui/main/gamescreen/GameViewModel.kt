package com.manacode.feedthechick.ui.main.gamescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.unit.Density
import com.manacode.feedthechick.ui.main.gamescreen.engine.GameEngine
import com.manacode.feedthechick.ui.main.gamescreen.engine.GameState
import com.manacode.feedthechick.ui.main.gamescreen.engine.Viewport
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ----------------------- VM -----------------------
class GameViewModel : ViewModel() {

    private val engine = GameEngine()
    val state: StateFlow<GameState> = engine.state
    val events = engine.events

    private var spawnJob: Job? = null

    fun reset() {
        engine.reset()
        restartSpawnLoop()
    }

    private var firstLaunch = true

    fun pauseAndOpenSettings() = engine.pauseAndOpenSettings()
    fun resumeFromSettings() = engine.resumeFromSettings()
    fun closeSettingsToHome(onExit: (Int) -> Unit) = engine.closeSettingsToHome(onExit)
    fun showIntroOnEnter() = engine.showIntroOnEnter()
    fun removeItem(id: Int) = engine.removeItem(id)
    fun acknowledgeChickIdle() = engine.acknowledgeChickIdle()

    fun spawnTick(viewport: Viewport, density: Density) = engine.spawnTick(viewport, density)

    private fun restartSpawnLoop() {
        spawnJob?.cancel()
        spawnJob = viewModelScope.launch(AndroidUiDispatcher.Main) {
            while (true) {
                val s = state.value
                if (s.running) {
                    spawnTickHolder?.invoke()
                }
                val delayMs = s.spawnDelay
                delay(delayMs)
            }
        }
    }

    private var spawnTickHolder: (() -> Unit)? = null
    fun bindSpawner(spawnTick: () -> Unit) {
        spawnTickHolder = spawnTick
        if (state.value.running && spawnJob == null) restartSpawnLoop()
    }
}