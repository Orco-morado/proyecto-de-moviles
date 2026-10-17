package com.example.proyectoappmovil.data.validation

import com.example.proyectoappmovil.data.model.TipoEvento
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OportunidadValidacionesTest {

    private val validador = OportunidadValidaciones

    @Test
    fun nombreVacioGeneraError() {
        val errores = validador.validar("", TipoEvento.EVENTO_CORPORATIVO, "Web", "2026-01-01")
        assertEquals(1, errores.size)
        assertTrue(errores.containsKey("nombre"))
    }

    @Test
    fun tipoNuloGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", null, "Web", "2026-01-01")
        assertTrue(errores.containsKey("tipo"))
    }

    @Test
    fun fuenteVaciaGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.EVENTO_CORPORATIVO, "", "2026-01-01")
        assertTrue(errores.containsKey("fuenteInformacion"))
    }

    @Test
    fun fechaInvalidaGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.EVENTO_CORPORATIVO, "Web", "not-a-date")
        assertTrue(errores.containsKey("fechaDeteccion"))
    }

    @Test
    fun datosValidosNoGeneranErrores() {
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.FERIA_EMPRESARIAL, "Web", "2026-10-15")
        assertTrue(errores.isEmpty())
    }
}
