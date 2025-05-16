package com.example.cebolaolotofacilgenerator

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.ui.graphics.vector.ImageVector

/** Rotas de navegação do aplicativo */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Principal : Screen("principal", "Principal", Icons.Filled.Create)
    object Gerador :
        Screen(
            route = "gerador?dezenasFixas={dezenasFixas}",
            title = "Gerador",
            icon = Icons.Filled.AddCircle
        ) {
        fun createRoute(dezenasFixas: List<Int>? = null): String {
            val dezenasArg = dezenasFixas?.joinToString(",") ?: ""
            // Se dezenasArg for vazio, não adiciona o parâmetro para evitar "dezenasFixas="
            return if (dezenasArg.isNotEmpty()) "gerador?dezenasFixas=$dezenasArg" else "gerador"
        }
    }
    object Filtros :
        Screen(
            route = "filtros?dezenasFixas={dezenasFixas}",
            title = "Filtros",
            icon = Icons.Filled.Build
        ) {
        fun createRoute(dezenasFixas: List<Int>? = null): String {
            val dezenasArg = dezenasFixas?.joinToString(",") ?: ""
            return "filtros?dezenasFixas=$dezenasArg"
        }
    }
    object Conferencia : Screen("conferencia", "Conferência", Icons.Filled.Checklist)
    object Favoritos : Screen("favoritos", "Favoritos", Icons.Filled.Favorite)
    object Resultados : Screen("resultados", "Resultados", Icons.AutoMirrored.Filled.List)
    object Settings : Screen("settings", "Configurações", Icons.Filled.Settings)
    object JogosGerados : Screen("jogos_gerados", "Jogos Gerados", Icons.Filled.Checklist)
}
