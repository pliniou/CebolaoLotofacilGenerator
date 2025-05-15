package com.example.cebolaolotofacilgenerator.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_settings")

class AppDataStore(private val context: Context) {

    private val FIRST_RUN_COMPLETED = booleanPreferencesKey("first_run_completed")
    private val TEMA_APLICATIVO_ORDINAL = intPreferencesKey("tema_aplicativo_ordinal")
    private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")

    val firstRunCompleted: Flow<Boolean> =
            context.dataStore.data.map { preferences -> preferences[FIRST_RUN_COMPLETED] ?: false }

    suspend fun setFirstRunCompleted() {
        context.dataStore.edit { preferences -> preferences[FIRST_RUN_COMPLETED] = true }
    }

    val temaAplicativo: Flow<Int> =
            context.dataStore.data.map { preferences ->
                preferences[TEMA_APLICATIVO_ORDINAL] ?: MainViewModel.TemaAplicativo.SISTEMA.ordinal
            }

    suspend fun salvarTemaAplicativo(ordinal: Int) {
        context.dataStore.edit { preferences -> preferences[TEMA_APLICATIVO_ORDINAL] = ordinal }
    }

    val notificationsEnabled: Flow<Boolean> =
            context.dataStore.data.map { preferences -> preferences[NOTIFICATIONS_ENABLED] ?: true }

    suspend fun setNotificationsEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[NOTIFICATIONS_ENABLED] = isEnabled }
    }
}
