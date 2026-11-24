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
        val errores = validador.validar("", TipoEvento.EVENTO_CORPORATIVO, "Descripcion valida", "Web", "01/01/2026", Prioridad.ALTA, "Alto")
        assertEquals(1, errores.size)
        assertTrue(errores.containsKey("nombre"))
    }

    @Test
    fun tipoNuloGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", null, "Descripcion valida", "Web", "01/01/2026", Prioridad.ALTA, "Alto")
        assertTrue(errores.containsKey("tipo"))
    }

    @Test
    fun descripcionVaciaGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.EVENTO_CORPORATIVO, "", "Web", "01/01/2026", Prioridad.ALTA, "Alto")
        assertEquals(1, errores.size)
        assertTrue(errores.containsKey("descripcion"))
    }

    @Test
    fun fuenteVaciaGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.EVENTO_CORPORATIVO, "Descripcion valida", "", "01/01/2026", Prioridad.ALTA, "Alto")
        assertTrue(errores.containsKey("fuenteInformacion"))
    }

    @Test
    fun prioridadNulaGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.EVENTO_CORPORATIVO, "Descripcion valida", "Web", "01/01/2026", null, "Alto")
        assertEquals(1, errores.size)
        assertTrue(errores.containsKey("prioridad"))
    }

    @Test
    fun potencialComercialVacioGeneraError() {
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.EVENTO_CORPORATIVO, "Descripcion valida", "Web", "01/01/2026", Prioridad.ALTA, "")
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
        val errores = validador.validar("Oportunidad de prueba", TipoEvento.FERIA_EMPRESARIAL, "Descripcion valida", "Web", "15/10/2026", Prioridad.MEDIA, "Medio")
        assertTrue(errores.isEmpty())
    }

    @Test
    fun camposEnBlancoGeneranErrores() {
        val errores = validador.validar("   ", TipoEvento.EVENTO_CORPORATIVO, "   ", "   ", "01/01/2026", Prioridad.ALTA, "   ")
        assertEquals(4, errores.size)
        assertTrue(errores.containsKey("nombre"))
        assertTrue(errores.containsKey("descripcion"))
        assertTrue(errores.containsKey("fuenteInformacion"))
        assertTrue(errores.containsKey("potencialComercial"))
    }

    @Test
    fun fechaVaciaOFormatoBorde_generaError() {
        val fechaVacia = validador.validar("Oportunidad", TipoEvento.EVENTO_CORPORATIVO, "Descripcion", "Web", "", Prioridad.ALTA, "Alto")
        val fechaBorde = validador.validar("Oportunidad", TipoEvento.EVENTO_CORPORATIVO, "Descripcion", "Web", "31/02/2026", Prioridad.ALTA, "Alto")
        val fechaCorta = validador.validar("Oportunidad", TipoEvento.EVENTO_CORPORATIVO, "Descripcion", "Web", "01-01-2026", Prioridad.ALTA, "Alto")

        assertTrue(fechaVacia.containsKey("fechaDeteccion"))
        assertTrue(fechaBorde.containsKey("fechaDeteccion"))
        assertTrue(fechaCorta.containsKey("fechaDeteccion"))
        assertFalse(fechaVacia.containsKey("nombre"))
    }

    @Test
    fun multiplesCamposInvalidos_acumulaErrores() {
        val errores = validador.validar("", null, "", "", "", null, "")

        assertEquals(7, errores.size)
        assertTrue(errores.keys.containsAll(
            listOf("nombre", "tipo", "descripcion", "fuenteInformacion", "prioridad", "potencialComercial", "fechaDeteccion")
        ))
    }
}
