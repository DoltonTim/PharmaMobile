package pe.edu.upeu.pharmamobile.viewmodel

import androidx.compose.runtime.mutableStateOf
import pe.edu.upeu.pharmamobile.model.Medicamento
import pe.edu.upeu.pharmamobile.repository.MedicamentoRepository
import pe.edu.upeu.pharmamobile.usecase.ObtenerMedicamentosUseCase
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
class MedicamentoViewModel {
    private val repository = MedicamentoRepository()
    private val obtenerMedicamentosUseCase = ObtenerMedicamentosUseCase(repository)
    var medicamentos by mutableStateOf<List<Medicamento>>(emptyList())
        private set

    init {
        cargarMedicamentos()
    }

    private fun cargarMedicamentos() {
        medicamentos = obtenerMedicamentosUseCase()
    }
}