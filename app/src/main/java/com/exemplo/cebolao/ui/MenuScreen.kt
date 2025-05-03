package com.exemplo.cebolao.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun MenuScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Button(onClick = { /*TODO*/ }, modifier = Modifier.padding(8.dp)) {
            Text("Filtros Selecionáveis")
        }
        Button(onClick = { /*TODO*/ }, modifier = Modifier.padding(8.dp)) {
            Text("Jogos Gerados")
        }
        Button(onClick = { /*TODO*/ }, modifier = Modifier.padding(8.dp)) {
            Text("Jogos Favoritos")
        }
        Button(onClick = { /*TODO*/ }, modifier = Modifier.padding(8.dp)) {
            Text("Configurações")
        }
    }
}