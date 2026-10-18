package com.example.proyectoappmovil.data.alertas

import org.junit.Assert.assertEquals
import org.junit.Test

class ResumenAlertasTest {

    @Test
    fun sinPendientesNiEsperaDevuelveMensajeSinAlertas() {
        assertEquals(
            "Sin pendientes por ahora: no hay seguimientos ni propuestas en espera",
            ResumenAlertas.construirTexto(0, 0, 0)
        )
    }

    @Test
    fun soloPendientesSinVencidos() {
        assertEquals(
            "2 seguimientos pendientes",
            ResumenAlertas.construirTexto(2, 0, 0)
        )
    }

    @Test
    fun unSoloPendienteUsaSingular() {
        assertEquals(
            "1 seguimiento pendiente",
            ResumenAlertas.construirTexto(1, 0, 0)
        )
    }

    @Test
    fun pendientesConVencidosIndicaCantidad() {
        assertEquals(
            "3 seguimientos pendientes (2 vencidos)",
            ResumenAlertas.construirTexto(3, 2, 0)
        )
    }

    @Test
    fun unSoloVencidoUsaSingular() {
        assertEquals(
            "1 seguimiento pendiente (1 vencido)",
            ResumenAlertas.construirTexto(1, 1, 0)
        )
    }

    @Test
    fun combinaPendientesYPropuestas() {
        assertEquals(
            "2 seguimientos pendientes (1 vencido) y 1 propuesta en espera de respuesta",
            ResumenAlertas.construirTexto(2, 1, 1)
        )
    }

    @Test
    fun soloPropuestasEnEspera() {
        assertEquals(
            "3 propuestas en espera de respuesta",
            ResumenAlertas.construirTexto(0, 0, 3)
        )
    }
}
