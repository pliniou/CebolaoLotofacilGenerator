package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
 * @param mainViewModel ViewModel principal para acesso a dados compartilhados
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstrucoesScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.instrucoes_titulo)) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Seção Introdução
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_secao_introducao_titulo),
                conteudo = stringResource(R.string.instrucoes_secao_introducao_conteudo)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Seção Filtros
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_secao_filtros_titulo),
                conteudo = stringResource(R.string.instrucoes_secao_filtros_conteudo)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Seção Números Fixos
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_secao_numeros_fixos_titulo),
                conteudo = stringResource(R.string.instrucoes_secao_numeros_fixos_conteudo)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Seção Gerar Jogos
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_secao_gerar_jogos_titulo),
                conteudo = stringResource(R.string.instrucoes_secao_gerar_jogos_conteudo)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Seção Conferência
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_secao_conferencia_titulo),
                conteudo = stringResource(R.string.instrucoes_secao_conferencia_conteudo)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Seção Dicas
            InstrucaoCard(
                titulo = stringResource(R.string.instrucoes_secao_dicas_titulo),
                conteudo = stringResource(R.string.instrucoes_secao_dicas_conteudo)
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
            .fillMaxSize()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
} 