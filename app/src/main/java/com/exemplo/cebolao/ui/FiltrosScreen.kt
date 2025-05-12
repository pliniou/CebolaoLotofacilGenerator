package com.exemplo.cebolao.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.exemplo.cebolao.viewmodel.MainViewModel

@Composable
fun FiltrosScreen(viewModel: MainViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Filtros Selecionáveis",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.primary
        )

        val filters = listOf(
            "Primos",
            "Pares",
            "Cruz",
            "Moldura",
            "Metade Superior",
            "Progressão Aritmética",
            "Sequência",
            "Linha",
            "Coluna",
            "Soma"
        )
        filters.forEach { filter ->
            var isChecked by remember { mutableStateOf(false) }
            val switchModifier = Modifier.clickable {
                isChecked = !isChecked
            }
            Card(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 10.dp)
                        .fillMaxWidth()
                        .padding(16.dp),                       
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = filter)
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isChecked,
                        onCheckedChange = {
                            isChecked = it
                        },
                        modifier = switchModifier
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                viewModel.setFilters(filters)
            },
            modifier = Modifier
                .align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Aplicar",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
