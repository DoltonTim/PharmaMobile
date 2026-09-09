package pe.edu.upeu.pharmamobile.presentation.producto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.presentation.components.ValidatedTextField

@Composable
fun ProductoScreen(
    viewModel: ProductoViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val titulosTabs = listOf("Activos", "Inactivos", "Bajo stock")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "PharmaMobil",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Registro de Producto",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            ValidatedTextField(
                value = state.formulario.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = "Nombre",
                errorMessage = state.formulario.errorNombre
            )
            Spacer(modifier = Modifier.height(10.dp))

            ValidatedTextField(
                value = state.formulario.precio,
                onValueChange = { viewModel.onPrecioChange(it) },
                label = "Precio",
                errorMessage = state.formulario.errorPrecio,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.height(10.dp))

            ValidatedTextField(
                value = state.formulario.stock,
                onValueChange = { viewModel.onStockChange(it) },
                label = "Stock",
                errorMessage = state.formulario.errorStock,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.registrarProducto() },
                enabled = !state.formulario.estaEnviando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = if (state.formulario.estaEnviando) "Registrando..." else "Registrar",
                    fontSize = 16.sp
                )
            }

            if (state.formulario.mensajeResultado.isNotBlank()) {
                Text(
                    text = state.formulario.mensajeResultado,
                    color = if (state.formulario.esExito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        when (val fase = state.fase) {
            is ProductoFase.Cargando -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Cargando inventario...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            is ProductoFase.SinProductos -> {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Inventario vacío. No hay productos registrados aún.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            is ProductoFase.Error -> {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error: ${fase.mensaje}",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { viewModel.cargarProductos() }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }

            is ProductoFase.ConProductos -> {
                val todosProductos = fase.productos
                val productosFiltrados = when (state.tabSeleccionada) {
                    0 -> todosProductos.filter { it.stock > 5 }
                    1 -> todosProductos.filter { it.stock == 0 }
                    2 -> todosProductos.filter { it.stock in 1..5 }
                    else -> todosProductos
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Inventario de Productos (${todosProductos.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PrimaryTabRow(
                        selectedTabIndex = state.tabSeleccionada,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        titulosTabs.forEachIndexed { index, titulo ->
                            val conteo = when (index) {
                                0 -> todosProductos.count { it.stock > 5 }
                                1 -> todosProductos.count { it.stock == 0 }
                                2 -> todosProductos.count { it.stock in 1..5 }
                                else -> 0
                            }
                            Tab(
                                selected = state.tabSeleccionada == index,
                                onClick = { viewModel.onTabSeleccionada(index) },
                                text = { Text("$titulo ($conteo)") }
                            )
                        }
                    }
                }

                if (productosFiltrados.isEmpty()) {
                    item {
                        Text(
                            text = "No hay productos en la categoría '${titulosTabs[state.tabSeleccionada]}'",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(productosFiltrados, key = { it.id }) { prod ->
                        ProductoItemCard(prod)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductoItemCard(prod: Producto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = prod.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "S/ ${prod.precio}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Stock: ${prod.stock} unidades",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Valor Total: S/ ${prod.estadoDisponible()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (prod.requiereReposicion) {
                Text(
                    text = "⚠️ Requiere reposición (Stock <= ${Producto.STOCK_MINIMO})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
