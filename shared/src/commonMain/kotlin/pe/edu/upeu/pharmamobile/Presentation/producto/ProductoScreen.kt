package pe.edu.upeu.pharmamobile.presentation.producto

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upeu.pharmamobile.model.Producto
import pe.edu.upeu.pharmamobile.presentation.components.ValidatedTextField

@Composable
fun ProductoScreen(
    listaProductos: SnapshotStateList<Producto> = remember { mutableStateListOf() }
) {
    var nombre by remember { mutableStateOf("") }
    var precioTexto by remember { mutableStateOf("") }
    var stockTexto by remember { mutableStateOf("") }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorPrecio by remember { mutableStateOf<String?>(null) }
    var errorStock by remember { mutableStateOf<String?>(null) }

    var mensajeResultado by remember { mutableStateOf("") }
    var esExito by remember { mutableStateOf(false) }

    var tabSeleccionada by remember { mutableStateOf(0) }
    // 0: Activos, 1: Inactivos, 2: Bajo stock
    val titulosTabs = listOf("Activos", "Inactivos", "Bajo stock")

    val productosFiltrados = when (tabSeleccionada) {
        0 -> listaProductos.filter { it.stock > 5 }
        1 -> listaProductos.filter { it.stock == 0 }
        2 -> listaProductos.filter { it.stock in 1..5 }
        else -> listaProductos
    }

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
                value = nombre,
                onValueChange = {
                    nombre = it
                    errorNombre = ProductoValidator.validarNombre(it)
                },
                label = "Nombre",
                errorMessage = errorNombre
            )
            Spacer(modifier = Modifier.height(10.dp))


            ValidatedTextField(
                value = precioTexto,
                onValueChange = {
                    precioTexto = it
                    errorPrecio = ProductoValidator.validarPrecio(it)
                },
                label = "Precio",
                errorMessage = errorPrecio,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.height(10.dp))


            ValidatedTextField(
                value = stockTexto,
                onValueChange = {
                    stockTexto = it
                    errorStock = ProductoValidator.validarStock(it)
                },
                label = "Stock",
                errorMessage = errorStock,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    errorNombre = ProductoValidator.validarNombre(nombre)
                    errorPrecio = ProductoValidator.validarPrecio(precioTexto)
                    errorStock = ProductoValidator.validarStock(stockTexto)

                    val hayErrores = errorNombre != null || errorPrecio != null || errorStock != null

                    if (!hayErrores) {
                        val precio = precioTexto.toDouble()
                        val stock = stockTexto.toInt()

                        val nuevoProducto = Producto(
                            id = (1L..9999L).random(),
                            nombre = nombre.trim(),
                            precio = precio,
                            stock = stock
                        )


                        listaProductos.add(0, nuevoProducto)

                        mensajeResultado = "✅ Producto '${nuevoProducto.nombre}' agregado con éxito."
                        esExito = true


                        nombre = ""
                        precioTexto = ""
                        stockTexto = ""
                        errorNombre = null
                        errorPrecio = null
                        errorStock = null
                    } else {
                        mensajeResultado = ""
                        esExito = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Registrar", fontSize = 16.sp)
            }

            if (mensajeResultado.isNotBlank()) {
                Text(
                    text = mensajeResultado,
                    color = if (esExito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (listaProductos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Inventario de Productos (${listaProductos.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                PrimaryTabRow(
                    selectedTabIndex = tabSeleccionada,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    titulosTabs.forEachIndexed { index, titulo ->
                        val conteo = when (index) {
                            0 -> listaProductos.count { it.stock > 5 }
                            1 -> listaProductos.count { it.stock == 0 }
                            2 -> listaProductos.count { it.stock in 1..5 }
                            else -> 0
                        }
                        Tab(
                            selected = tabSeleccionada == index,
                            onClick = { tabSeleccionada = index },
                            text = { Text("$titulo ($conteo)") }
                        )
                    }
                }
            }
        }

        if (listaProductos.isNotEmpty() && productosFiltrados.isEmpty()) {
            item {
                Text(
                    text = "No hay productos en la categoría '${titulosTabs[tabSeleccionada]}'",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        } else {
            items(productosFiltrados) { prod ->
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
                        Text(text = "Stock: ${prod.stock} unidades", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "Valor Total: S/ ${prod.estadoDisponible()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
