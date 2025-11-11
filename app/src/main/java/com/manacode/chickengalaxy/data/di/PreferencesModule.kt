package com.manacode.chickengalaxy.data.di

import android.content.Context
import android.content.SharedPreferences
import com.manacode.chickengalaxy.data.player.DefaultSkinCatalog
import com.manacode.chickengalaxy.data.player.PlayerRepository
import com.manacode.chickengalaxy.data.player.PlayerRepositoryImpl
import com.manacode.chickengalaxy.data.player.SkinCatalog
import com.manacode.chickengalaxy.data.settings.SettingsRepository
import com.manacode.chickengalaxy.data.settings.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsDataModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {
    @Provides @Singleton
    fun providePlayerRepository(
        @ApplicationContext ctx: Context
    ): PlayerRepository = PlayerRepositoryImpl(ctx)
}

@Module
@InstallIn(SingletonComponent::class)
object SkinModule {
    @Provides @Singleton
    fun provideSkinCatalog(): SkinCatalog = DefaultSkinCatalog()
}
