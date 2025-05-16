package com.example.cebolaolotofacilgenerator.ui.screens

// import android.content.Context // Não é mais usado diretamente aqui
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel.TemaAplicativo

/**
 * Tela de configurações do aplicativo.
 * Permite ao usuário personalizar o tema, resetar configurações e ver informações sobre o app.
 *
 * @param mainViewModel O [MainViewModel] para acessar e modificar preferências e tema.
 * @param navController O [NavController] para navegação, como voltar para a tela anterior.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(mainViewModel: MainViewModel, navController: NavController) {
    val temaAtual by mainViewModel.temaAplicativo.collectAsState()
    var showSobreDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current // Usado para o Snackbar

    if (showSobreDialog) {
        AlertDialog(
            onDismissRequest = { showSobreDialog = false },
            title = { Text(stringResource(R.string.sobre_app_titulo)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.sobre_app_versao, stringResource(R.string.app_version_name))) // Usar stringResource para versão
                    Text(stringResource(R.string.sobre_app_desenvolvido_por, "Cebola Studios"))
                    Text(stringResource(R.string.sobre_app_descricao))
                }
            },
            confirmButton = {
                TextButton(onClick = { showSobreDialog = false }) { Text(stringResource(R.string.fechar)) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tela_configuracoes_titulo)) },
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
                            contentDescription = stringResource(R.string.voltar),
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                stringResource(R.string.preferencias_aparencia_titulo),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ThemeSettingsGroup(
                temaAtual = temaAtual,
                onThemeSelected = { mainViewModel.salvarTemaAplicativo(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.acoes_app_titulo),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                ActionItem(
                    icon = Icons.Filled.Refresh,
                    title = stringResource(R.string.resetar_configuracoes_app_titulo),
                    subtitle = stringResource(R.string.resetar_configuracoes_app_subtitulo),
                    onClick = {
                        mainViewModel.preferenciasViewModel.resetarConfiguracoes()
                        mainViewModel.showSnackbar(context.getString(R.string.configuracoes_resetadas_confirmacao))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nova Seção: Configurações de Filtros
            Text(
                stringResource(R.string.configuracoes_filtros_titulo_secao),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    val salvarFiltrosAutomaticamente by mainViewModel.preferenciasViewModel.salvarFiltrosAutomaticamente.collectAsState()

                    SwitchSettingItem(
                        icon = Icons.Filled.Save,
                        title = stringResource(R.string.salvar_filtros_automaticamente_titulo),
                        subtitle = stringResource(R.string.salvar_filtros_automaticamente_subtitulo),
                        checked = salvarFiltrosAutomaticamente,
                        onCheckedChange = { newState ->
                            mainViewModel.preferenciasViewModel.setSalvarFiltrosAutomaticamente(newState)
                        }
                    )
                    ActionItem(
                        icon = Icons.Filled.Tune,
                        title = stringResource(R.string.restaurar_filtros_padrao_titulo),
                        subtitle = stringResource(R.string.restaurar_filtros_padrao_subtitulo),
                        onClick = {
                            mainViewModel.showSnackbar(context.getString(R.string.filtros_restaurados_confirmacao))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nova Seção: Gerenciamento de Dados
            Text(
                stringResource(R.string.gerenciamento_dados_titulo_secao),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            var showConfirmationDialogLimparJogos by remember { mutableStateOf(false) }
            if (showConfirmationDialogLimparJogos) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialogLimparJogos = false },
                    title = { Text(stringResource(R.string.confirmar_limpar_jogos_titulo)) },
                    text = { Text(stringResource(R.string.confirmar_limpar_jogos_mensagem)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                mainViewModel.showSnackbar(context.getString(R.string.todos_jogos_excluidos_confirmacao))
                                showConfirmationDialogLimparJogos = false
                            }
                        ) { Text(stringResource(R.string.confirmar)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmationDialogLimparJogos = false }) {
                            Text(stringResource(R.string.cancelar))
                        }
                    }
                )
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                ActionItem(
                    icon = Icons.Filled.DeleteForever,
                    title = stringResource(R.string.limpar_jogos_salvos_titulo),
                    subtitle = stringResource(R.string.limpar_jogos_salvos_subtitulo),
                    onClick = { showConfirmationDialogLimparJogos = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.informacoes_titulo),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                InfoItem(
                    icon = Icons.Filled.Info,
                    title = stringResource(R.string.sobre_app_label),
                    subtitle = stringResource(R.string.sobre_app_versao_subtitulo, stringResource(R.string.app_version_name), "Cebola Studios"),
                    onClick = { showSobreDialog = true }
                )
                InfoItem(
                    icon = Icons.Filled.Favorite,
                    title = stringResource(R.string.agradecimentos_licencas_titulo),
                    subtitle = stringResource(R.string.agradecimentos_licencas_subtitulo),
                    onClick = {
                        mainViewModel.showSnackbar("Agradecimentos/Licenças em breve!")
                    }
                )
                InfoItem(
                    icon = Icons.Filled.Email,
                    title = stringResource(R.string.contato_feedback_titulo),
                    subtitle = stringResource(R.string.contato_feedback_subtitulo),
                    onClick = {
                        mainViewModel.showSnackbar("Função de contato em breve!")
                    }
                )
                InfoItem(
                    icon = Icons.Filled.StarRate,
                    title = stringResource(R.string.avaliar_app_titulo),
                    subtitle = stringResource(R.string.avaliar_app_subtitulo),
                    onClick = {
                        mainViewModel.showSnackbar("Função de avaliar na loja em breve!")
                    }
                )
            }
        }
    }
}

/**
 * Grupo de configurações para seleção do tema do aplicativo.
 *
 * @param temaAtual O [TemaAplicativo] atualmente selecionado.
 * @param onThemeSelected Lambda chamada quando um novo tema é selecionado.
 */
