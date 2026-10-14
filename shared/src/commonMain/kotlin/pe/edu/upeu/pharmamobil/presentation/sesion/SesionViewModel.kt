package pe.edu.upeu.pharmamobil.presentation.sesion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.data.remote.mapearErrorSesion
import pe.edu.upeu.pharmamobil.domain.model.Credenciales
import pe.edu.upeu.pharmamobil.domain.model.ErrorSesion
import pe.edu.upeu.pharmamobil.domain.usecase.CerrarSesionUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.IniciarSesionUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ObtenerSesionGuardadaUseCase

class SesionViewModel(
    private val iniciarSesionUseCase: IniciarSesionUseCase,
    private val obtenerSesionGuardadaUseCase: ObtenerSesionGuardadaUseCase,
    private val cerrarSesionUseCase: CerrarSesionUseCase
) : ViewModel() {

    private val _estado = MutableStateFlow(SesionUiState())
    val estado = _estado.asStateFlow()

    init {
        verificarSesion()
    }

    fun onUsuarioCambio(usuario: String) {
        _estado.update { it.copy(usuario = usuario, error = null) }
    }

    fun onClaveCambio(clave: String) {
        _estado.update { it.copy(clave = clave, error = null) }
    }

    fun verificarSesion() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            val sesion = obtenerSesionGuardadaUseCase()
            _estado.update { it.copy(sesion = sesion, cargando = false) }
        }
    }

    fun iniciarSesion(onExito: () -> Unit = {}) {
        val usuario = _estado.value.usuario.trim()
        val clave = _estado.value.clave.trim()

        if (usuario.isBlank() || clave.isBlank()) {
            _estado.update { it.copy(error = "Ingrese usuario y contraseña") }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            iniciarSesionUseCase(Credenciales(usuario, clave))
                .onSuccess { sesion ->
                    _estado.update {
                        it.copy(
                            sesion = sesion,
                            cargando = false,
                            usuario = "",
                            clave = "",
                            error = null
                        )
                    }
                    onExito()
                }
                .onFailure { fallo ->
                    val errorMensaje = when (fallo) {
                        is ClientRequestException -> {
                            when (mapearErrorSesion(fallo.response.status, fallo.message)) {
                                ErrorSesion.CredencialesInvalidas -> "Credenciales inválidas"
                                ErrorSesion.SesionExpirada -> "La sesión ha expirado"
                                ErrorSesion.SinPermiso -> "No tienes permisos suficientes"
                                ErrorSesion.SinConexion -> "Error de conexión con el servidor"
                            }
                        }
                        else -> fallo.message ?: "Error al iniciar sesión"
                    }
                    _estado.update { it.copy(cargando = false, error = errorMensaje) }
                }
        }
    }

    fun cerrarSesion(onCerrar: () -> Unit = {}) {
        viewModelScope.launch {
            cerrarSesionUseCase()
            _estado.update { SesionUiState() }
            onCerrar()
        }
    }
}
