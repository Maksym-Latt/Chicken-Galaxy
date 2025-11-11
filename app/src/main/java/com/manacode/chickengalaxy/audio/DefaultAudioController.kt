package com.manacode.chickengalaxy.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import com.manacode.chickengalaxy.data.settings.SettingsRepository

@Singleton
class DefaultAudioController @Inject constructor(
    @ApplicationContext private val context: Context,
    settingsRepository: SettingsRepository
) : AudioController {

    private enum class MusicChannel { MENU, GAME }
    private enum class SoundEffect { SHIP_PURCHASE, NOT_ENOUGH_MONEY, MISS, HIT, EXPLOSION, WIN, LOSE }

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val effectToName = mapOf(
        SoundEffect.NOT_ENOUGH_MONEY to "sfx_no_money",
        SoundEffect.WIN to "sfx_game_win",
        SoundEffect.LOSE to "sfx_game_lose"
    )

    private val effectToResId = effectToName.mapValues { resolveRaw(it.value) }
    private val loadedEffects = mutableMapOf<SoundEffect, Int>()
    private val readySamples = mutableSetOf<Int>()
    private val pendingPlays = mutableSetOf<Int>()

    private val menuMusicRes = resolveRaw("music_menu")
    private val gameMusicRes = resolveRaw("music_game")

    private val musicPlayers = mutableMapOf<MusicChannel, MediaPlayer>()
    private var currentMusic: MusicChannel? = null

    private var musicVolume: Float = settingsRepository.getMusicVolume().toVolume()
    private var soundVolume: Float = settingsRepository.getSoundVolume().toVolume()
    private var resumeAfterLifecyclePause: Boolean = false

    init {
        soundPool.setOnLoadCompleteListener { pool, sampleId, status ->
            if (status == 0) {
                readySamples += sampleId
                if (pendingPlays.remove(sampleId)) {
                    pool.play(sampleId, soundVolume, soundVolume, 1, 0, 1f)
                }
            } else {
                pendingPlays.remove(sampleId)
            }
        }
    }

    override fun playMenuMusic() {
        resumeAfterLifecyclePause = false
        playMusic(MusicChannel.MENU)
    }

    override fun playGameMusic() {
        resumeAfterLifecyclePause = false
        playMusic(MusicChannel.GAME)
    }

    override fun stopMusic() {
        currentMusic?.let { channel ->
            musicPlayers[channel]?.let { player ->
                if (player.isPlaying) player.pause()
                player.seekTo(0)
            }
        }
        currentMusic = null
        resumeAfterLifecyclePause = false
    }

    override fun pauseMusic() {
        currentMusic?.let { channel ->
            musicPlayers[channel]?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                    resumeAfterLifecyclePause = true
                }
            }
        }
    }

    override fun resumeMusic() {
        if (!resumeAfterLifecyclePause) return
        resumeAfterLifecyclePause = false
        currentMusic?.let { channel ->
            val player = musicPlayers[channel]
            if (player != null && !player.isPlaying) {
                try {
                    player.start()
                } catch (_: IllegalStateException) {
                    musicPlayers.remove(channel)
                    currentMusic = null
                }
            } else if (player == null) {
                currentMusic = null
            }
        }
    }

    override fun setMusicVolume(percent: Int) {
        musicVolume = percent.toVolume()
        musicPlayers.values.forEach { player ->
            player.setVolume(musicVolume, musicVolume)
        }
    }

    override fun setSoundVolume(percent: Int) {
        soundVolume = percent.toVolume()
    }

    override fun playMagnetPurchase() {
        playEffect(SoundEffect.SHIP_PURCHASE)
    }

    override fun playNotEnoughMoney() {
        playEffect(SoundEffect.NOT_ENOUGH_MONEY)
    }

    override fun playEggPickup() {
        playEffect(SoundEffect.HIT)
    }

    override fun playRockPickup() {
        playEffect(SoundEffect.MISS)
    }

    override fun playBombHit() {
        playEffect(SoundEffect.EXPLOSION)
    }

    override fun playGameWin() {
        playEffect(SoundEffect.WIN)
    }

    override fun playGameLose() {
        playEffect(SoundEffect.LOSE)
    }

    private fun playMusic(channel: MusicChannel) {
        if (currentMusic == channel && musicPlayers[channel]?.isPlaying == true) {
            return
        }

        currentMusic?.let { active ->
            musicPlayers[active]?.let { player ->
                if (player.isPlaying) player.pause()
                player.seekTo(0)
            }
        }

        val player = ensurePlayer(channel)
        currentMusic = if (player != null) channel else null
        player?.let {
            it.setVolume(musicVolume, musicVolume)
            it.isLooping = true
            try {
                it.start()
            } catch (_: IllegalStateException) {
                // player might have been released by the system, recreate lazily on next call
                musicPlayers.remove(channel)
            }
        }
    }

    private fun ensurePlayer(channel: MusicChannel): MediaPlayer? {
        musicPlayers[channel]?.let { existing ->
            return existing
        }

        val resId = when (channel) {
            MusicChannel.MENU -> menuMusicRes
            MusicChannel.GAME -> gameMusicRes
        }

        if (resId == 0) return null

        return runCatching { MediaPlayer.create(context, resId) }
            .getOrNull()
            ?.also { player ->
                player.isLooping = true
                player.setVolume(musicVolume, musicVolume)
                musicPlayers[channel] = player
            }
    }

    private fun playEffect(effect: SoundEffect) {
        if (soundVolume <= 0f) return

        val resId = effectToResId[effect] ?: 0
        if (resId == 0) return

        val sampleId = loadedEffects[effect]
        if (sampleId != null) {
            if (readySamples.contains(sampleId)) {
                soundPool.play(sampleId, soundVolume, soundVolume, 1, 0, 1f)
            } else {
                pendingPlays += sampleId
            }
            return
        }

        val loadId = soundPool.load(context, resId, 1)
        if (loadId != 0) {
            loadedEffects[effect] = loadId
            pendingPlays += loadId
        }
    }

    private fun resolveRaw(name: String): Int {
        if (name.isBlank()) return 0
        return context.resources.getIdentifier(name, "raw", context.packageName)
    }

    private fun Int.toVolume(): Float = (this.coerceIn(0, 100) / 100f).coerceIn(0f, 1f)
}
