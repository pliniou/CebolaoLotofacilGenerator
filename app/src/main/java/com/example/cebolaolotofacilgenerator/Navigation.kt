package com.example.cebolaolotofacilgenerator

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

/** Rotas de navegação do aplicativo */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object BoasVindas : Screen("boas_vindas", "Bem-vindo(a)", Icons.Filled.Home)
    object Gerador :
        Screen(
            route = "gerador?dezenasFixas={dezenasFixas}",
            title = "Gerar Jogos",
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
    object Favoritos : Screen("favoritos", "Favoritos", Icons.Filled.Favorite)
    object Settings : Screen("settings", "Ajustes", Icons.Filled.Settings)
    object JogosGerados : Screen("jogos_gerados", "Jogos Gerados", Icons.Filled.Checklist)
    object Instrucoes : Screen("instrucoes", "Instruções", Icons.Filled.Info)
}
