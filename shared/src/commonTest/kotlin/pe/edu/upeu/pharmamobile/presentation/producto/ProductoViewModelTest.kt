package pe.edu.upeu.pharmamobile.presentation.producto

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobile.domain.usecase.RegistrarProductoUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FakeProductoRepository(
    private var productosIniciales: List<Producto> = emptyList(),
    private val deberiaFallar: Boolean = false
) : ProductoRepository {
    
    private val productos = mutableListOf<Producto>().apply { addAll(productosIniciales) }

    override suspend fun listar(): List<Producto> {
        if (deberiaFallar) throw Exception("Error forzado en la red")
        return productos
    }

    override suspend fun registrar(producto: Producto): Result<Producto> {
        if (deberiaFallar) return Result.failure(Exception("Error forzado en la red al registrar"))
        val nuevoProducto = producto.copy(id = (productos.size + 1).toLong())
        productos.add(nuevoProducto)
        return Result.success(nuevoProducto)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun cargarProductos_conListaVacia_deberiaRetornarFaseSinProductos() = runTest {
        val fakeRepository = FakeProductoRepository(emptyList())
        val useCase = RegistrarProductoUseCase(fakeRepository)
        val viewModel = ProductoViewModel(useCase, fakeRepository)

        val uiState = viewModel.uiState.value
        assertIs<ProductoFase.SinProductos>(uiState.fase)
    }

    @Test
    fun cargarProductos_conListaLlena_deberiaRetornarFaseConProductos() = runTest {
        val productos = listOf(
            Producto(1, "Paracetamol", 5.0, 10),
            Producto(2, "Ibuprofeno", 8.0, 5),
            Producto(3, "Amoxicilina", 15.0, 3)
        )
        val fakeRepository = FakeProductoRepository(productos)
        val useCase = RegistrarProductoUseCase(fakeRepository)
        val viewModel = ProductoViewModel(useCase, fakeRepository)

        val uiState = viewModel.uiState.value
        assertIs<ProductoFase.ConProductos>(uiState.fase)
        val estado = uiState.fase as ProductoFase.ConProductos
        assertEquals(3, estado.productos.size)
    }

    @Test
    fun cargarProductos_conErrorRed_deberiaRetornarFaseError() = runTest {
        val fakeRepository = FakeProductoRepository(deberiaFallar = true)
        val useCase = RegistrarProductoUseCase(fakeRepository)
        val viewModel = ProductoViewModel(useCase, fakeRepository)

        val uiState = viewModel.uiState.value
        assertIs<ProductoFase.Error>(uiState.fase)
        val estado = uiState.fase as ProductoFase.Error
        assertEquals("Error forzado en la red", estado.mensaje)
    }

    @Test
    fun registrarProducto_conPrecioInvalido_deberiaMostrarErrorDeValidacion() = runTest {
        val fakeRepository = FakeProductoRepository()
        val useCase = RegistrarProductoUseCase(fakeRepository)
        val viewModel = ProductoViewModel(useCase, fakeRepository)

        viewModel.onNombreChange("Aspirina")
        viewModel.onPrecioChange("0")
        viewModel.onStockChange("10")
        
        viewModel.registrarProducto()
        
        val uiState = viewModel.uiState.value
        assertEquals(false, uiState.formulario.esExito)
        assertTrue(uiState.formulario.errorPrecio != null)
    }
}
