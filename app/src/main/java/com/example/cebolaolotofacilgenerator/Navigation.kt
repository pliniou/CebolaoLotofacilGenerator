package com.example.cebolaolotofacilgenerator

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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
    object Onboarding :
            Screen(
                    "onboarding",
                    "Bem-vindo",
                    Icons.Filled.Home
            ) // Onboarding não precisa de ícone na barra
}
