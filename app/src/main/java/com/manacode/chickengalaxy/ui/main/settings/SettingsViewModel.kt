package com.manacode.eggmagnet.ui.main.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manacode.eggmagnet.audio.AudioController
import com.manacode.eggmagnet.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository,
    private val audio: AudioController
) : ViewModel() {

    private val _ui = MutableStateFlow(SettingsUiState())
    val ui: StateFlow<SettingsUiState> = _ui

    init {
        val music = repo.getMusicVolume()
        val sound = repo.getSoundVolume()
        _ui.value = SettingsUiState(
            musicVolume = music,
            soundVolume = sound
        )
        audio.setMusicVolume(music)
        audio.setSoundVolume(sound)
    }

    fun setMusicVolume(value: Int) {
        val v = value.coerceIn(0, 100)
        _ui.value = _ui.value.copy(musicVolume = v)
        viewModelScope.launch { repo.setMusicVolume(v) }
        audio.setMusicVolume(v)
    }

    fun setSoundVolume(value: Int) {
        val v = value.coerceIn(0, 100)
        _ui.value = _ui.value.copy(soundVolume = v)
        viewModelScope.launch { repo.setSoundVolume(v) }
        audio.setSoundVolume(v)
    }
}
