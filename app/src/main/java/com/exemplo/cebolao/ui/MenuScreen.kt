package com.exemplo.cebolao.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.exemplo.cebolao.navigation.Screen
import com.exemplo.cebolao.Screen




@Composable
fun MenuScreen(navController: NavHostController) {
    val menuItems = listOf(
        Screen("Bem-vindo", "welcome"),
        Screen("Menu", "menu"),
        Screen("Filtros", "filtros"),
        Screen("Jogos Gerados", "jogosGerados"),
        Screen("Favoritos", "favoritos"),
        Screen("Configurações", "settings"),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Menu",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
            HorizontalDivider(color = Color.Gray, thickness = 1.dp)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(menuItems) { menuItem ->
                    Text(text = menuItem.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { navController.navigate(menuItem.route)}
                            .padding(8.dp)
                    )
                    HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                }
            }
        }
    }
}