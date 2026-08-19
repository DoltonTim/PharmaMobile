package pe.edu.upeu.pharmamobile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.pharmamobile.viewmodel.MedicamentoViewModel

@Composable
fun App() {

    val viewModel = remember {
        MedicamentoViewModel()
    }

    val medicamentos = viewModel.medicamentos

    MaterialTheme {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "PharmaMobile",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Medicamentos",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(medicamentos) { medicamento ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = medicamento.nombre,
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text = "Principio activo: ${medicamento.principioActivo}"
                            )

                            Text(
                                text = "Precio: S/ ${medicamento.precio}"
                            )

                            Text(
                                text = "Stock: ${medicamento.stock}"
                            )
                        }
                    }
                }
            }
        }
    }
}