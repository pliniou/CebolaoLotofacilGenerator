package com.exemplo.cebolao.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit



class AppDataStore(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "settings")

    suspend fun saveString(key: String, value: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }
}
package com.exemplo.cebolao.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit



class AppDataStore(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "settings")

    suspend fun saveString(key: String, value: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }
}