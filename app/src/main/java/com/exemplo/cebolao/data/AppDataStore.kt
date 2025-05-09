package com.exemplo.cebolao.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppDataStore(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "settings")
    
    val savedNumbers: Flow<List<Int>> = context.dataStore.data
        .map { preferences ->
            preferences[Preferences.Key<Set<String>>("SAVED_NUMBERS")]
                ?.map { it.toInt() }?.toList() ?: emptyList()
        }
    
    suspend fun saveNumbers(numbers: List<Int>) {
        context.dataStore.edit { preferences ->
            preferences[Preferences.Key<Set<String>>("SAVED_NUMBERS")] =
                numbers.map { it.toString() }.toSet()
        }
    }
    fun getThemePreference(): Flow<String> {
        return context.dataStore.data.map { preferences ->
 preferences[stringPreferencesKey("theme_preference")] ?: "system"
        }
    }
}