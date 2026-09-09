package pe.edu.upeu.pharmamobile.repository

import pe.edu.upeu.pharmamobile.domain.model.Medicamento
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MedicamentoRepository{
    

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


    fun observarMedicamentos(): Flow<List<Medicamento>> = flow {

        emit(emptyList())
        

        delay(1500)
        

        emit(listaMedicamentos)
    }

    fun obtenerMedicamentos(): List<Medicamento>{
        return listaMedicamentos
    }


    fun obtenerMedicamentosDisponibles(): List<Medicamento> {
        return listaMedicamentos.filter { it.stock > 0 }
    }


    fun obtenerNombresDeMedicamentos(): List<String> {
        return listaMedicamentos.map { it.nombre }
    }


    fun buscarMedicamentoPorId(idBuscado: Int): Medicamento? {
        return listaMedicamentos.find { it.id == idBuscado }
    }
}