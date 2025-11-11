package com.manacode.eggmagnet.ui.main.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _ui = MutableStateFlow(SplashUiState())
    val ui: StateFlow<SplashUiState> = _ui

    fun start() {
        if (!_ui.value.isLoading) return

        viewModelScope.launch {
            var p = 0f
            while (p < 1f) {
                delay(80)
                p = (p + 0.03f).coerceAtMost(1f)
                _ui.value = SplashUiState(progress = p, isLoading = p < 1f)
            }
        }
    }
}