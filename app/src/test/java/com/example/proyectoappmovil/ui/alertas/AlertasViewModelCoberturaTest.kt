package com.example.proyectoappmovil.ui.alertas

import com.example.proyectoappmovil.data.MainDispatcherRule
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.local.entity.Seguimiento
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoAccionSeguimiento
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.data.repository.OportunidadRepository
import com.example.proyectoappmovil.data.repository.SeguimientoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlertasViewModelCoberturaTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val seguimientoRepository = mockk<SeguimientoRepository>(relaxed = true)
    private val oportunidadRepository = mockk<OportunidadRepository>(relaxed = true)

    private fun crearSeguimiento() = Seguimiento(
        id = 1L,
        oportunidadId = 2L,
        usuarioResponsableId = 3L,
        tipoAccion = TipoAccionSeguimiento.entries.first(),
        fechaHora = 1000L,
        resultado = "Resultado",
        proximaAccion = "Llamar",
        fechaProximoSeguimiento = 2000L,
        observaciones = ""
    )

    private fun crearOportunidad() = Oportunidad(
        id = 2L,
        nombre = "Oportunidad en espera",
        tipo = TipoEvento.entries.first(),
        descripcion = "Descripcion",
        fuenteInformacion = "Web",
        fechaDeteccion = 1000L,
        responsableId = 3L,
        prioridad = Prioridad.entries.first(),
        potencialComercial = "Medio",
        estado = EstadoOportunidad.entries.first(),
        eventoId = null,
        fechaActualizacion = 2000L,
        observaciones = ""
    )

    @Test
    fun init_exponeSeguimientosPendientesYOportunidadesEnEspera() = runTest {
        val seguimientos = listOf(crearSeguimiento())
        val oportunidades = listOf(crearOportunidad())
        every { seguimientoRepository.observarPendientes(any()) } returns flowOf(seguimientos)
        every { oportunidadRepository.observarEnEsperaRespuesta() } returns flowOf(oportunidades)

        val viewModel = AlertasViewModel(seguimientoRepository, oportunidadRepository)
        var seguimientosRecibidos: List<Seguimiento> = emptyList()
        var oportunidadesRecibidas: List<Oportunidad> = emptyList()
        val jobSeguimientos = launch { viewModel.seguimientosPendientes.collect { seguimientosRecibidos = it } }
        val jobOportunidades = launch { viewModel.oportunidadesEnEspera.collect { oportunidadesRecibidas = it } }
        advanceUntilIdle()

        assertEquals(seguimientos, seguimientosRecibidos)
        assertEquals(oportunidades, oportunidadesRecibidas)
        jobSeguimientos.cancel()
        jobOportunidades.cancel()
        verify(exactly = 1) {
            seguimientoRepository.observarPendientes(match { it > System.currentTimeMillis() })
        }
        verify(exactly = 1) { oportunidadRepository.observarEnEsperaRespuesta() }
    }

    @Test
    fun init_conListasVacias_exponeEstadosVacios() = runTest {
        every { seguimientoRepository.observarPendientes(any()) } returns flowOf(emptyList())
        every { oportunidadRepository.observarEnEsperaRespuesta() } returns flowOf(emptyList())

        val viewModel = AlertasViewModel(seguimientoRepository, oportunidadRepository)
        advanceUntilIdle()

        assertEquals(emptyList<Seguimiento>(), viewModel.seguimientosPendientes.first())
        assertEquals(emptyList<Oportunidad>(), viewModel.oportunidadesEnEspera.first())
    }
}
