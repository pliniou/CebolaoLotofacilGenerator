package com.example.cebolaolotofacilgenerator.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Classe para gerenciar mensagens de Snackbar em todo o aplicativo
 */
object SnackbarManager {
    private val _mensagens = MutableSharedFlow<MensagemSnackbar>()
    val mensagens: SharedFlow<MensagemSnackbar> = _mensagens.asSharedFlow()

    /**
     * Mostra uma mensagem do tipo Snackbar
     * @param mensagem Texto a ser exibido
     * @param duracao Duração do Snackbar (curta ou longa)
     * @param acao Texto do botão de ação (opcional)
     */
    fun mostrarMensagem(
        mensagem: String,
        duracao: SnackbarDuration = SnackbarDuration.Short,
        acao: String? = null
    ) {
        val mensagemSnackbar = MensagemSnackbar(
            mensagem = mensagem,
            duracao = duracao,
            acao = acao
        )
        
        CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            _mensagens.emit(mensagemSnackbar)
        }
    }
}

/**
 * Dados da mensagem a ser exibida no Snackbar
 */
data class MensagemSnackbar(
    val mensagem: String,
    val duracao: SnackbarDuration = SnackbarDuration.Short,
    val acao: String? = null
)

/**
 * Composable para observar as mensagens do SnackbarManager e exibi-las
 * 
 * @param snackbarHostState O estado do SnackbarHost onde as mensagens serão exibidas
 */
@Composable
fun ObservarMensagensSnackbar(snackbarHostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(key1 = true) {
        SnackbarManager.mensagens.collect { mensagem ->
            scope.launch {
                if (mensagem.acao != null) {
                    val resultado = snackbarHostState.showSnackbar(
                        message = mensagem.mensagem,
                        actionLabel = mensagem.acao,
                        duration = mensagem.duracao
                    )
                    // Pode-se adicionar lógica para tratar o resultado da ação aqui se necessário
                } else {
                    snackbarHostState.showSnackbar(
                        message = mensagem.mensagem,
                        duration = mensagem.duracao
                    )
                }
            }
        }
    }
} 