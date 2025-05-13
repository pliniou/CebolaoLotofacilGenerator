// Arquivo legado, esvaziado para evitar conflitos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun MenuScreen(navController: NavHostController) {
    val menuItems = listOf(
        Screen.Welcome, // Welcome should probably not be in the menu
        // Screen.Menu, // Don't navigate to the menu from the menu
        Screen.Filtros,
        Screen.JogosGerados,
        Screen.Favoritos,
        Screen.Settings,
    )

    Box(modifier = Modifier.fillMaxSize()) { // Using Box as a container
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
            HorizontalDivider(color = Color.Gray, thickness = 1.dp) // Using HorizontalDivider
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(menuItems) { menuItem ->
                    Text(
                        text = menuItem.title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { navController.navigate(menuItem.route)}
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
