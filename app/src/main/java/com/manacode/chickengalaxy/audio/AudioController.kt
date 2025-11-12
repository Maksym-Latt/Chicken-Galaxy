package com.manacode.chickengalaxy.audio

interface AudioController {
    fun playMenuMusic()
    fun playGameMusic()
    fun stopMusic()
    fun pauseMusic()
    fun resumeMusic()

    fun setMusicVolume(percent: Int)
    fun setSoundVolume(percent: Int)

    fun playMagnetPurchase()
    fun playNotEnoughMoney()


}
