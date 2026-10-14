package pe.edu.upeu.pharmamobil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

import pe.edu.upeu.pharmamobil.domain.model.Sesion
import pe.edu.upeu.pharmamobil.navigation.Screen
import pe.edu.upeu.pharmamobil.presentation.cliente.ClienteScreen
import pe.edu.upeu.pharmamobil.presentation.components.EstadoVacio
import pe.edu.upeu.pharmamobil.presentation.inicio.InicioScreen
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoScreen
import pe.edu.upeu.pharmamobil.presentation.sesion.LoginScreen
import pe.edu.upeu.pharmamobil.presentation.sesion.SesionViewModel
import pe.edu.upeu.pharmamobil.theme.PharmaMobilTheme

private data class Destino(
    val screen: Screen,
    val titulo: String,
    val icono: ImageVector
)

@Composable
fun App() = KoinContext {

    val sesionViewModel: SesionViewModel = koinViewModel()
    val sesionState by sesionViewModel.estado.collectAsState()

    var darkTheme by rememberSaveable { mutableStateOf(false) }

    PharmaMobilTheme(darkTheme = darkTheme) {
        val sesionActual = sesionState.sesion

        if (sesionActual == null) {
            LoginScreen(
                viewModel = sesionViewModel,
                onLoginExitoso = {}
            )
        } else {
            ContenidoPrincipal(
                sesion = sesionActual,
                darkTheme = darkTheme,
                onCambiarTema = { darkTheme = it },
                onCerrarSesion = { sesionViewModel.cerrarSesion() }
            )
        }
    }
}

@Composable
private fun ContenidoPrincipal(
    sesion: Sesion,
    darkTheme: Boolean,
    onCambiarTema: (Boolean) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val destinosVisibles = remember(sesion) {
        buildList {
            add(Destino(Screen.Inicio, "Inicio", Icons.Default.Home))
            if (sesion.puede("CATALOGO_LEER")) {
                add(Destino(Screen.Productos, "Productos", Icons.Default.Medication))
            }
            if (sesion.puede("CLIENTES_LEER")) {
                add(Destino(Screen.Clientes, "Clientes", Icons.Default.Person))
            }
            if (sesion.puede("VENTAS_LEER")) {
                add(Destino(Screen.Pedidos, "Pedidos", Icons.Default.ShoppingCart))
            }
        }
    }

    var pantallaActual by remember {
        mutableStateOf<Screen>(
            if (sesion.puede("CATALOGO_LEER")) Screen.Productos else Screen.Inicio
        )
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader(sesion = sesion)

                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))

                destinosVisibles.forEach { destino ->
                    NavigationDrawerItem(
                        label = { Text(destino.titulo) },
                        selected = pantallaActual == destino.screen,
                        onClick = {
                            pantallaActual = destino.screen
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(imageVector = destino.icono, contentDescription = null)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onCerrarSesion()
                    },
                    icon = {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(modifier = Modifier.weight(1f))

                HorizontalDivider()

                ModoOscuro(
                    activo = darkTheme,
                    onCambiar = onCambiarTema
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = tituloDe(pantallaActual, destinosVisibles))
                    },
                    navigationIcon = {
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
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (pantallaActual) {
                    Screen.Inicio -> InicioScreen(
                        onNavegar = { destino -> pantallaActual = destino }
                    )
                    Screen.Productos -> ProductoScreen(
                        viewModel = koinViewModel(),
                        puedeRegistrar = sesion.puede("CATALOGO_ESCRIBIR")
                    )
                    Screen.Clientes -> ClienteScreen(
                        viewModel = koinViewModel()
                    )
                    Screen.Pedidos -> EstadoVacio(
                        icono = Icons.Default.ShoppingCart,
                        titulo = "Pedidos en construcción",
                        descripcion = "Este módulo llega en una próxima sesión del curso.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Screen.Login -> Unit
                }
            }
        }
    }
}

@Composable
private fun DrawerHeader(sesion: Sesion) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.LocalPharmacy,
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .size(28.dp)
            )
        }

        Column {
            Text(
                text = sesion.nombreCompleto,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "@${sesion.usuario}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ModoOscuro(
    activo: Boolean,
    onCambiar: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 28.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.DarkMode,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Modo oscuro",
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = activo,
            onCheckedChange = onCambiar
        )
    }
}

private fun tituloDe(screen: Screen, destinos: List<Destino>): String {
    return destinos.find { it.screen == screen }?.titulo ?: "PharmaMobil"
}
