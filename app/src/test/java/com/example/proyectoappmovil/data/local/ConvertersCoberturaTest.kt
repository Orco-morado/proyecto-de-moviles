package com.example.proyectoappmovil.data.local

import com.example.proyectoappmovil.data.model.AreaContacto
import com.example.proyectoappmovil.data.model.EstadoContacto
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.EstadoUsuario
import com.example.proyectoappmovil.data.model.MedioContacto
import com.example.proyectoappmovil.data.model.PerfilUsuario
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoAccionSeguimiento
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.data.model.TipoOrganizacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ConvertersCoberturaTest {

    private val converters = Converters()

    @Test
    fun perfilesUsuario_hacenRoundTrip() {
        PerfilUsuario.entries.forEach {
            assertEquals(it.name, converters.dePerfilUsuario(it))
            assertEquals(it, converters.aPerfilUsuario(it.name))
        }
    }

    @Test
    fun estadosUsuario_hacenRoundTrip() {
        EstadoUsuario.entries.forEach {
            assertEquals(it.name, converters.deEstadoUsuario(it))
            assertEquals(it, converters.aEstadoUsuario(it.name))
        }
    }

    @Test
    fun tiposOrganizacion_hacenRoundTrip() {
        TipoOrganizacion.entries.forEach {
            assertEquals(it.name, converters.deTipoOrganizacion(it))
            assertEquals(it, converters.aTipoOrganizacion(it.name))
        }
    }

    @Test
    fun tiposEvento_hacenRoundTrip() {
        TipoEvento.entries.forEach {
            assertEquals(it.name, converters.deTipoEvento(it))
            assertEquals(it, converters.aTipoEvento(it.name))
        }
    }

    @Test
    fun estadosOportunidad_hacenRoundTrip() {
        EstadoOportunidad.entries.forEach {
            assertEquals(it.name, converters.deEstadoOportunidad(it))
            assertEquals(it, converters.aEstadoOportunidad(it.name))
        }
    }

    @Test
    fun prioridades_hacenRoundTrip() {
        Prioridad.entries.forEach {
            assertEquals(it.name, converters.dePrioridad(it))
            assertEquals(it, converters.aPrioridad(it.name))
        }
    }

    @Test
    fun areasContacto_hacenRoundTrip() {
        AreaContacto.entries.forEach {
            assertEquals(it.name, converters.deAreaContacto(it))
            assertEquals(it, converters.aAreaContacto(it.name))
        }
    }

    @Test
    fun mediosContacto_hacenRoundTrip() {
        MedioContacto.entries.forEach {
            assertEquals(it.name, converters.deMedioContacto(it))
            assertEquals(it, converters.aMedioContacto(it.name))
        }
    }

    @Test
    fun estadosContacto_hacenRoundTrip() {
        EstadoContacto.entries.forEach {
            assertEquals(it.name, converters.deEstadoContacto(it))
            assertEquals(it, converters.aEstadoContacto(it.name))
        }
    }

    @Test
    fun tiposAccionSeguimiento_hacenRoundTrip() {
        TipoAccionSeguimiento.entries.forEach {
            assertEquals(it.name, converters.deTipoAccionSeguimiento(it))
            assertEquals(it, converters.aTipoAccionSeguimiento(it.name))
        }
    }

    @Test
    fun valorInvalido_lanzaIllegalArgumentException() {
        assertThrows(IllegalArgumentException::class.java) { converters.aPerfilUsuario("NO_EXISTE") }
        assertThrows(IllegalArgumentException::class.java) { converters.aPrioridad("") }
    }
}
