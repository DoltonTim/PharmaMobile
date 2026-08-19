package pe.edu.upeu.pharmamobile.usecase

import pe.edu.upeu.pharmamobile.model.Medicamento
import pe.edu.upeu.pharmamobile.repository.MedicamentoRepository

class ObtenerMedicamentosUseCase(
    private val repository: MedicamentoRepository
){
    operator fun invoke(): List<Medicamento> {
        return repository.obtenerMedicamentos()
    }
}