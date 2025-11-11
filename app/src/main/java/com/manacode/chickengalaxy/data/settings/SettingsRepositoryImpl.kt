package com.manacode.eggmagnet.data.settings

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val prefs: SharedPreferences
) : SettingsRepository {

    private companion object {
        const val KEY_MUSIC = "music_volume"
        const val KEY_SOUND = "sound_volume"
        const val DEF_MUSIC = 70
        const val DEF_SOUND = 80
    }

    override fun getMusicVolume(): Int = prefs.getInt(KEY_MUSIC, DEF_MUSIC)
    override fun getSoundVolume(): Int = prefs.getInt(KEY_SOUND, DEF_SOUND)

    override fun setMusicVolume(value: Int) {
        prefs.edit().putInt(KEY_MUSIC, value.coerceIn(0, 100)).apply()
    }

    override fun setSoundVolume(value: Int) {
        prefs.edit().putInt(KEY_SOUND, value.coerceIn(0, 100)).apply()
    }
}