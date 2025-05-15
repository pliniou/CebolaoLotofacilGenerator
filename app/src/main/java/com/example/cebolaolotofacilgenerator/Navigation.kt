package com.example.cebolaolotofacilgenerator

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/** Rotas de navegação do aplicativo */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
        object Home : Screen("home", "Início", Icons.Filled.Home)
        object Favoritos : Screen("favoritos", "Favoritos", Icons.Filled.Favorite)
        object Resultados : Screen("resultados", "Resultados", Icons.AutoMirrored.Filled.List)
        object Settings : Screen("settings", "Configurações", Icons.Filled.Settings)
        object Gerador :
                Screen(
                        route = "gerador?dezenasFixas={dezenasFixas}",
                        title = "Gerar Jogos",
                        icon = Icons.Filled.Create
                ) {
                fun createRoute(dezenasFixas: List<Int>? = null): String {
                        val dezenasArg = dezenasFixas?.joinToString(",") ?: ""
                        return "gerador?dezenasFixas=$dezenasArg"
                }
        }
        object JogosGerados : Screen("jogos_gerados", "Jogos Gerados", Icons.Filled.Checklist)
}
