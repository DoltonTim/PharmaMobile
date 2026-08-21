package pe.edu.upeu.pharmamobile.repository

import pe.edu.upeu.pharmamobile.model.Producto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductoRepository {
    
    // Lista simulada de productos
    private val productos = listOf(
        Producto(id = 1L, nombre = "Paracetamol", precio = 5.0, stock = 10),
        Producto(id = 2L, nombre = "Ibuprofeno", precio = 8.0, stock = 0), // Sin stock
        Producto(id = 3L, nombre = "Amoxicilina", precio = 15.0, stock = 5)
    )

    // PASO 6: Asincronía con Corrutinas & Flow
    // Emisión de múltiples valores a lo largo del tiempo
    fun observarProductos(): Flow<List<Producto>> = flow {
        // 1. Emitimos una lista vacía primero (por ejemplo, para mostrar un "Cargando..." en la pantalla)
        emit(emptyList())
        
        // 2. Simulamos que estamos esperando a que el servidor de internet responda (1 segundo)
        delay(1000)
        
        // 3. Emitimos los datos reales ("productosRemotos")
        emit(productos)
    }

    // 1. Uso de 'filter'
    // Aplicación: Obtener solo los productos que tienen stock mayor a 0 para mostrarlos en la tienda.
    fun obtenerProductosDisponibles(): List<Producto> {
        return productos.filter { it.stock > 0 }
    }

    // 2. Uso de 'map'
    // Aplicación: Extraer solo los nombres de los productos para mostrarlos en un buscador o lista simple.
    fun obtenerNombresDeProductos(): List<String> {
        return productos.map { it.nombre }
    }

    // 3. Uso de 'find'
    // Aplicación: Buscar un producto en específico por su ID cuando el usuario entra a ver el "Detalle" del producto.
    fun buscarProductoPorId(idBuscado: Long): Producto? {
        return productos.find { it.id == idBuscado }
    }
}
