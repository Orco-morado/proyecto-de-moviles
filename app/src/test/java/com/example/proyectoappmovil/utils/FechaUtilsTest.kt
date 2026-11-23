package com.example.proyectoappmovil.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FechaUtilsTest {

    @Test
    fun parsearFecha_valida_devuelveTimestampYEsValida() {
        val fecha = FechaUtils.parsearFecha("22/11/2026")

        assertNotNull(fecha)
        assertTrue(FechaUtils.esFechaValida("22/11/2026"))
        assertEquals("dd/MM/yyyy", FechaUtils.PATRON_FECHA)
    }

    @Test
    fun parsearFecha_diaInexistente_devuelveNull() {
        assertNull(FechaUtils.parsearFecha("31/02/2026"))
        assertFalse(FechaUtils.esFechaValida("31/02/2026"))
    }

    @Test
    fun parsearFecha_formatoIso_devuelveNull() {
        assertNull(FechaUtils.parsearFecha("2026-01-01"))
        assertFalse(FechaUtils.esFechaValida("2026-01-01"))
    }

    @Test
    fun parsearFecha_textoConBasuraAlFinal_devuelveNull() {
        assertNull(FechaUtils.parsearFecha("01/01/2026 basura"))
        assertFalse(FechaUtils.esFechaValida("01/01/2026 basura"))
    }

    @Test
    fun parsearFecha_vacio_devuelveNull() {
        assertNull(FechaUtils.parsearFecha(""))
        assertNull(FechaUtils.parsearFecha("   "))
        assertFalse(FechaUtils.esFechaValida(""))
    }

    @Test
    fun formatearYVolverALeer_devuelveLaMismaFecha() {
        val original = FechaUtils.parsearFecha("01/01/2026")
        assertNotNull(original)

        val formateado = FechaUtils.formatearFecha(original!!)

        assertEquals("01/01/2026", formateado)
        assertEquals(original, FechaUtils.parsearFecha(formateado))
    }
}
