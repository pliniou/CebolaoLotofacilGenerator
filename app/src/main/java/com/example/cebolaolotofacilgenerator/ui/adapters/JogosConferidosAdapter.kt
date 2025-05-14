package com.example.cebolaolotofacilgenerator.ui.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.databinding.ItemJogoConferidoBinding
import com.example.cebolaolotofacilgenerator.ui.viewmodel.ConferenciaViewModel

/** Adaptador para exibir jogos conferidos com seus respectivos acertos */
class JogosConferidosAdapter :
        ListAdapter<
                ConferenciaViewModel.JogoConferido, JogosConferidosAdapter.JogoConferidoViewHolder>(
                JogoConferidoDiffCallback()
        ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JogoConferidoViewHolder {
        val binding =
                ItemJogoConferidoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JogoConferidoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JogoConferidoViewHolder, position: Int) {
        val jogoConferido = getItem(position)
        holder.bind(jogoConferido)
    }

    class JogoConferidoViewHolder(private val binding: ItemJogoConferidoBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(jogoConferido: ConferenciaViewModel.JogoConferido) {
            val context = binding.root.context
            val jogo = jogoConferido.jogo
            val acertos = jogoConferido.acertos

            binding.apply {
                textViewNumeros.text = jogo.numeros.joinToString(" - ")
                textViewAcertos.text = context.getString(R.string.acertos_formatado, acertos)

                // Define a cor de fundo baseada na quantidade de acertos
                val corFundo =
                        when {
                            acertos >= 15 -> R.color.acertos_15
                            acertos >= 14 -> R.color.acertos_14
                            acertos >= 13 -> R.color.acertos_13
                            acertos >= 12 -> R.color.acertos_12
                            acertos >= 11 -> R.color.acertos_11
                            else -> R.color.acertos_menor_11
                        }

                cardView.setCardBackgroundColor(
                        ColorStateList.valueOf(ContextCompat.getColor(context, corFundo))
                )

                // Exibe informações sobre o prêmio
                val premio =
                        when (acertos) {
                            15 -> context.getString(R.string.premio_15_acertos)
                            14 -> context.getString(R.string.premio_14_acertos)
                            13 -> context.getString(R.string.premio_13_acertos)
                            12 -> context.getString(R.string.premio_12_acertos)
                            11 -> context.getString(R.string.premio_11_acertos)
                            else -> context.getString(R.string.sem_premio)
                        }

                textViewPremio.text = premio

                // Exibe características do jogo
                textViewCaracteristicas.text = buildString {
                    append("P/I: ${jogo.quantidadePares}/${jogo.quantidadeImpares}")
                    append(" | Soma: ${jogo.soma}")
                    append(" | Pri: ${jogo.quantidadePrimos}")
                    append(" | Fib: ${jogo.quantidadeFibonacci}")
                }
            }
        }
    }

    class JogoConferidoDiffCallback : DiffUtil.ItemCallback<ConferenciaViewModel.JogoConferido>() {
        override fun areItemsTheSame(
                oldItem: ConferenciaViewModel.JogoConferido,
                newItem: ConferenciaViewModel.JogoConferido
        ): Boolean {
            return oldItem.jogo.id == newItem.jogo.id
        }

        override fun areContentsTheSame(
                oldItem: ConferenciaViewModel.JogoConferido,
                newItem: ConferenciaViewModel.JogoConferido
        ): Boolean {
            return oldItem == newItem
        }
    }
}
