package com.example.proyectoappmovil.data.validation

import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OportunidadValidacionesTest {

    private val validador = OportunidadValidaciones

    @Test
    fun nombreVacioGeneraError() {
        val errores = validador.validar("", TipoEvento.EVENTO_CORPORATIVO, "Descripcion valida", "Web", "2026-01-01", Prioridad.ALTA, "Alto")
        assertEquals(1, errores.size)
        assertTrue(errores.containsKey("nombre"))
    }

    @Test
    fun tipoNuloGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", null, "Descripcion valida", "Web", "2026-01-01", Prioridad.ALTA, "Alto")
        assertTrue(errores.containsKey("tipo"))
    }

    @Test
    fun descripcionVaciaGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.EVENTO_CORPORATIVO, "", "Web", "2026-01-01", Prioridad.ALTA, "Alto")
        assertEquals(1, errores.size)
        assertTrue(errores.containsKey("descripcion"))
    }

    @Test
    fun fuenteVaciaGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.EVENTO_CORPORATIVO, "Descripcion valida", "", "2026-01-01", Prioridad.ALTA, "Alto")
        assertTrue(errores.containsKey("fuenteInformacion"))
    }

    @Test
    fun prioridadNulaGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.EVENTO_CORPORATIVO, "Descripcion valida", "Web", "2026-01-01", null, "Alto")
        assertEquals(1, errores.size)
        assertTrue(errores.containsKey("prioridad"))
    }

    @Test
    fun potencialComercialVacioGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.EVENTO_CORPORATIVO, "Descripcion valida", "Web", "2026-01-01", Prioridad.ALTA, "")
        assertEquals(1, errores.size)
        assertTrue(errores.containsKey("potencialComercial"))
    }

    @Test
    fun fechaInvalidaGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.EVENTO_CORPORATIVO, "Descripcion valida", "Web", "not-a-date", Prioridad.ALTA, "Alto")
        assertTrue(errores.containsKey("fechaDeteccion"))
    }

    @Test
    fun datosValidosNoGeneranErrores() {
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.FERIA_EMPRESARIAL, "Descripcion valida", "Web", "2026-10-15", Prioridad.MEDIA, "Medio")
        assertTrue(errores.isEmpty())
    }
}
