package pe.edu.upeu.pharmamobile.usecase

import pe.edu.upeu.pharmamobile.domain.model.EstadoPedido
import pe.edu.upeu.pharmamobile.domain.model.Pedido

class GestionarPedidoUseCase {

    // Aquí aplicamos la evaluación "exhaustiva" mediante 'when' 
    // como se menciona en tu imagen.
    fun obtenerMensajeDeEstado(pedido: Pedido): String {
        
        return when (pedido.estado) {
            
            // Evaluamos cada posible estado de la sealed class
            EstadoPedido.Pendiente -> {
                "Su pedido está en la cola, esperando ser atendido."
            }
            
            EstadoPedido.Procesando -> {
                "Estamos empacando sus medicamentos."
            }
            
            EstadoPedido.Entregado -> {
                "¡El pedido ha sido entregado exitosamente al cliente ${pedido.cliente.nombre}!"
            }
            
            // En Rechazado, extraemos la información adicional (el motivo)
            is EstadoPedido.Rechazado -> {
                "Lo sentimos, su pedido fue rechazado. Motivo: ${pedido.estado.motivo}"
            }
        }
    }
}
