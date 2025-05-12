package com.example.cebolaolotofacilgenerator.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cebolaolotofacilgenerator.data.modelo.Resultado
import com.example.cebolaolotofacilgenerator.databinding.ItemResultadoBinding
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adaptador para exibir resultados de concursos
 */
class ResultadosAdapter(
    private val onResultadoClick: (Resultado) -> Unit
) : ListAdapter<Resultado, ResultadosAdapter.ResultadoViewHolder>(ResultadoDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultadoViewHolder {
        val binding = ItemResultadoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ResultadoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultadoViewHolder, position: Int) {
        val resultado = getItem(position)
        holder.bind(resultado)
    }

    inner class ResultadoViewHolder(
        private val binding: ItemResultadoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onResultadoClick(getItem(position))
                }
            }
        }

        fun bind(resultado: Resultado) {
            binding.apply {
                textViewConcurso.text = resultado.numeroConcurso.toString()
                textViewData.text = dateFormat.format(resultado.dataRealizacao)
                
                // Exibe os primeiros números do resultado para economizar espaço
                val primeirosNumeros = resultado.numeros.take(5)
                val textoPrimeirosNumeros = primeirosNumeros.joinToString(" - ")
                textViewNumeros.text = "$textoPrimeirosNumeros..."
            }
        }
    }

    class ResultadoDiffCallback : DiffUtil.ItemCallback<Resultado>() {
        override fun areItemsTheSame(oldItem: Resultado, newItem: Resultado): Boolean {
            return oldItem.numeroConcurso == newItem.numeroConcurso
        }

        override fun areContentsTheSame(oldItem: Resultado, newItem: Resultado): Boolean {
            return oldItem == newItem
        }
    }
}
