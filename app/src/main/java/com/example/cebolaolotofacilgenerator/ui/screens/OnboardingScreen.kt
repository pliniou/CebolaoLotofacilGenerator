package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel

// Remover NavController se não for usado diretamente pela OnboardingScreen
// import androidx.navigation.NavController

@Composable
fun OnboardingScreen(viewModel: MainViewModel, onComplete: () -> Unit) {
    Text("Tela Onboarding", modifier = Modifier.padding(32.dp))
}
