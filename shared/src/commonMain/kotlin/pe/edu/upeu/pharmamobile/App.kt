package pe.edu.upeu.pharmamobile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import pe.edu.upeu.pharmamobile.model.Cliente
import pe.edu.upeu.pharmamobile.model.Producto
import pe.edu.upeu.pharmamobile.presentation.cliente.ClienteScreen
import pe.edu.upeu.pharmamobile.presentation.producto.ProductoScreen

@Composable
fun App() {
    var pestañaSeleccionada by remember { mutableStateOf(0) } // 0: Clientes, 1: Productos
    val titulos = listOf("Clientes", "Productos")

    // Listas persistentes en memoria durante la ejecución de la app
    val listaProductos = remember { mutableStateListOf<Producto>() }
    val listaClientes = remember { mutableStateListOf<Cliente>() }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeDrawingPadding() // Evita que la cámara y la barra de estado tapen las pestañas
        ) {
            TabRow(
                selectedTabIndex = pestañaSeleccionada,
                modifier = Modifier.fillMaxWidth()
            ) {
                titulos.forEachIndexed { index, titulo ->
                    Tab(
                        selected = pestañaSeleccionada == index,
                        onClick = { pestañaSeleccionada = index },
                        text = { Text(titulo) }
                    )
                }
            }

            when (pestañaSeleccionada) {
                0 -> ClienteScreen(listaClientes = listaClientes)
                1 -> ProductoScreen(listaProductos = listaProductos)
            }
        }
    }
}