package com.example.cebolaolotofacilgenerator.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.cebolaolotofacilgenerator.data.model.ConfiguracaoFiltros
import com.google.gson.Gson

/**
 * Gerenciador de preferências do usuário usando DataStore.
 */
class PreferenciasManager(private val context: Context) {

    // Chaves para as preferências
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "configuracoes")
        
        // Configurações gerais
        val QUANTIDADE_NUMEROS = intPreferencesKey("quantidade_numeros")
        val QUANTIDADE_JOGOS = intPreferencesKey("quantidade_jogos")
        val TEMA_ESCURO = booleanPreferencesKey("tema_escuro")
        
        // Configurações de filtros
        val MIN_PARES = intPreferencesKey("min_pares")
        val MAX_PARES = intPreferencesKey("max_pares")
        val MIN_PRIMOS = intPreferencesKey("min_primos")
        val MAX_PRIMOS = intPreferencesKey("max_primos")
        val MIN_FIBONACCI = intPreferencesKey("min_fibonacci")
        val MAX_FIBONACCI = intPreferencesKey("max_fibonacci")
        val MIN_MIOLO = intPreferencesKey("min_miolo")
        val MAX_MIOLO = intPreferencesKey("max_miolo")
        val MIN_MULTIPLOS_TRES = intPreferencesKey("min_multiplos_tres")
        val MAX_MULTIPLOS_TRES = intPreferencesKey("max_multiplos_tres")
        val MIN_SOMA = intPreferencesKey("min_soma")
        val MAX_SOMA = intPreferencesKey("max_soma")
        
        // Números fixos/excluídos (armazenados como string separada por vírgulas)
        val NUMEROS_FIXOS = stringPreferencesKey("numeros_fixos")
        val NUMEROS_EXCLUIDOS = stringPreferencesKey("numeros_excluidos")

        // Salvar filtros automaticamente
        val SALVAR_FILTROS_AUTOMATICAMENTE = booleanPreferencesKey("salvar_filtros_automaticamente")

        // Chave para a configuração de filtros completa (armazenada como JSON)
        val CONFIGURACAO_FILTROS_COMPLETA = stringPreferencesKey("configuracao_filtros_completa")
    }
    
    private val gson = Gson()

    /**
     * Obtém a configuração de quantidade de números por jogo.
     */
    val quantidadeNumeros: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[QUANTIDADE_NUMEROS] ?: 15
        }
    
    /**
     * Obtém a configuração de quantidade de jogos a gerar.
     */
    val quantidadeJogos: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[QUANTIDADE_JOGOS] ?: 10
        }
    
    /**
     * Obtém a configuração de tema escuro.
     */
    val temaEscuro: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[TEMA_ESCURO] ?: false
        }
    
    /**
     * Obtém as configurações de filtro de números pares.
     */
    val filtrosPares: Flow<Pair<Int?, Int?>> = context.dataStore.data
        .map { preferences ->
            Pair(
                preferences[MIN_PARES]?.let { if (it >= 0) it else null },
                preferences[MAX_PARES]?.let { if (it >= 0) it else null }
            )
        }
    
    /**
     * Obtém as configurações de filtro de números primos.
     */
    val filtrosPrimos: Flow<Pair<Int?, Int?>> = context.dataStore.data
        .map { preferences ->
            Pair(
                preferences[MIN_PRIMOS]?.let { if (it >= 0) it else null },
                preferences[MAX_PRIMOS]?.let { if (it >= 0) it else null }
            )
        }
    
    /**
     * Obtém as configurações de filtro de números de Fibonacci.
     */
    val filtrosFibonacci: Flow<Pair<Int?, Int?>> = context.dataStore.data
        .map { preferences ->
            Pair(
                preferences[MIN_FIBONACCI]?.let { if (it >= 0) it else null },
                preferences[MAX_FIBONACCI]?.let { if (it >= 0) it else null }
            )
        }
    
    /**
     * Obtém as configurações de filtro de números do miolo.
     */
    val filtrosMiolo: Flow<Pair<Int?, Int?>> = context.dataStore.data
        .map { preferences ->
            Pair(
                preferences[MIN_MIOLO]?.let { if (it >= 0) it else null },
                preferences[MAX_MIOLO]?.let { if (it >= 0) it else null }
            )
        }
    
    /**
     * Obtém as configurações de filtro de múltiplos de 3.
     */
    val filtrosMultiplosTres: Flow<Pair<Int?, Int?>> = context.dataStore.data
        .map { preferences ->
            Pair(
                preferences[MIN_MULTIPLOS_TRES]?.let { if (it >= 0) it else null },
                preferences[MAX_MULTIPLOS_TRES]?.let { if (it >= 0) it else null }
            )
        }
    
    /**
     * Obtém as configurações de filtro de soma total.
     */
    val filtrosSoma: Flow<Pair<Int?, Int?>> = context.dataStore.data
        .map { preferences ->
            Pair(
                preferences[MIN_SOMA]?.let { if (it >= 0) it else null },
                preferences[MAX_SOMA]?.let { if (it >= 0) it else null }
            )
        }
    
    /**
     * Obtém a lista de números fixos.
     */
    val numerosFixos: Flow<List<Int>> = context.dataStore.data
        .map { preferences ->
            val numerosString = preferences[NUMEROS_FIXOS] ?: ""
            if (numerosString.isBlank()) {
                emptyList()
            } else {
                numerosString.split(",").map { it.toInt() }
            }
        }
    
    /**
     * Obtém a lista de números excluídos.
     */
    val numerosExcluidos: Flow<List<Int>> = context.dataStore.data
        .map { preferences ->
            val numerosString = preferences[NUMEROS_EXCLUIDOS] ?: ""
            if (numerosString.isBlank()) {
                emptyList()
            } else {
                numerosString.split(",").map { it.toInt() }
            }
        }
    
    /**
     * Indica se os filtros devem ser salvos automaticamente.
     */
    val salvarFiltrosAutomaticamente: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SALVAR_FILTROS_AUTOMATICAMENTE] ?: false // Padrão é false
        }

    /**
     * Define se os filtros devem ser salvos automaticamente.
     */
    suspend fun setSalvarFiltrosAutomaticamente(salvar: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SALVAR_FILTROS_AUTOMATICAMENTE] = salvar
        }
    }
    
    /**
     * Salva a configuração de quantidade de números por jogo.
     */
    suspend fun salvarQuantidadeNumeros(quantidade: Int) {
        context.dataStore.edit { preferences ->
            preferences[QUANTIDADE_NUMEROS] = quantidade
        }
    }
    
    /**
     * Salva a configuração de quantidade de jogos a gerar.
     */
    suspend fun salvarQuantidadeJogos(quantidade: Int) {
        context.dataStore.edit { preferences ->
            preferences[QUANTIDADE_JOGOS] = quantidade
        }
    }
    
    /**
     * Salva a configuração de tema escuro.
     */
    suspend fun salvarTemaEscuro(ativo: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TEMA_ESCURO] = ativo
        }
    }
    
    /**
     * Salva as configurações de filtro de números pares.
     */
    suspend fun salvarFiltrosPares(minimo: Int?, maximo: Int?) {
        context.dataStore.edit { preferences ->
            preferences[MIN_PARES] = minimo ?: -1
            preferences[MAX_PARES] = maximo ?: -1
        }
    }
    
    /**
     * Salva as configurações de filtro de números primos.
     */
    suspend fun salvarFiltrosPrimos(minimo: Int?, maximo: Int?) {
        context.dataStore.edit { preferences ->
            preferences[MIN_PRIMOS] = minimo ?: -1
            preferences[MAX_PRIMOS] = maximo ?: -1
        }
    }
    
    /**
     * Salva as configurações de filtro de números de Fibonacci.
     */
    suspend fun salvarFiltrosFibonacci(minimo: Int?, maximo: Int?) {
        context.dataStore.edit { preferences ->
            preferences[MIN_FIBONACCI] = minimo ?: -1
            preferences[MAX_FIBONACCI] = maximo ?: -1
        }
    }
    
    /**
     * Salva as configurações de filtro de números do miolo.
     */
    suspend fun salvarFiltrosMiolo(minimo: Int?, maximo: Int?) {
        context.dataStore.edit { preferences ->
            preferences[MIN_MIOLO] = minimo ?: -1
            preferences[MAX_MIOLO] = maximo ?: -1
        }
    }
    
    /**
     * Salva as configurações de filtro de múltiplos de 3.
     */
    suspend fun salvarFiltrosMultiplosTres(minimo: Int?, maximo: Int?) {
        context.dataStore.edit { preferences ->
            preferences[MIN_MULTIPLOS_TRES] = minimo ?: -1
            preferences[MAX_MULTIPLOS_TRES] = maximo ?: -1
        }
    }
    
    /**
     * Salva as configurações de filtro de soma total.
     */
    suspend fun salvarFiltrosSoma(minimo: Int?, maximo: Int?) {
        context.dataStore.edit { preferences ->
            preferences[MIN_SOMA] = minimo ?: -1
            preferences[MAX_SOMA] = maximo ?: -1
        }
    }
    
    /**
     * Salva a lista de números fixos.
     */
    suspend fun salvarNumerosFixos(numeros: List<Int>) {
        context.dataStore.edit { preferences ->
            val numerosString = numeros.joinToString(",")
            preferences[NUMEROS_FIXOS] = numerosString
        }
    }
    
    /**
     * Salva a lista de números excluídos.
     */
    suspend fun salvarNumerosExcluidos(numeros: List<Int>) {
        context.dataStore.edit { preferences ->
            val numerosString = numeros.joinToString(",")
            preferences[NUMEROS_EXCLUIDOS] = numerosString
        }
    }
    
    /**
     * Adiciona ou remove um número da lista de números fixos.
     * @return true se o número foi adicionado, false se foi removido
     */
    suspend fun toggleNumeroFixo(numero: Int): Boolean {
        var resultado = false
        
        context.dataStore.edit { preferences ->
            val numerosString = preferences[NUMEROS_FIXOS] ?: ""
            val numeros = if (numerosString.isBlank()) {
                mutableListOf()
            } else {
                numerosString.split(",").map { it.toInt() }.toMutableList()
            }
            
            if (numero in numeros) {
                numeros.remove(numero)
                resultado = false
            } else {
                numeros.add(numero)
                resultado = true
            }
            
            preferences[NUMEROS_FIXOS] = numeros.joinToString(",")
        }
        
        return resultado
    }
    
    /**
     * Adiciona ou remove um número da lista de números excluídos.
     * @return true se o número foi adicionado, false se foi removido
     */
    suspend fun toggleNumeroExcluido(numero: Int): Boolean {
        var resultado = false
        
        context.dataStore.edit { preferences ->
            val numerosString = preferences[NUMEROS_EXCLUIDOS] ?: ""
            val numeros = if (numerosString.isBlank()) {
                mutableListOf()
            } else {
                numerosString.split(",").map { it.toInt() }.toMutableList()
            }
            
            if (numero in numeros) {
                numeros.remove(numero)
                resultado = false
            } else {
                numeros.add(numero)
                resultado = true
            }
            
            preferences[NUMEROS_EXCLUIDOS] = numeros.joinToString(",")
        }
        
        return resultado
    }
    
    /**
     * Reseta todas as configurações para os valores padrão.
     */
    suspend fun resetarConfiguracoes() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * Salva a configuração de filtros completa como uma string JSON.
     */
    suspend fun salvarConfiguracaoFiltros(config: ConfiguracaoFiltros) {
        val jsonConfig = gson.toJson(config)
        context.dataStore.edit { preferences ->
            preferences[CONFIGURACAO_FILTROS_COMPLETA] = jsonConfig
        }
    }

    /**
     * Carrega a configuração de filtros completa a partir de uma string JSON.
     * Retorna null se não houver configuração salva.
     */
    val configuracaoFiltros: Flow<ConfiguracaoFiltros?> = context.dataStore.data
        .map { preferences ->
            val jsonConfig = preferences[CONFIGURACAO_FILTROS_COMPLETA]
            if (jsonConfig != null) {
                try {
                    gson.fromJson(jsonConfig, ConfiguracaoFiltros::class.java)
                } catch (e: Exception) {
                    // Em caso de erro na desserialização, retorna null
                    null
                }
            } else {
                null
            }
        }
}
