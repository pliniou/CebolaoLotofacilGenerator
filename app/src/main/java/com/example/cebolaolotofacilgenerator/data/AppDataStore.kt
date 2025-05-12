package com.example.cebolaolotofacilgenerator.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_settings")

class AppDataStore(private val context: Context) {

    private val FIRST_RUN_COMPLETED = booleanPreferencesKey("first_run_completed")

    val firstRunCompleted: Flow<Boolean> =
            context.dataStore.data.map { preferences -> preferences[FIRST_RUN_COMPLETED] ?: false }

    suspend fun setFirstRunCompleted() {
        context.dataStore.edit { preferences -> preferences[FIRST_RUN_COMPLETED] = true }
    }
}
