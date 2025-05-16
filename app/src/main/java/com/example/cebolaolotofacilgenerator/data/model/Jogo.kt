package com.example.cebolaolotofacilgenerator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entidade que representa um jogo da Lotofácil, armazenado no banco de dados.
 *
 * @property id Identificador único do jogo (gerado automaticamente).
 * @property numeros Lista das dezenas que compõem o jogo.
 * @property dataCriacao Data em que o jogo foi gerado/criado.
 * @property favorito Indica se o jogo foi marcado como favorito pelo usuário.
 * @property acertos Número de acertos do jogo quando conferido com um resultado. Nulo se não conferido.
 * @property concursoConferido ID do concurso ([Resultado.id]) com o qual este jogo foi conferido. Nulo se não conferido.
 * @property dezenasSorteadasConferencia Dezenas sorteadas no concurso utilizado para conferência.
 * @property quantidadePares Quantidade de números pares no jogo.
 * @property quantidadeImpares Quantidade de números ímpares no jogo.
 * @property quantidadePrimos Quantidade de números primos no jogo.
 * @property quantidadeFibonacci Quantidade de números da sequência de Fibonacci no jogo.
 * @property quantidadeMiolo Quantidade de números pertencentes ao "miolo" do volante (7 a 19).
 * @property quantidadeMoldura Quantidade de números pertencentes à "moldura" do volante.
 * @property quantidadeMultiplosDeTres Quantidade de números múltiplos de 3 no jogo.
 * @property soma Soma de todas as dezenas do jogo.
 */
@Entity(tableName = "jogos")
data class Jogo(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,

        // Alterado para List<Int>
        val numeros: List<Int>,

        // Data de criação do jogo
        val dataCriacao: Date,

        // Flag para marcar o jogo como favorito
        var favorito: Boolean = false, // Use var se for modificar diretamente

        // Resultado da conferência (número de acertos) - null se não foi conferido
        val acertos: Int? = null,

        // ID do concurso com o qual o jogo foi conferido - null se não foi conferido
        val concursoConferido: Long? = null,

        // Dezenas sorteadas no concurso utilizado para conferência
        val dezenasSorteadasConferencia: List<Int>? = null,

        // Características estatísticas do jogo
        val quantidadePares: Int, // Renomeado para consistência
        val quantidadeImpares: Int, // Renomeado para consistência
        val quantidadePrimos: Int, // Renomeado para consistência
        val quantidadeFibonacci: Int, // Renomeado para consistência
        val quantidadeMiolo: Int, // Renomeado para consistência
        val quantidadeMoldura: Int, // Renomeado para consistência (era numerosBorda)
        val quantidadeMultiplosDeTres: Int, // Renomeado para consistência
        val soma: Int // Renomeado para consistência (era somaTotal)
) {
    /**
     * Retorna as dezenas do jogo formatadas como uma string, separadas por " - ".
     * Exemplo: "01 - 02 - 05 - ..."
     *
     * @return String formatada das dezenas do jogo.
     */
    fun getNumerosFormatados(): String {
        return numeros.joinToString(" - ") // Adaptação simples
    }

    companion object {
        /**
         * Cria uma nova instância de [Jogo] a partir de uma lista de dezenas.
         * As características estatísticas (pares, ímpares, soma, etc.) são calculadas automaticamente.
         * A lista de dezenas é ordenada antes da criação do jogo.
         *
         * @param numerosList A lista de dezenas para o novo jogo.
         * @return Uma nova instância de [Jogo].
         */
        fun fromList(numerosList: List<Int>): Jogo {
            val numerosOrdenados = numerosList.sorted() // Garante a ordem

            // Calcula as características estatísticas
            val pares = numerosOrdenados.count { it % 2 == 0 }
            val impares = numerosOrdenados.size - pares
            val primos = numerosOrdenados.count { isPrimo(it) }
            val fibonacci = numerosOrdenados.count { isFibonacci(it) }
            val miolo = numerosOrdenados.count { isMiolo(it) }
            val moldura = numerosOrdenados.size - miolo // Calcula a moldura
            val multiplosTres = numerosOrdenados.count { it % 3 == 0 }
            val soma = numerosOrdenados.sum()

            return Jogo(
                    numeros = numerosOrdenados, // Usa a lista ordenada
                    dataCriacao = Date(),
                    quantidadePares = pares,
                    quantidadeImpares = impares,
                    quantidadePrimos = primos,
                    quantidadeFibonacci = fibonacci,
                    quantidadeMiolo = miolo,
                    quantidadeMoldura = moldura, // Usa a moldura calculada
                    quantidadeMultiplosDeTres = multiplosTres,
                    soma = soma, // Usa o nome consistente
                    dezenasSorteadasConferencia = null, // Inicializa como nulo
                    // 'favorito', 'acertos', 'concursoConferido' terão valores padrão
                    )
        }

        // Verifica se um número é primo
        private fun isPrimo(num: Int): Boolean {
            if (num <= 1) return false
            if (num <= 3) return true
            if (num % 2 == 0 || num % 3 == 0) return false

            var i = 5
            while (i * i <= num) {
                if (num % i == 0 || num % (i + 2) == 0) return false
                i += 6
            }
            return true
        }

        // Verifica se um número pertence à sequência de Fibonacci (até o 25)
        private fun isFibonacci(num: Int): Boolean {
            return num in setOf(1, 2, 3, 5, 8, 13, 21)
        }

        // Verifica se um número pertence ao "miolo"
        private fun isMiolo(num: Int): Boolean {
            return num in 7..19
        }
    }
}
