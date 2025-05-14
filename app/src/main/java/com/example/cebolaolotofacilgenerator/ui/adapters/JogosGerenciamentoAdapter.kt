package com.example.cebolaolotofacilgenerator.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.Jogo
import com.example.cebolaolotofacilgenerator.databinding.ItemJogoGerenciamentoBinding
import java.text.SimpleDateFormat
import java.util.Locale

/** Adaptador para exibir jogos na tela de gerenciamento */
class JogosGerenciamentoAdapter(
        private val onFavoritoClick: (Jogo, Boolean) -> Unit,
        private val onExcluirClick: (Jogo) -> Unit,
        private val onJogoClick: (Jogo) -> Unit
) : ListAdapter<Jogo, JogosGerenciamentoAdapter.JogoViewHolder>(JogoDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JogoViewHolder {
        val binding =
                ItemJogoGerenciamentoBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
        return JogoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JogoViewHolder, position: Int) {
        val jogo = getItem(position)
        holder.bind(jogo)
    }

    inner class JogoViewHolder(private val binding: ItemJogoGerenciamentoBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onJogoClick(getItem(position))
                }
            }

            binding.buttonFavorito.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val jogo = getItem(position)
                    onFavoritoClick(jogo, !jogo.favorito)
                }
            }

            binding.buttonExcluir.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onExcluirClick(getItem(position))
                }
            }
        }

        fun bind(jogo: Jogo) {
            val context = binding.root.context
            binding.apply {
                textViewNumeros.text = jogo.numeros.joinToString(" - ")
                textViewData.text =
                        context.getString(
                                R.string.jogo_data_criacao,
                                dateFormat.format(jogo.dataCriacao)
                        )

                // Configura o ícone de favorito
                val iconFavoritoResId =
                        if (jogo.favorito) {
                            android.R.drawable.btn_star_big_on
                        } else {
                            android.R.drawable.btn_star_big_off
                        }
                buttonFavorito.setImageResource(iconFavoritoResId)

                // Exibe as características do jogo
                textViewCaracteristicas.text =
                        context.getString(
                                R.string.jogo_caracteristicas_compacto,
                                jogo.quantidadePares,
                                jogo.quantidadeImpares,
                                jogo.soma,
                                jogo.quantidadePrimos,
                                jogo.quantidadeFibonacci
                        )

                // Exibe informações de conferência, se aplicável
                if (jogo.acertos != null) {
                    textViewResultados.text = context.getString(R.string.jogo_acertos, jogo.acertos)
                    textViewResultados.visibility = android.view.View.VISIBLE
                } else {
                    textViewResultados.visibility = android.view.View.GONE
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
