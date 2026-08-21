package pe.edu.upeu.pharmamobile.viewmodel

import androidx.compose.runtime.mutableStateOf
import pe.edu.upeu.pharmamobile.model.Medicamento
import pe.edu.upeu.pharmamobile.repository.MedicamentoRepository
import pe.edu.upeu.pharmamobile.usecase.ObtenerMedicamentosUseCase
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class MedicamentoViewModel {
    private val repository = MedicamentoRepository()
    private val obtenerMedicamentosUseCase = ObtenerMedicamentosUseCase(repository)
    
    // Un scope para las corrutinas (en Android normalmente hereda de ViewModel, aquí creamos uno simple)
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    // Lista original completa
    var medicamentos by mutableStateOf<List<Medicamento>>(emptyList())
        private set

    // Estado para saber si está cargando
    var estaCargando by mutableStateOf(false)
        private set

    // 1. Aquí usaremos el filtro (filter)
    var medicamentosDisponibles by mutableStateOf<List<Medicamento>>(emptyList())
        private set

    // 2. Aquí usaremos el mapa (map)
    var nombresDeMedicamentos by mutableStateOf<List<String>>(emptyList())
        private set

    // 3. Variable para guardar el resultado de la búsqueda (find)
    var medicamentoEncontrado by mutableStateOf<Medicamento?>(null)
        private set

    init {
        cargarMedicamentosAsincrono() // Llamamos a la versión asíncrona
    }

    // PASO 6: Consumir el Flow (collect) de manera asíncrona
    private fun cargarMedicamentosAsincrono() {
        viewModelScope.launch {
            repository.observarMedicamentos().collect { listaEmitida ->
                // Cuando recibimos lista vacía, significa que apenas está "Cargando"
                estaCargando = listaEmitida.isEmpty()
                
                // Actualizamos nuestra variable de estado con lo que va llegando del tubo
                medicamentos = listaEmitida
                
                // Una vez llegan los datos reales, actualizamos los filtros también
                if (listaEmitida.isNotEmpty()) {
                    cargarDatosFiltradosYTransformados()
                }
            }
        }
    }

    private fun cargarDatosFiltradosYTransformados() {
        // Obtenemos solo los que tienen stock > 0 (filter)
        medicamentosDisponibles = repository.obtenerMedicamentosDisponibles()
        
        // Obtenemos solo los nombres (map)
        nombresDeMedicamentos = repository.obtenerNombresDeMedicamentos()
    }

    fun buscarMedicamento(id: Int) {
        // Buscamos un medicamento específico por su ID (find)
        medicamentoEncontrado = repository.buscarMedicamentoPorId(id)
    }
}