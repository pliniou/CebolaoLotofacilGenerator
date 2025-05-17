package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel

/**
 * Tela de Instruções que exibe informações sobre como usar o aplicativo
 *
 * @param navController Controlador de navegação para voltar à tela anterior
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstrucoesScreen(
    navController: NavHostController,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Instruções") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.voltar)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Seção Conceito e Geração Aleatória
            InstrucaoCard(
                titulo = "Conceito Básico",
                conteudo = "Este aplicativo ajuda a gerar jogos para a Lotofácil utilizando diversos filtros para refinar suas escolhas. Explore as configurações para personalizar os jogos gerados."
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Filtro: Números Fixos
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_filtro_numeros_fixos_titulo),
                conteudo = stringResource(R.string.instrucoes_filtro_numeros_fixos_conteudo)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Filtro: Números Excluídos
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_filtro_numeros_excluidos_titulo),
                conteudo = stringResource(R.string.instrucoes_filtro_numeros_excluidos_conteudo)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Filtro: Pares e Ímpares
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_filtro_pares_impares_titulo),
                conteudo = stringResource(R.string.instrucoes_filtro_pares_impares_conteudo)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Filtro: Soma Total das Dezenas
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_filtro_soma_total_titulo),
                conteudo = stringResource(R.string.instrucoes_filtro_soma_total_conteudo)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Filtro: Números Primos
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_filtro_numeros_primos_titulo),
                conteudo = stringResource(R.string.instrucoes_filtro_numeros_primos_conteudo)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Filtro: Números de Fibonacci
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_filtro_fibonacci_titulo),
                conteudo = stringResource(R.string.instrucoes_filtro_fibonacci_conteudo)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Filtro: Miolo e Moldura
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_filtro_miolo_moldura_titulo),
                conteudo = stringResource(R.string.instrucoes_filtro_miolo_moldura_conteudo)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Filtro: Múltiplos de Três
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_filtro_multiplos_de_tres_titulo),
                conteudo = stringResource(R.string.instrucoes_filtro_multiplos_de_tres_conteudo)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Filtro: Repetição do Concurso Anterior
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_filtro_repeticao_concurso_anterior_titulo),
                conteudo = stringResource(R.string.instrucoes_filtro_repeticao_concurso_anterior_conteudo)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Observação Importante
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_observacao_importante_titulo),
                conteudo = stringResource(R.string.instrucoes_observacao_importante_conteudo)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Card para exibir uma seção de instruções
 *
 * @param titulo Título da seção de instruções
 * @param conteudo Conteúdo explicativo da seção
 */
@Composable
fun InstrucaoCard(
    titulo: String,
    conteudo: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = conteudo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}