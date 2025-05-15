package com.example.cebolaolotofacilgenerator.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.Resultado
import com.example.cebolaolotofacilgenerator.databinding.ItemResultadoBinding
import java.text.SimpleDateFormat
import java.util.Locale

/** Adaptador para exibir resultados de concursos */
class ResultadosAdapter(private val onResultadoClick: (Resultado) -> Unit) :
        ListAdapter<Resultado, ResultadosAdapter.ResultadoViewHolder>(ResultadoDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultadoViewHolder {
        val binding =
                ItemResultadoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultadoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultadoViewHolder, position: Int) {
        val resultado = getItem(position)
        holder.bind(resultado)
    }

    inner class ResultadoViewHolder(private val binding: ItemResultadoBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onResultadoClick(getItem(position))
                }
            }
        }

        fun bind(resultado: Resultado) {
            val context = binding.root.context
            binding.apply {
                textViewConcurso.text =
                        context.getString(R.string.resultado_concurso, resultado.id.toString())
                textViewData.text =
                        context.getString(
                                R.string.resultado_data,
                                dateFormat.format(resultado.dataSorteio)
                        )

                // Exibe os primeiros números do resultado para economizar espaço
                val primeirosNumeros = resultado.numeros.take(5)
                val textoPrimeirosNumeros = primeirosNumeros.joinToString(" - ")
                textViewNumeros.text =
                        context.getString(
                                R.string.resultado_numeros_parciais,
                                textoPrimeirosNumeros
                        )
            }
        }
    }

    class ResultadoDiffCallback : DiffUtil.ItemCallback<Resultado>() {
        override fun areItemsTheSame(oldItem: Resultado, newItem: Resultado): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Resultado, newItem: Resultado): Boolean {
            return oldItem == newItem
        }
    }
}
