package pe.edu.upeu.pharmamobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobile.model.Cliente
import pe.edu.upeu.pharmamobile.model.Producto
import pe.edu.upeu.pharmamobile.navigation.Screen
import pe.edu.upeu.pharmamobile.presentation.cliente.ClienteScreen
import pe.edu.upeu.pharmamobile.presentation.inicio.InicioScreen
import pe.edu.upeu.pharmamobile.presentation.producto.ProductoScreen
import pe.edu.upeu.pharmamobile.theme.PharmaMobilTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var pantallaActual by remember {
        mutableStateOf<Screen>(Screen.Inicio)
    }

    var darkTheme by remember {
        mutableStateOf(false)
    }

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    // Listas persistentes en memoria durante la ejecución de la app
    val listaProductos = remember {
        mutableStateListOf(
            Producto(1L, "Paracetamol 500mg", 5.0, 15),  // Activos (stock > 5)
            Producto(2L, "Amoxicilina 500mg", 12.0, 0),   // Inactivos (stock == 0)
            Producto(3L, "Ibuprofeno 400mg", 8.0, 3)      // Bajo stock (stock in 1..5)
        )
    }
    val listaClientes = remember { mutableStateListOf<Cliente>() }

    PharmaMobilTheme(
        darkTheme = darkTheme
    ) {
        androidx.compose.foundation.layout.BoxWithConstraints {
            // Evaluamos el tamaño de la pantalla actual
            val isCompact = maxWidth < 600.dp
            val isMedium = maxWidth >= 600.dp && maxWidth < 840.dp
            val isExpanded = maxWidth >= 840.dp

            // ==========================================
            // 1. EL CONTENIDO PRINCIPAL (App Bar y Pantallas)
            // ==========================================
            val mainContent: @Composable () -> Unit = {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = tituloPantalla(pantallaActual))
                            },
                            navigationIcon = {
                                // El menú hamburguesa SOLO se muestra en teléfonos (isCompact)
                                if (isCompact) {
                                    IconButton(
                                        onClick = {
                                            scope.launch { drawerState.open() }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Abrir menú"
                                        )
                                    }
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    when (pantallaActual) {
                        Screen.Inicio -> {
                            Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                                InicioScreen()
                            }
                        }
                        Screen.Productos -> {
                            Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                                ProductoScreen(listaProductos = listaProductos)
                            }
                        }
                        Screen.Clientes -> {
                            Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                                ClienteScreen(listaClientes = listaClientes)
                            }
                        }
                        Screen.Pedidos -> {
                            Column(
                                modifier = Modifier.padding(paddingValues).fillMaxSize().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Pedidos",
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Pantalla de pedidos en construcción",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 2. EL MENÚ DEL DRAWER (Reutilizable)
            // ==========================================
            val drawerContentBlock: @Composable () -> Unit = {
                DrawerHeader()

                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = pantallaActual is Screen.Inicio,
                    onClick = {
                        pantallaActual = Screen.Inicio
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Inicio") }
                )

                NavigationDrawerItem(
                    label = { Text("Productos") },
                    selected = pantallaActual is Screen.Productos,
                    onClick = {
                        pantallaActual = Screen.Productos
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(imageVector = Icons.Default.Medication, contentDescription = "Productos") }
                )

                NavigationDrawerItem(
                    label = { Text("Clientes") },
                    selected = pantallaActual is Screen.Clientes,
                    onClick = {
                        pantallaActual = Screen.Clientes
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Clientes") }
                )

                NavigationDrawerItem(
                    label = { Text("Pedidos") },
                    selected = pantallaActual is Screen.Pedidos,
                    onClick = {
                        pantallaActual = Screen.Pedidos
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Pedidos") }
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Modo oscuro")
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = { darkTheme = it }
                    )
                }
            }

            // ==========================================
            // 3. LA DECISIÓN ADAPTATIVA (Paso 14 y 15)
            // ==========================================
            when {
                // A) MODO TELÉFONO: Menú oculto (Drawer Modal)
                isCompact -> {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                drawerContentBlock()
                            }
                        }
                    ) {
                        mainContent()
                    }
                }
                
                // B) MODO TABLET (Mediana): Barra lateral delgada (Navigation Rail)
                isMedium -> {
                    Row {
                        androidx.compose.material3.NavigationRail {
                            Spacer(Modifier.padding(16.dp))
                            androidx.compose.material3.NavigationRailItem(
                                selected = pantallaActual is Screen.Inicio,
                                onClick = { pantallaActual = Screen.Inicio },
                                icon = { Icon(Icons.Default.Home, "Inicio") }
                            )
                            androidx.compose.material3.NavigationRailItem(
                                selected = pantallaActual is Screen.Productos,
                                onClick = { pantallaActual = Screen.Productos },
                                icon = { Icon(Icons.Default.Medication, "Productos") }
                            )
                            androidx.compose.material3.NavigationRailItem(
                                selected = pantallaActual is Screen.Clientes,
                                onClick = { pantallaActual = Screen.Clientes },
                                icon = { Icon(Icons.Default.Person, "Clientes") }
                            )
                            androidx.compose.material3.NavigationRailItem(
                                selected = pantallaActual is Screen.Pedidos,
                                onClick = { pantallaActual = Screen.Pedidos },
                                icon = { Icon(Icons.Default.ShoppingCart, "Pedidos") }
                            )
                            Spacer(Modifier.weight(1f))
                            Switch(
                                checked = darkTheme,
                                onCheckedChange = { darkTheme = it },
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        mainContent()
                    }
                }
                
                // C) MODO ESCRITORIO (Amplia): Menú lateral permanente
                else -> {
                    androidx.compose.material3.PermanentNavigationDrawer(
                        drawerContent = {
                            androidx.compose.material3.PermanentDrawerSheet {
                                drawerContentBlock()
                            }
                        }
                    ) {
                        mainContent()
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "PharmaMobil",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Gestión farmacéutica",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun tituloPantalla(screen: Screen): String {
    return when (screen) {
        Screen.Inicio -> "Inicio"
        Screen.Productos -> "Productos"
        Screen.Clientes -> "Clientes"
        Screen.Pedidos -> "Pedidos"
    }
}