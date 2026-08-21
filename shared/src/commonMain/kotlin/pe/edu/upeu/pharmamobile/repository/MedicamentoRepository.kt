package pe.edu.upeu.pharmamobile.repository

import pe.edu.upeu.pharmamobile.model.Medicamento
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MedicamentoRepository{
    
    // Lista original de medicamentos (simulando una base de datos)
    private val listaMedicamentos = listOf(
        Medicamento(
            id=1,
            nombre="PARACETAMOL 500 mg",
            principioActivo = "Paracetamol 500 mg",
            precio = 5.50,
            stock = 100
        ),
        Medicamento(
            id = 2,
            nombre = "Ibuprofeno",
            principioActivo = "Ibuprofeno 400 mg",
            precio = 8.90,
            stock = 0 // <-- NOTA: Puse stock 0 para probar el filtro
        ),
        Medicamento(
            id = 3,
            nombre = "Amoxicilina",
            principioActivo = "Amoxicilina 500 mg",
            precio = 12.50,
            stock = 30
        )
    )

    // PASO 6: Aplicando Flow y Asincronía
    fun observarMedicamentos(): Flow<List<Medicamento>> = flow {
        // Emitimos lista vacía (simula estado "Cargando")
        emit(emptyList())
        
        // Simulamos retraso de red de 1.5 segundos
        delay(1500)
        
        // Emitimos la lista real
        emit(listaMedicamentos)
    }

    fun obtenerMedicamentos(): List<Medicamento>{
        return listaMedicamentos
    }

    // 1. Filtrado (filter) -> Solo medicamentos con stock mayor a 0
    fun obtenerMedicamentosDisponibles(): List<Medicamento> {
        return listaMedicamentos.filter { it.stock > 0 }
    }

    // 2. Transformación (map) -> Obtener solo una lista de nombres de medicamentos
    fun obtenerNombresDeMedicamentos(): List<String> {
        return listaMedicamentos.map { it.nombre }
    }

    // 3. Búsqueda (find) -> Buscar un medicamento en específico por su ID
    fun buscarMedicamentoPorId(idBuscado: Int): Medicamento? {
        return listaMedicamentos.find { it.id == idBuscado }
    }
}