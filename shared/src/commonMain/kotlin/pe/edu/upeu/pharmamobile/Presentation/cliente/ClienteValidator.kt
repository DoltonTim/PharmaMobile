package pe.edu.upeu.pharmamobile.presentation.cliente

object ClienteValidator {

    fun validarNombre(nombre: String): String? {
        return if (nombre.isBlank()) "El nombre es obligatorio" else null
    }

    fun validarCorreo(correo: String): String? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return when {
            correo.isBlank() -> "El correo es obligatorio"
            !correo.trim().matches(emailRegex) -> "Formato de correo no válido"
            else -> null
        }
    }

    fun validarTelefono(telefono: String): String? {
        val telLimpio = telefono.trim()
        return when {
            telLimpio.isNotBlank() && !telLimpio.all { it.isDigit() } -> "El teléfono solo debe contener números"
            telLimpio.isNotBlank() && telLimpio.length < 9 -> "El teléfono debe tener al menos 9 dígitos"
            else -> null
        }
    }
}
