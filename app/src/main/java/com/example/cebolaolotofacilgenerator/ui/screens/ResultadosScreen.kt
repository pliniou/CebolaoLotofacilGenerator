package com.example.cebolaolotofacilgenerator.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.Screen
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadosScreen(viewModel: MainViewModel, navController: NavController) {
        val ultimoResultado by viewModel.ultimoResultado.collectAsState()
        val dezenasSelecionadas by viewModel.dezenasSelecionadasUltimoResultado.collectAsState()

        // Carrega as dezenas do último resultado quando ele estiver disponível ou mudar
        LaunchedEffect(ultimoResultado) {
                viewModel.carregarDezenasDoUltimoResultado(ultimoResultado?.numeros)
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Último Resultado e Seleção") },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                titleContentColor =
                                                        MaterialTheme.colorScheme.onPrimary,
                                                navigationIconContentColor =
                                                        MaterialTheme.colorScheme.onPrimary
                                        ),
                                navigationIcon = {
                                        IconButton(onClick = { navController.popBackStack() }) {
                                                Icon(
                                                        Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = "Voltar"
                                                )
                                        }
                                }
                        )
                }
        ) { paddingValues ->
                Column(
                        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        ultimoResultado?.let { resultado ->
                                Text(
                                        text = "Concurso: ${resultado.concurso}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                        text =
                                                "Data: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(resultado.dataSorteio)}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                )

                                Text(
                                        "Dezenas Sorteadas:",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                )
                                // Exibe as dezenas sorteadas de forma simples (pode ser melhorado)
                                Text(
                                        text = resultado.numeros.joinToString(" - "),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 24.dp)
                                )

                                Text(
                                        "Marque as dezenas para usar no gerador:",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                )

                                DezenasGrid(
                                        todasDezenas = (1..25).toList(),
                                        dezenasSelecionadas = dezenasSelecionadas,
                                        dezenasSorteadasOriginalmente = resultado.numeros.toSet(),
                                        onDezenaClick = { dezena ->
                                                viewModel.toggleDezenaSelecionadaUltimoResultado(
                                                        dezena
                                                )
                                        }
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                Button(
                                        onClick = {
                                                // Navega para GeradorScreen passando as dezenas
                                                // selecionadas
                                                val dezenasParaGerador =
                                                        viewModel.dezenasSelecionadasUltimoResultado
                                                                .value.toList()
                                                navController.navigate(
                                                        Screen.Gerador.createRoute(
                                                                dezenasParaGerador
                                                        )
                                                )
                                        },
                                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                                ) { Text("Usar Dezenas Marcadas no Gerador") }
                        }
                                ?: run {
                                        Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Text(
                                                        text =
                                                                "Nenhum resultado anterior encontrado ou carregando...",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        textAlign = TextAlign.Center
                                                )
                                        }
                                }
                }
        }
}

@Composable
fun DezenasGrid(
        todasDezenas: List<Int>,
        dezenasSelecionadas: Set<Int>,
        dezenasSorteadasOriginalmente: Set<Int>, // Para destacar as sorteadas no concurso
        onDezenaClick: (Int) -> Unit
) {
        LazyVerticalGrid(
                columns = GridCells.Fixed(5), // 5 colunas para as 25 dezenas
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
                items(todasDezenas) { dezena ->
                        val isSelected = dezena in dezenasSelecionadas
                        val wasOriginallySorted = dezena in dezenasSorteadasOriginalmente

                        val backgroundColor =
                                when {
                                        isSelected && wasOriginallySorted ->
                                                MaterialTheme.colorScheme
                                                        .tertiaryContainer // Marcada e sorteada
                                        isSelected ->
                                                MaterialTheme.colorScheme
                                                        .primaryContainer // Marcada pelo usuário
                                        wasOriginallySorted ->
                                                MaterialTheme.colorScheme
                                                        .secondaryContainer // Apenas sorteada
                                        // originalmente
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                        val textColor =
                                when {
                                        isSelected && wasOriginallySorted ->
                                                MaterialTheme.colorScheme.onTertiaryContainer
                                        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                        wasOriginallySorted ->
                                                MaterialTheme.colorScheme.onSecondaryContainer
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                        val border =
                                if (isSelected)
                                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                else null

                        Card(
                                modifier =
                                        Modifier.aspectRatio(1f) // Para fazer os itens quadrados
                                                .clickable { onDezenaClick(dezena) },
                                shape = CircleShape, // Ou RoundedCornerShape(8.dp)
                                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                                border = border,
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                                Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text =
                                                        "%02d".format(
                                                                dezena
                                                        ), // Formato com dois dígitos
                                                color = textColor,
                                                fontSize = 16.sp, // Ajuste o tamanho conforme
                                                // necessário
                                                fontWeight = FontWeight.Medium
                                        )
                                }
                        }
                }
        }
}
