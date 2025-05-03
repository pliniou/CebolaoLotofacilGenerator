package com.exemplo.cebolao.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
        context.dataStore.edit { preferences ->
            preferences[THEME_PREFERENCE_KEY] = theme
        }
    }

    fun getThemePreference(): Flow<String> = flow {
        try {
            context.dataStore.data.map { preferences ->
                emit(preferences[THEME_PREFERENCE_KEY] ?: "Padrão do Sistema")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit("Padrão do Sistema")
        }
    }

}