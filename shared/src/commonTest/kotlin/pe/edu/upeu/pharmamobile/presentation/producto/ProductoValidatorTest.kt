package pe.edu.upeu.pharmamobile.presentation.producto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProductoValidatorTest {

    @Test
    fun aceptaUnPrecioValido() {
        assertNull(ProductoValidator.validarPrecio("12.50"))
    }

    @Test
    fun rechazaPrecioNoNumerico() {
        assertEquals(
            "El precio debe ser un número válido",
            ProductoValidator.validarPrecio("abc")
        )
    }

    @Test
    fun rechazaNaNComoPrecio() {
        assertEquals(
            "El precio debe ser un número válido",
            ProductoValidator.validarPrecio("NaN")
        )
    }

    @Test
    fun rechazaInfinityComoPrecio() {
        assertEquals(
            "El precio debe ser un número válido",
            ProductoValidator.validarPrecio("Infinity")
        )
    }

    @Test
    fun rechazaPrecioCero() {
        assertEquals(
            "El precio debe ser mayor a 0",
            ProductoValidator.validarPrecio("0")
        )
    }

    @Test
    fun aceptaStockCero() {
        assertNull(ProductoValidator.validarStock("0"))
    }

    @Test
    fun rechazaStockNegativo() {
        assertEquals(
            "El stock no puede ser negativo",
            ProductoValidator.validarStock("-1")
        )
    }
}
