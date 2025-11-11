package com.manacode.chickengalaxy.ui.main.menuscreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manacode.chickengalaxy.ui.main.gamescreen.GameScreen
import com.manacode.chickengalaxy.ui.main.menuscreen.overlay.LeaderboardOverlay
import com.manacode.chickengalaxy.ui.main.menuscreen.overlay.LeaderboardRow
import com.manacode.chickengalaxy.ui.main.menuscreen.overlay.PrivacyOverlay
import com.manacode.chickengalaxy.ui.main.menuscreen.overlay.SettingsOverlay
import com.manacode.chickengalaxy.ui.main.upgrades.UpgradeScreen

@Composable
fun AppRoot(
    vm: MainViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsStateWithLifecycle()

    BackHandler(enabled = true) {
        if (!vm.onBackPressed()) {
            // no-op
        }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFF040414))) {
        AnimatedContent(
            targetState = ui.screen,
            label = "screen",
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { screen ->
            when (screen) {
                MainViewModel.Screen.Menu -> MenuScreen(
                    onStartGame = vm::startGame,
                    onOpenSettings = vm::openSettings,
                    onOpenLeaderboard = vm::openLeaderboard,
                    onOpenSkins = vm::openSkins,
                )
                MainViewModel.Screen.Game -> GameScreen(
                    onExitToMenu = vm::backFromGameToMenu,
                    onOpenSkins = vm::backFromGameToMenuWithSkins
                )
            }
        }

        if (ui.screen == MainViewModel.Screen.Menu && ui.menuOverlay != MainViewModel.MenuOverlay.None) {
            when (ui.menuOverlay) {
                MainViewModel.MenuOverlay.Leaderboard ->
                    LeaderboardOverlay(
                        onClose = vm::closeOverlay,
                        items = listOf(
                            LeaderboardRow("Sky Raily", 13200),
                            LeaderboardRow("John Douk", 11200),
                            LeaderboardRow("John Snows", 9200),
                            LeaderboardRow("John", 8200),
                            LeaderboardRow("Singer", 200),
                            LeaderboardRow("Antony", 100),
                        ),
                        youName = "You"
                    )

                MainViewModel.MenuOverlay.Settings ->
                    SettingsOverlay(
                        onClose = vm::closeOverlay,
                        onPrivacy = vm::openPrivacy
                    )

                MainViewModel.MenuOverlay.Skins ->
                    UpgradeScreen(onBack = vm::closeOverlay)

                MainViewModel.MenuOverlay.Privacy ->
                    PrivacyOverlay(onClose = vm::closeOverlay)

                MainViewModel.MenuOverlay.None -> Unit
            }
        }
    }
}
