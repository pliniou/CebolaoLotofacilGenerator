package com.exemplo.cebolao.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

class AppDataStore(private val context: Context) {

    private val dataStore = context.preferencesDataStore(name = "app_preferences")
    private val THEME_PREFERENCE_KEY = stringPreferencesKey("theme")
    
    fun getThemePreference(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_PREFERENCE_KEY] ?: "system"
        }
    }
}