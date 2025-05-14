package com.example.cebolaolotofacilgenerator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cebolaolotofacilgenerator.R
import com.example.cebolaolotofacilgenerator.data.model.ConfiguracaoFiltros
import com.example.cebolaolotofacilgenerator.databinding.FragmentFiltrosBinding
import com.example.cebolaolotofacilgenerator.viewmodel.FiltrosViewModel
import com.google.android.material.slider.RangeSlider

class FiltrosFragment : Fragment() {

        private var _binding: FragmentFiltrosBinding? = null
        private val binding
                get() = _binding!!
        private val viewModel: FiltrosViewModel by viewModels()

        override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
        ): View {
                _binding = FragmentFiltrosBinding.inflate(inflater, container, false)
                return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                setupViews()
                setupListeners()
                observeViewModel()
        }

        private fun setupViews() {
                // Configuração dos controles de filtro
                binding.apply {
                        // Configurar ranges de números
                        rangeSliderPares.addOnChangeListener {
                                slider: RangeSlider,
                                value: Float,
                                fromUser: Boolean ->
                                viewModel.setQuantidadePares(value.toInt())
                        }

                        rangeSliderImpares.addOnChangeListener {
                                slider: RangeSlider,
                                value: Float,
                                fromUser: Boolean ->
                                viewModel.setQuantidadeImpares(value.toInt())
                        }

                        // Configurar switches
                        switchPrimos.setOnCheckedChangeListener { buttonView, isChecked: Boolean ->
                                viewModel.setFiltrarPrimos(isChecked)
                        }

                        switchFibonacci.setOnCheckedChangeListener { buttonView, isChecked: Boolean
                                ->
                                viewModel.setFiltrarFibonacci(isChecked)
                        }

                        // Configurar botão de aplicar
                        buttonAplicarFiltros.setOnClickListener { viewModel.aplicarFiltros() }
                }
        }

        private fun observeViewModel() {
                viewModel.filtrosAplicados.observe(viewLifecycleOwner) {
                        filtros: ConfiguracaoFiltros? ->
                        // Atualizar UI com os filtros aplicados
                        filtros?.let { f ->
                                binding.apply {
                                        rangeSliderPares.value = f.quantidadePares.toFloat()
                                        rangeSliderImpares.value = f.quantidadeImpares.toFloat()
                                        switchPrimos.isChecked = f.filtrarPrimos
                                        switchFibonacci.isChecked = f.filtrarFibonacci
                                }
                                atualizarInterface(f)
                        }
                }

                viewModel.configuracaoAtual.observe(viewLifecycleOwner) {
                        config: ConfiguracaoFiltros? ->
                        config?.let { atualizarInterface(it) }
                }

                viewModel.mensagem.observe(viewLifecycleOwner) { mensagem: String? ->
                        mensagem?.let {
                                if (it.isNotEmpty()) {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                        viewModel.limparMensagem()
                                }
                        }
                }
        }

        private fun setupListeners() {
                binding.buttonAplicarFiltros.setOnClickListener {
                        val config = viewModel.construirConfiguracaoAtual()
                        viewModel.salvarConfiguracaoFiltros(config)
                }

                binding.buttonSalvarFiltros.setOnClickListener {
                        val config = viewModel.construirConfiguracaoAtual()
                        viewModel.salvarConfiguracaoFiltros(config)
                }

                binding.buttonResetarFiltros.setOnClickListener {
                        viewModel.resetarConfiguracaoFiltros()
                }

                setupSwitchListeners()
                setupRangeSliderListeners()
        }

        private fun setupSwitchListeners() {
                binding.switchParesImpares.setOnCheckedChangeListener { _, isChecked: Boolean ->
                        binding.sliderParesImpares.isEnabled = isChecked
                        if (binding.switchParesImpares.isPressed) {
                                viewModel.setFiltroParesImpares(isChecked)
                        }
                }
                binding.switchSomaTotal.setOnCheckedChangeListener { _, isChecked: Boolean ->
                        binding.sliderSomaTotal.isEnabled = isChecked
                        if (binding.switchSomaTotal.isPressed) {
                                viewModel.setFiltroSomaTotal(isChecked)
                        }
                }
                binding.switchPrimos.setOnCheckedChangeListener { _, isChecked: Boolean ->
                        binding.sliderPrimos.isEnabled = isChecked
                        if (binding.switchPrimos.isPressed) {
                                viewModel.setFiltroPrimos(isChecked)
                        }
                }
                binding.switchFibonacci.setOnCheckedChangeListener { _, isChecked: Boolean ->
                        binding.sliderFibonacci.isEnabled = isChecked
                        if (binding.switchFibonacci.isPressed) {
                                viewModel.setFiltroFibonacci(isChecked)
                        }
                }
                binding.switchMioloMoldura.setOnCheckedChangeListener { _, isChecked: Boolean ->
                        binding.sliderMioloMoldura.isEnabled = isChecked
                        if (binding.switchMioloMoldura.isPressed) {
                                viewModel.setFiltroMioloMoldura(isChecked)
                        }
                }
                binding.switchMultiplosTres.setOnCheckedChangeListener { _, isChecked: Boolean ->
                        binding.sliderMultiplosTres.isEnabled = isChecked
                        if (binding.switchMultiplosTres.isPressed) {
                                viewModel.setFiltroMultiplosDeTres(isChecked)
                        }
                }
        }

        private fun setupRangeSliderListeners() {
                binding.sliderParesImpares.addOnChangeListener { slider, _, fromUser ->
                        if (fromUser) {
                                viewModel.setMinMaxImpares(
                                        slider.values[0].toInt(),
                                        slider.values[1].toInt()
                                )
                        }
                        atualizarTextoSLeia(
                                binding.textViewValorParesImpares,
                                R.string.valor_filtro_pares_impares,
                                slider.values,
                                binding.sliderParesImpares.isEnabled
                        )
                }
                binding.sliderSomaTotal.addOnChangeListener { slider, _, fromUser ->
                        if (fromUser) {
                                viewModel.setMinMaxSoma(
                                        slider.values[0].toInt(),
                                        slider.values[1].toInt()
                                )
                        }
                        atualizarTextoSLeia(
                                binding.textViewValorSomaTotal,
                                R.string.valor_filtro_soma_total,
                                slider.values,
                                binding.sliderSomaTotal.isEnabled
                        )
                }
                binding.sliderPrimos.addOnChangeListener { slider, _, fromUser ->
                        if (fromUser) {
                                viewModel.setMinMaxPrimos(
                                        slider.values[0].toInt(),
                                        slider.values[1].toInt()
                                )
                        }
                        atualizarTextoSLeia(
                                binding.textViewValorPrimos,
                                R.string.valor_filtro_primos,
                                slider.values,
                                binding.sliderPrimos.isEnabled
                        )
                }
                binding.sliderFibonacci.addOnChangeListener { slider, _, fromUser ->
                        if (fromUser) {
                                viewModel.setMinMaxFibonacci(
                                        slider.values[0].toInt(),
                                        slider.values[1].toInt()
                                )
                        }
                        atualizarTextoSLeia(
                                binding.textViewValorFibonacci,
                                R.string.valor_filtro_fibonacci,
                                slider.values,
                                binding.sliderFibonacci.isEnabled
                        )
                }
                binding.sliderMioloMoldura.addOnChangeListener { slider, _, fromUser ->
                        if (fromUser) {
                                viewModel.setMinMaxMiolo(
                                        slider.values[0].toInt(),
                                        slider.values[1].toInt()
                                )
                        }
                        atualizarTextoSLeia(
                                binding.textViewValorMioloMoldura,
                                R.string.valor_filtro_miolo_moldura,
                                slider.values,
                                binding.sliderMioloMoldura.isEnabled
                        )
                }
                binding.sliderMultiplosTres.addOnChangeListener { slider, _, fromUser ->
                        if (fromUser) {
                                viewModel.setMinMaxMultiplos(
                                        slider.values[0].toInt(),
                                        slider.values[1].toInt()
                                )
                        }
                        atualizarTextoSLeia(
                                binding.textViewValorMultiplosTres,
                                R.string.valor_filtro_multiplos_tres,
                                slider.values,
                                binding.sliderMultiplosTres.isEnabled
                        )
                }
        }

        private fun atualizarInterface(config: ConfiguracaoFiltros) {
                binding.switchParesImpares.isChecked = config.filtroParesImpares
                binding.sliderParesImpares.isEnabled = config.filtroParesImpares

                binding.switchSomaTotal.isChecked = config.filtroSomaTotal
                binding.sliderSomaTotal.isEnabled = config.filtroSomaTotal

                binding.switchPrimos.isChecked = config.filtroPrimos
                binding.sliderPrimos.isEnabled = config.filtroPrimos

                binding.switchFibonacci.isChecked = config.filtroFibonacci
                binding.sliderFibonacci.isEnabled = config.filtroFibonacci

                binding.switchMioloMoldura.isChecked = config.filtroMioloMoldura
                binding.sliderMioloMoldura.isEnabled = config.filtroMioloMoldura

                binding.switchMultiplosTres.isChecked = config.filtroMultiplosDeTres
                binding.sliderMultiplosTres.isEnabled = config.filtroMultiplosDeTres

                configurarSlider(
                        binding.sliderParesImpares,
                        config.minImpares.toFloat(),
                        config.maxImpares.toFloat(),
                        listOf(config.atualMinImpares.toFloat(), config.atualMaxImpares.toFloat())
                )
                configurarSlider(
                        binding.sliderSomaTotal,
                        config.minSoma.toFloat(),
                        config.maxSoma.toFloat(),
                        listOf(config.atualMinSoma.toFloat(), config.atualMaxSoma.toFloat())
                )
                configurarSlider(
                        binding.sliderPrimos,
                        config.minPrimos.toFloat(),
                        config.maxPrimos.toFloat(),
                        listOf(config.atualMinPrimos.toFloat(), config.atualMaxPrimos.toFloat())
                )
                configurarSlider(
                        binding.sliderFibonacci,
                        config.minFibonacci.toFloat(),
                        config.maxFibonacci.toFloat(),
                        listOf(
                                config.atualMinFibonacci.toFloat(),
                                config.atualMaxFibonacci.toFloat()
                        )
                )
                configurarSlider(
                        binding.sliderMioloMoldura,
                        config.minMiolo.toFloat(),
                        config.maxMiolo.toFloat(),
                        listOf(config.atualMinMiolo.toFloat(), config.atualMaxMiolo.toFloat())
                )
                configurarSlider(
                        binding.sliderMultiplosTres,
                        config.minMultiplos.toFloat(),
                        config.maxMultiplos.toFloat(),
                        listOf(
                                config.atualMinMultiplos.toFloat(),
                                config.atualMaxMultiplos.toFloat()
                        )
                )

                atualizarTodosOsTextosDeSlider()
        }

        private fun configurarSlider(
                slider: RangeSlider,
                valorMin: Float,
                valorMax: Float,
                valoresAtuais: List<Float>
        ) {
                slider.valueFrom = valorMin
                slider.valueTo = valorMax
                val vMin = valoresAtuais[0].coerceIn(valorMin, valorMax)
                val vMax = valoresAtuais[1].coerceIn(valorMin, valorMax)
                if (vMin <= vMax) {
                        slider.values = listOf(vMin, vMax)
                } else {
                        slider.values = listOf(valorMin, valorMin)
                }
        }

        private fun atualizarTextoSLeia(
                textView: TextView,
                stringRes: Int,
                values: List<Float>,
                isEnabled: Boolean
        ) {
                if (isEnabled) {
                        if (values.size == 2) {
                                textView.text =
                                        getString(stringRes, values[0].toInt(), values[1].toInt())
                        } else if (values.isNotEmpty()) {
                                textView.text =
                                        getString(stringRes, values[0].toInt(), values[0].toInt())
                        }
                } else {
                        textView.text = getString(R.string.na_aplicado)
                }
        }

        private fun atualizarTodosOsTextosDeSlider() {
                atualizarTextoSLeia(
                        binding.textViewValorParesImpares,
                        R.string.valor_filtro_pares_impares,
                        binding.sliderParesImpares.values,
                        binding.sliderParesImpares.isEnabled
                )
                atualizarTextoSLeia(
                        binding.textViewValorSomaTotal,
                        R.string.valor_filtro_soma_total,
                        binding.sliderSomaTotal.values,
                        binding.sliderSomaTotal.isEnabled
                )
                atualizarTextoSLeia(
                        binding.textViewValorPrimos,
                        R.string.valor_filtro_primos,
                        binding.sliderPrimos.values,
                        binding.sliderPrimos.isEnabled
                )
                atualizarTextoSLeia(
                        binding.textViewValorFibonacci,
                        R.string.valor_filtro_fibonacci,
                        binding.sliderFibonacci.values,
                        binding.sliderFibonacci.isEnabled
                )
                atualizarTextoSLeia(
                        binding.textViewValorMioloMoldura,
                        R.string.valor_filtro_miolo_moldura,
                        binding.sliderMioloMoldura.values,
                        binding.sliderMioloMoldura.isEnabled
                )
                atualizarTextoSLeia(
                        binding.textViewValorMultiplosTres,
                        R.string.valor_filtro_multiplos_tres,
                        binding.sliderMultiplosTres.values,
                        binding.sliderMultiplosTres.isEnabled
                )
        }

        override fun onDestroyView() {
                super.onDestroyView()
                _binding = null
        }
}
