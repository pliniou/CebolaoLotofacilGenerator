package com.example.cebolaolotofacilgenerator.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cebolaolotofacilgenerator.R
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
                observeViewModel()
        }

        private fun setupViews() {
                // Configuração dos controles de filtro
                binding.apply {
                        // Configurar ranges de números
                        rangeSliderPares.addOnChangeListener { _, value, _ ->
                                viewModel.setQuantidadePares(value.toInt())
                        }

                        rangeSliderImpares.addOnChangeListener { _, value, _ ->
                                viewModel.setQuantidadeImpares(value.toInt())
                        }

                        // Configurar switches
                        switchPrimos.setOnCheckedChangeListener { _, isChecked ->
                                viewModel.setFiltrarPrimos(isChecked)
                        }

                        switchFibonacci.setOnCheckedChangeListener { _, isChecked ->
                                viewModel.setFiltrarFibonacci(isChecked)
                        }

                        // Configurar botão de aplicar
                        buttonAplicarFiltros.setOnClickListener { viewModel.aplicarFiltros() }
                }
        }

        private fun observeViewModel() {
                viewModel.filtrosAplicados.observe(viewLifecycleOwner) { filtros ->
                        // Atualizar UI com os filtros aplicados
                        binding.apply {
                                rangeSliderPares.value = filtros.quantidadePares.toFloat()
                                rangeSliderImpares.value = filtros.quantidadeImpares.toFloat()
                                switchPrimos.isChecked = filtros.filtrarPrimos
                                switchFibonacci.isChecked = filtros.filtrarFibonacci
                        }
                }
        }

        private fun setupObservers() {}

        private fun setupListeners() {
                // Botão para salvar as configurações
                binding.buttonSalvarFiltros.setOnClickListener {
                        Toast.makeText(
                                        requireContext(),
                                        R.string.filtros_salvos_com_sucesso,
                                        Toast.LENGTH_SHORT
                                )
                                .show()
                }

                // Botão para resetar as configurações
                binding.buttonResetarFiltros.setOnClickListener {
                        Toast.makeText(
                                        requireContext(),
                                        R.string.filtros_resetados,
                                        Toast.LENGTH_SHORT
                                )
                                .show()
                }

                // Configuração de listeners para switches
                setupSwitchListeners()

                // Configuração de listeners para sliders
                setupRangeSliderListeners()
        }

        private fun setupSwitchListeners() {
                // Ativa/desativa controles quando os switches são alterados
                binding.switchParesImpares.setOnCheckedChangeListener { _, isChecked ->
                        binding.sliderParesImpares.isEnabled = isChecked
                }

                binding.switchSomaTotal.setOnCheckedChangeListener { _, isChecked ->
                        binding.sliderSomaTotal.isEnabled = isChecked
                }

                binding.switchPrimos.setOnCheckedChangeListener { _, isChecked ->
                        binding.sliderPrimos.isEnabled = isChecked
                }

                binding.switchFibonacci.setOnCheckedChangeListener { _, isChecked ->
                        binding.sliderFibonacci.isEnabled = isChecked
                }

                binding.switchMioloMoldura.setOnCheckedChangeListener { _, isChecked ->
                        binding.sliderMioloMoldura.isEnabled = isChecked
                }

                binding.switchMultiplosTres.setOnCheckedChangeListener { _, isChecked ->
                        binding.sliderMultiplosTres.isEnabled = isChecked
                }
        }

        private fun setupRangeSliderListeners() {
                // Atualiza os textos de valores quando os sliders são alterados
                binding.sliderParesImpares.addOnChangeListener { slider, _, _ ->
                        val values = slider.values
                        binding.textViewValorParesImpares.text =
                                getString(
                                        R.string.valor_filtro_pares_impares,
                                        values[0].toInt(),
                                        values[1].toInt()
                                )
                }

                binding.sliderSomaTotal.addOnChangeListener { slider, _, _ ->
                        val values = slider.values
                        binding.textViewValorSomaTotal.text =
                                getString(
                                        R.string.valor_filtro_soma_total,
                                        values[0].toInt(),
                                        values[1].toInt()
                                )
                }

                binding.sliderPrimos.addOnChangeListener { slider, _, _ ->
                        val values = slider.values
                        binding.textViewValorPrimos.text =
                                getString(
                                        R.string.valor_filtro_primos,
                                        values[0].toInt(),
                                        values[1].toInt()
                                )
                }

                binding.sliderFibonacci.addOnChangeListener { slider, _, _ ->
                        val values = slider.values
                        binding.textViewValorFibonacci.text =
                                getString(
                                        R.string.valor_filtro_fibonacci,
                                        values[0].toInt(),
                                        values[1].toInt()
                                )
                }

                binding.sliderMioloMoldura.addOnChangeListener { slider, _, _ ->
                        val values = slider.values
                        binding.textViewValorMioloMoldura.text =
                                getString(
                                        R.string.valor_filtro_miolo_moldura,
                                        values[0].toInt(),
                                        values[1].toInt()
                                )
                }

                binding.sliderMultiplosTres.addOnChangeListener { slider, _, _ ->
                        val values = slider.values
                        binding.textViewValorMultiplosTres.text =
                                getString(
                                        R.string.valor_filtro_multiplos_tres,
                                        values[0].toInt(),
                                        values[1].toInt()
                                )
                }
        }

        private fun atualizarInterface(config: ConfiguracaoFiltros) {
                // Configura os switches
                binding.switchParesImpares.isChecked = config.filtroParesImpares
                binding.switchSomaTotal.isChecked = config.filtroSomaTotal
                binding.switchPrimos.isChecked = config.filtroPrimos
                binding.switchFibonacci.isChecked = config.filtroFibonacci
                binding.switchMioloMoldura.isChecked = config.filtroMioloMoldura
                binding.switchMultiplosTres.isChecked = config.filtroMultiplosDeTres

                // Configura os sliders e ativa/desativa conforme o estado dos switches
                configurarSlider(
                        binding.sliderParesImpares,
                        config.minImpares.toFloat(),
                        config.maxImpares.toFloat(),
                        config.filtroParesImpares
                )
                configurarSlider(
                        binding.sliderSomaTotal,
                        config.minSoma.toFloat(),
                        config.maxSoma.toFloat(),
                        config.filtroSomaTotal
                )
                configurarSlider(
                        binding.sliderPrimos,
                        config.minPrimos.toFloat(),
                        config.maxPrimos.toFloat(),
                        config.filtroPrimos
                )
                configurarSlider(
                        binding.sliderFibonacci,
                        config.minFibonacci.toFloat(),
                        config.maxFibonacci.toFloat(),
                        config.filtroFibonacci
                )
                configurarSlider(
                        binding.sliderMioloMoldura,
                        config.minMiolo.toFloat(),
                        config.maxMiolo.toFloat(),
                        config.filtroMioloMoldura
                )
                configurarSlider(
                        binding.sliderMultiplosTres,
                        config.minMultiplos.toFloat(),
                        config.maxMultiplos.toFloat(),
                        config.filtroMultiplosDeTres
                )

                // Força a atualização dos textos de valores
                atualizarTextosValores()
        }

        private fun configurarSlider(
                slider: RangeSlider,
                valorMin: Float,
                valorMax: Float,
                ativo: Boolean
        ) {
                slider.isEnabled = ativo
                slider.values = listOf(valorMin, valorMax)
        }

        private fun atualizarTextosValores() {
                // Atualiza os textos com os valores atuais dos sliders
                val valoresParesImpares = binding.sliderParesImpares.values
                binding.textViewValorParesImpares.text =
                        getString(
                                R.string.valor_filtro_pares_impares,
                                valoresParesImpares[0].toInt(),
                                valoresParesImpares[1].toInt()
                        )

                val valoresSomaTotal = binding.sliderSomaTotal.values
                binding.textViewValorSomaTotal.text =
                        getString(
                                R.string.valor_filtro_soma_total,
                                valoresSomaTotal[0].toInt(),
                                valoresSomaTotal[1].toInt()
                        )

                val valoresPrimos = binding.sliderPrimos.values
                binding.textViewValorPrimos.text =
                        getString(
                                R.string.valor_filtro_primos,
                                valoresPrimos[0].toInt(),
                                valoresPrimos[1].toInt()
                        )

                val valoresFibonacci = binding.sliderFibonacci.values
                binding.textViewValorFibonacci.text =
                        getString(
                                R.string.valor_filtro_fibonacci,
                                valoresFibonacci[0].toInt(),
                                valoresFibonacci[1].toInt()
                        )

                val valoresMioloMoldura = binding.sliderMioloMoldura.values
                binding.textViewValorMioloMoldura.text =
                        getString(
                                R.string.valor_filtro_miolo_moldura,
                                valoresMioloMoldura[0].toInt(),
                                valoresMioloMoldura[1].toInt()
                        )

                val valoresMultiplosTres = binding.sliderMultiplosTres.values
                binding.textViewValorMultiplosTres.text =
                        getString(
                                R.string.valor_filtro_multiplos_tres,
                                valoresMultiplosTres[0].toInt(),
                                valoresMultiplosTres[1].toInt()
                        )
        }

        override fun onDestroyView() {
                super.onDestroyView()
                _binding = null
        }
}
