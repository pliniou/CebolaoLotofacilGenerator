package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.Screen
import com.example.cebolaolotofacilgenerator.ui.theme.CebolaoLotofacilGeneratorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoasVindasScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) }, // Ou um título específico para Boas Vindas
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bem-vindo(a) ao Gerador Lotofácil!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Este aplicativo ajuda você a gerar jogos aleatórios para a Lotofácil, com a opção de aplicar filtros para personalizar suas combinações e aumentar suas chances.
                    Funcionalidades Principais
                    -   **Geração de Jogos com Filtros:** Criação de jogos da Lotofácil com base em aleatoriedade, refinada por um conjunto de filtros personalizáveis.
                    -   **Filtros Estatísticos Detalhados:**
                        -   Números Fixos e Excluídos
                        -   Quantidade de Pares e Ímpares
                        -   Intervalo para Soma Total das Dezenas
                        -   Quantidade de Números Primos
                        -   Quantidade de Números de Fibonacci
                        -   Quantidade de Dezenas do Miolo (vs. Moldura)
                        -   Quantidade de Múltiplos de Três
                        -   Quantidade de Dezenas Repetidas do Concurso Anterior (usuário informa as 15 dezenas do concurso anterior e define a faixa de repetição).
                        *Para uma explicação detalhada de cada filtro, consulte a tela "Instruções" dentro do aplicativo.*
                    -   **Gerenciamento de Jogos:** Salvar, visualizar, favoritar e excluir jogos gerados.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.navigate(Screen.Gerador.createRoute()) },
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Text("Gerar Meus Jogos")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BoasVindasScreenPreview() {
    CebolaoLotofacilGeneratorTheme {
        // Mock NavController para o preview, se necessário, ou pode omitir se não interagir com ele no preview.
        // val mockNavController = rememberNavController()
        // BoasVindasScreen(navController = mockNavController)
        Text("Preview BoasVindasScreen") // Placeholder simples para o preview inicial
    }
}