package com.example.cebolaolotofacilgenerator.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.databinding.ItemJogoBinding

/** Adaptador para exibir jogos gerados na tela principal */
class JogosAdapter(
        private val onFavoritoClick: (Jogo, Boolean) -> Unit,
        private val onJogoClick: (Jogo) -> Unit
) : ListAdapter<Jogo, JogosAdapter.JogoViewHolder>(JogoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JogoViewHolder {
        val binding = ItemJogoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JogoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JogoViewHolder, position: Int) {
        val jogo = getItem(position)
        holder.bind(jogo)
    }

    inner class JogoViewHolder(private val binding: ItemJogoBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onJogoClick(getItem(position))
                }
            }

            binding.iconeFavorito.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val jogo = getItem(position)
                    onFavoritoClick(jogo, !jogo.favorito)
                }
            }
        }

        fun bind(jogo: Jogo) {
            binding.apply {
                textViewNumeros.text = jogo.numeros.joinToString(" - ")

                // Configura o ícone de favorito
                val iconResId =
                        if (jogo.favorito) {
                            android.R.drawable.btn_star_big_on
                        } else {
                            android.R.drawable.btn_star_big_off
                        }
                iconeFavorito.setImageResource(iconResId)

                // Exibe as características do jogo
                textViewCaracteristicas.text = buildString {
                    append(
                            "P: ${jogo.quantidadePares} | I: ${jogo.quantidadeImpares} | Soma: ${jogo.soma}"
                    )
                    append("\nPrimos: ${jogo.quantidadePrimos} | Fib: ${jogo.quantidadeFibonacci}")
                    append("\nMiolo: ${jogo.quantidadeMiolo} | Moldura: ${jogo.quantidadeMoldura}")
                    append("\nMúlt. 3: ${jogo.quantidadeMultiplosDeTres}")
                }
            }
        }
    }

    class JogoDiffCallback : DiffUtil.ItemCallback<Jogo>() {
        override fun areItemsTheSame(oldItem: Jogo, newItem: Jogo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Jogo, newItem: Jogo): Boolean {
            return oldItem == newItem
        }
    }
}
