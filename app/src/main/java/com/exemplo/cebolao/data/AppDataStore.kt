package com.exemplo.cebolao.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow

class AppDataStore(private val context: Context) {

    companion object {
        const val DATA_STORE_NAME = "app_preferences"
        val THEME_PREFERENCE_KEY = stringPreferencesKey("theme")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

    suspend fun saveThemePreference(theme: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[THEME_PREFERENCE_KEY] = theme
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getThemePreference(): String {
        try {
            val preferences = context.dataStore.data.first()
            return preferences[THEME_PREFERENCE_KEY] ?: "system"
        } catch (e: Exception) {
            e.printStackTrace()
            return "system"
        }
    }
}