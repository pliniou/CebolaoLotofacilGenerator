package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel, navController: NavController) {
    Text("Tela Configurações", modifier = Modifier.padding(32.dp))
}
