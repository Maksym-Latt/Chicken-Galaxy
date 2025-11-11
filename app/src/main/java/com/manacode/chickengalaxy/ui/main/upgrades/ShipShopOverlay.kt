package com.manacode.chickengalaxy.ui.main.upgrades

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.manacode.chickengalaxy.ui.main.menuscreen.PlayerViewModel

@Composable
fun MagnetShopScreen(
    onBack: () -> Unit,
    playerVm: PlayerViewModel = hiltViewModel()
) {
    UpgradeScreen(onBack = onBack, playerVm = playerVm)
}
