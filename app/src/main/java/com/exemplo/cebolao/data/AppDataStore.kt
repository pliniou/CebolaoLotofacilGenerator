package com.exemplo.cebolao.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences as PreferencesCore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")
class AppDataStore(private val context: Context) {


    private val THEME_PREFERENCE_KEY = stringPreferencesKey("theme")

    suspend fun saveThemePreference(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_PREFERENCE_KEY] = theme
        }
    }
    fun getThemePreference(): Flow<String> {
        return context.dataStore.data.map { preferences: Preferences ->
            preferences[THEME_PREFERENCE_KEY] ?: ""
        }
    }
}