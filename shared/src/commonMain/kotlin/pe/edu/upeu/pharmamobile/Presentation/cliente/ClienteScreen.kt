package pe.edu.upeu.pharmamobile.presentation.cliente

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
import pe.edu.upeu.pharmamobile.model.Cliente
import pe.edu.upeu.pharmamobile.presentation.components.ValidatedTextField

@Composable
fun ClienteScreen(
    listaClientes: SnapshotStateList<Cliente> = remember { mutableStateListOf() }
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }

    var mensajeResultado by remember { mutableStateOf("") }
    var esExito by remember { mutableStateOf(false) }

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
                text = "Registro de Cliente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )


            ValidatedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    errorNombre = ClienteValidator.validarNombre(it)
                },
                label = "Nombre",
                errorMessage = errorNombre
            )
            Spacer(modifier = Modifier.height(10.dp))


            ValidatedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    errorCorreo = ClienteValidator.validarCorreo(it)
                },
                label = "Correo",
                errorMessage = errorCorreo,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(10.dp))


            ValidatedTextField(
                value = telefono,
                onValueChange = {
                    telefono = it
                    errorTelefono = ClienteValidator.validarTelefono(it)
                },
                label = "Teléfono",
                errorMessage = errorTelefono,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    errorNombre = ClienteValidator.validarNombre(nombre)
                    errorCorreo = ClienteValidator.validarCorreo(correo)
                    errorTelefono = ClienteValidator.validarTelefono(telefono)

                    val hayErrores = errorNombre != null || errorCorreo != null || errorTelefono != null

                    if (!hayErrores) {
                        val nuevoCliente = Cliente(
                            id = (1L..9999L).random(),
                            nombre = nombre.trim(),
                            correo = correo.trim(),
                            telefono = telefono.trim().ifBlank { null }
                        )


                        listaClientes.add(0, nuevoCliente)

                        mensajeResultado = "✅ Cliente '${nuevoCliente.nombre}' agregado con éxito."
                        esExito = true


                        nombre = ""
                        correo = ""
                        telefono = ""
                        errorNombre = null
                        errorCorreo = null
                        errorTelefono = null
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

            if (listaClientes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Clientes Registrados (${listaClientes.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }


        items(listaClientes) { cli ->
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
                            text = cli.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "#${cli.id}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Correo: ${cli.correo}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Teléfono: ${cli.obtenerTelefono()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