@Composable
fun ThemeSettingsGroup(temaAtual: TemaAplicativo, onThemeSelected: (TemaAplicativo) -> Unit) {
    val temas = TemaAplicativo.values()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                stringResource(R.string.tema_app_titulo_grupo),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            temas.forEach { tema ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onThemeSelected(tema) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (tema == temaAtual),
                        onClick = { onThemeSelected(tema) }
                    )
                    Text(
                        text =
                        when (tema) {
                            TemaAplicativo.CLARO -> stringResource(R.string.tema_claro)
                            TemaAplicativo.ESCURO -> stringResource(R.string.tema_escuro)
                            TemaAplicativo.SISTEMA -> stringResource(R.string.tema_sistema)
                            TemaAplicativo.AZUL -> stringResource(R.string.tema_azul)
                            TemaAplicativo.VERDE -> stringResource(R.string.tema_verde)
                            TemaAplicativo.LARANJA -> stringResource(R.string.tema_laranja)
                            TemaAplicativo.CIANO -> stringResource(R.string.tema_ciano)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Item de informação clicável, geralmente usado para exibir diálogos ou navegar para telas de detalhes.
 *
 * @param icon O [ImageVector] a ser exibido como ícone.
 * @param title O título do item.
 * @param subtitle O subtítulo descritivo do item.
 * @param onClick Lambda a ser executada quando o item é clicado.
 */
@Composable
fun InfoItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/**
 * Item de ação clicável, geralmente para executar uma operação.
 *
 * @param icon O [ImageVector] a ser exibido como ícone.
 * @param title O título da ação.
 * @param subtitle O subtítulo descritivo da ação.
 * @param onClick Lambda a ser executada quando o item é clicado.
 */
@Composable
fun ActionItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/**
 * Item de configuração com um Switch.
 *
 * @param icon O [ImageVector] a ser exibido como ícone.
 * @param title O título da configuração.
 * @param subtitle O subtítulo descritivo da configuração.
 * @param checked O estado atual do Switch.
 * @param onCheckedChange Lambda chamada quando o estado do Switch muda.
 */
@Composable
fun SwitchSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
