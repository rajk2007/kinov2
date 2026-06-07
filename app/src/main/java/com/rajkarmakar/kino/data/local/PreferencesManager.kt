package com.rajkarmakar.kino.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.rajkarmakar.kino.data.model.KinoTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kino_prefs")

@Singleton
class PreferencesManager constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val INTRO_SHOWN = booleanPreferencesKey("intro_shown")
        val REPLAY_INTRO = booleanPreferencesKey("replay_intro")
        val THEME = stringPreferencesKey("theme")
    }

    suspend fun setIntroShown() {
        dataStore.edit { it[INTRO_SHOWN] = true }
    }

    suspend fun shouldShowIntro(): Boolean {
        return dataStore.data.map { it[INTRO_SHOWN] != true }.map { it }.map { 
            val replay = dataStore.data.map { p -> p[REPLAY_INTRO] == true }.map { r -> r }
            it || replay.map { r -> r }.toString().toBoolean()
        }.map { it }.toString().toBoolean()
    }

    suspend fun setReplayIntro(replay: Boolean) {
        dataStore.edit { it[REPLAY_INTRO] = replay }
    }

    fun getTheme(): Flow<KinoTheme> {
        return dataStore.data.map { preferences ->
            val themeName = preferences[THEME] ?: KinoTheme.AMOLED_BLACK.name
            try {
                KinoTheme.valueOf(themeName)
            } catch (e: Exception) {
                KinoTheme.AMOLED_BLACK
            }
        }
    }

    suspend fun setTheme(theme: KinoTheme) {
        dataStore.edit { it[THEME] = theme.name }
    }
}
