package pe.edu.upeu.pharmamobil.domain.usecase

import pe.edu.upeu.pharmamobil.domain.model.Sesion
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository

class ObtenerSesionGuardadaUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Sesion? = authRepository.sesionGuardada()
}
