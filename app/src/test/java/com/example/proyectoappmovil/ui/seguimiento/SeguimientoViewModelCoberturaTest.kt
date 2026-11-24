package com.example.proyectoappmovil.ui.seguimiento

import com.example.proyectoappmovil.data.MainDispatcherRule
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.local.entity.Seguimiento
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoAccionSeguimiento
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.data.repository.OportunidadRepository
import com.example.proyectoappmovil.data.repository.SeguimientoRepository
import com.example.proyectoappmovil.utils.FechaUtils
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SeguimientoViewModelCoberturaTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val seguimientoRepository = mockk<SeguimientoRepository>(relaxed = true)
    private val oportunidadRepository = mockk<OportunidadRepository>(relaxed = true)
    private val fecha = FechaUtils.formatearFecha(1790000000000L)

    private fun crearOportunidad() = Oportunidad(
        id = 7L,
        nombre = "Oportunidad 7",
        tipo = TipoEvento.entries.first(),
        descripcion = "Descripcion",
        fuenteInformacion = "Web",
        fechaDeteccion = 1000L,
        responsableId = null,
        prioridad = Prioridad.entries.first(),
        potencialComercial = "Medio",
        estado = EstadoOportunidad.entries.first(),
        eventoId = null,
        fechaActualizacion = 2000L,
        observaciones = ""
    )

    private fun crearViewModel(): SeguimientoViewModel {
        every { seguimientoRepository.observarPorOportunidad(7L) } returns flowOf(emptyList())
        coEvery { oportunidadRepository.obtenerPorId(7L) } returns crearOportunidad()
        return SeguimientoViewModel(seguimientoRepository, oportunidadRepository, 7L)
    }

    private fun llenarFormulario(viewModel: SeguimientoViewModel, conFecha: Boolean = true) {
        viewModel.onTipoAccionSeleccionado(TipoAccionSeguimiento.entries.first())
        viewModel.onResultadoChange("Resultado de prueba")
        if (conFecha) {
            viewModel.onProximaAccionChange("Enviar correo")
            viewModel.onFechaProximoChange(fecha)
        }
        viewModel.onObservacionesChange("Observaciones")
    }

    @Test
    fun init_cargaOportunidadYObservaSeguimientos() = runTest {
        val viewModel = crearViewModel()
        advanceUntilIdle()

        assertNotNull(viewModel.oportunidad.value)
        assertEquals("Oportunidad 7", viewModel.oportunidad.value?.nombre)
        verify(exactly = 1) { seguimientoRepository.observarPorOportunidad(7L) }
        coVerify(exactly = 1) { oportunidadRepository.obtenerPorId(7L) }
    }

    @Test
    fun cambiosYConsumoDeRecordatorio_actualizanEstadoYLimpanErrores() = runTest {
        val viewModel = crearViewModel()
        viewModel.guardar()
        assertTrue(viewModel.uiState.value.errores.isNotEmpty())

        viewModel.onTipoAccionSeleccionado(TipoAccionSeguimiento.entries.first())
        viewModel.onResultadoChange("Resultado")
        viewModel.onProximaAccionChange("Llamar")
        viewModel.onFechaProximoChange(fecha)
        viewModel.onObservacionesChange("Obs")
        viewModel.onFotoTomada("/tmp/evidencia.jpg")
        assertEquals("Evidencia fotográfica adjunta", viewModel.uiState.value.mensaje)
        assertEquals("/tmp/evidencia.jpg", viewModel.uiState.value.fotoEvidencia)

        viewModel.onQuitarFoto()
        viewModel.onAviso("Aviso")
        viewModel.consumirRecordatorio()

        assertNull(viewModel.uiState.value.fotoEvidencia)
        assertEquals("Aviso", viewModel.uiState.value.mensaje)
        assertNull(viewModel.uiState.value.recordatorioProgramadoEn)
        assertNull(viewModel.uiState.value.recordatorioTexto)
        assertFalse(viewModel.uiState.value.errores.containsKey("tipoAccion"))
        assertFalse(viewModel.uiState.value.errores.containsKey("resultado"))
        assertFalse(viewModel.uiState.value.errores.containsKey("fechaProximo"))
    }

    @Test
    fun guardar_conErrores_noLlamaAlRepositorio() = runTest {
        val viewModel = crearViewModel()

        viewModel.guardar()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.errores.containsKey("tipoAccion"))
        assertTrue(viewModel.uiState.value.errores.containsKey("resultado"))
        assertFalse(viewModel.uiState.value.esGuardando)
        coVerify(exactly = 0) { seguimientoRepository.insertar(any()) }
    }

    @Test
    fun guardar_conFecha_programaRecordatorioYReiniciaElFormulario() = runTest {
        coEvery { seguimientoRepository.insertar(any()) } returns 15L
        val viewModel = crearViewModel()
        llenarFormulario(viewModel)

        viewModel.guardar()
        advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertEquals("Seguimiento registrado y recordatorio programado", estado.mensaje)
        assertEquals(
            FechaUtils.parsearFecha(fecha)!! + 9L * 60 * 60 * 1000,
            estado.recordatorioProgramadoEn
        )
        assertEquals("Enviar correo", estado.recordatorioTexto)
        assertEquals("", estado.resultado)
        assertNull(estado.fotoEvidencia)
        assertFalse(estado.esGuardando)
        coVerify(exactly = 1) {
            seguimientoRepository.insertar(match { it.oportunidadId == 7L && it.tipoAccion != null })
        }
    }

    @Test
    fun guardar_sinFecha_registraSinRecordatorio() = runTest {
        coEvery { seguimientoRepository.insertar(any()) } returns 16L
        val viewModel = crearViewModel()
        llenarFormulario(viewModel, conFecha = false)

        viewModel.guardar()
        advanceUntilIdle()

        assertEquals("Seguimiento registrado", viewModel.uiState.value.mensaje)
        assertNull(viewModel.uiState.value.recordatorioProgramadoEn)
        assertNull(viewModel.uiState.value.recordatorioTexto)
    }

    @Test
    fun guardar_cuandoInsertarFalla_muestraMensajeDeError() = runTest {
        coEvery { seguimientoRepository.insertar(any()) } returns 0L
        val viewModel = crearViewModel()
        llenarFormulario(viewModel)

        viewModel.guardar()
        advanceUntilIdle()

        assertEquals("No se pudo registrar el seguimiento", viewModel.uiState.value.mensaje)
        assertNull(viewModel.uiState.value.recordatorioProgramadoEn)
    }

    @Test
    fun guardar_conProximaAccionVaciaUsaTextoGenericoEnElRecordatorio() = runTest {
        coEvery { seguimientoRepository.insertar(any()) } returns 17L
        val viewModel = crearViewModel()
        viewModel.onTipoAccionSeleccionado(TipoAccionSeguimiento.entries.first())
        viewModel.onResultadoChange("Resultado")
        viewModel.onFechaProximoChange(fecha)

        viewModel.guardar()
        advanceUntilIdle()

        assertEquals(
            "Revisa el historial de la oportunidad",
            viewModel.uiState.value.recordatorioTexto
        )
    }
}
