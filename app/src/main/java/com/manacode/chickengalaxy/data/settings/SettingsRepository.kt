package com.manacode.eggmagnet.data.settings

interface SettingsRepository {
    fun getMusicVolume(): Int
    fun getSoundVolume(): Int

    fun setMusicVolume(value: Int)
    fun setSoundVolume(value: Int)
}