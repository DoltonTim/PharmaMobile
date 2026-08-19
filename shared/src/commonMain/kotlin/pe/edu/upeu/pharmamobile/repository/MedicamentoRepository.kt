package pe.edu.upeu.pharmamobile.repository

import pe.edu.upeu.pharmamobile.model.Medicamento

class MedicamentoRepository{
    fun obtenerMedicamentos(): List<Medicamento>{
        return listOf(
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
                stock = 50
            ),
            Medicamento(
                id = 3,
                nombre = "Amoxicilina",
                principioActivo = "Amoxicilina 500 mg",
                precio = 12.50,
                stock = 30
            )

        )
    }
}