package com.example.proyectoappmovil.ui.oportunidades

import com.example.proyectoappmovil.data.MainDispatcherRule
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.data.repository.OportunidadRepository
import com.example.proyectoappmovil.utils.FechaUtils
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class OportunidadFormViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repositorio = mockk<OportunidadRepository>(relaxed = true)

    private fun llenarFormulario(viewModel: OportunidadFormViewModel) {
        viewModel.onNombreChange("Oportunidad de prueba")
        viewModel.onTipoSeleccionado(TipoEvento.FERIA_EMPRESARIAL)
        viewModel.onDescripcionChange("Descripcion valida")
        viewModel.onFuenteInformacionChange("Web")
        viewModel.onFechaDeteccionChange("22/11/2026")
        viewModel.onPrioridadSeleccionada(Prioridad.ALTA)
        viewModel.onPotencialComercialChange("Alto")
    }

    @Test
    fun guardar_servidorCaeAlInsertar_muestraMensajeYTerminaCarga() = runTest {
        coEvery { repositorio.insertar(any()) } throws IOException("Sin conexión")
        val viewModel = OportunidadFormViewModel(repositorio, null)
        llenarFormulario(viewModel)

        viewModel.guardar()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.esGuardando)
        assertEquals("No se pudo conectar con el servidor", viewModel.uiState.value.mensaje)
        coVerify(exactly = 1) { repositorio.insertar(any()) }
    }

    @Test
    fun guardar_servidorCaeAlActualizar_muestraMensajeYTerminaCarga() = runTest {
        coEvery { repositorio.actualizar(any()) } throws IOException("Sin conexión")
        val viewModel = OportunidadFormViewModel(repositorio, 5L)
        llenarFormulario(viewModel)

        viewModel.guardar()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.esGuardando)
        assertEquals("No se pudo conectar con el servidor", viewModel.uiState.value.mensaje)
        coVerify(exactly = 1) { repositorio.actualizar(any()) }
    }

    @Test
    fun guardar_conErroresValidacion_noLlamaRepositorioYExponeErrores() = runTest {
        val viewModel = OportunidadFormViewModel(repositorio, null)

        viewModel.guardar()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.errores.containsKey("nombre"))
        assertTrue(viewModel.uiState.value.errores.containsKey("tipo"))
        assertFalse(viewModel.uiState.value.esGuardando)
        coVerify(exactly = 0) { repositorio.insertar(any()) }
    }

    @Test
    fun init_conId_cargaOportunidadParaEditar() = runTest {
        coEvery { repositorio.obtenerPorId(5L) } returns Oportunidad(
            id = 5L,
            nombre = "Oportunidad existente",
            tipo = TipoEvento.FERIA_EMPRESARIAL,
            descripcion = "Descripcion existente",
            fuenteInformacion = "Web existente",
            fechaDeteccion = 1000L,
            responsableId = null,
            prioridad = Prioridad.BAJA,
            potencialComercial = "Bajo",
            estado = EstadoOportunidad.EVENTO_IDENTIFICADO,
            eventoId = null,
            fechaActualizacion = 2000L,
            observaciones = "Observaciones"
        )

        val viewModel = OportunidadFormViewModel(repositorio, 5L)
        advanceUntilIdle()

        assertEquals("Oportunidad existente", viewModel.uiState.value.nombre)
        assertEquals(FechaUtils.formatearFecha(1000L), viewModel.uiState.value.fechaDeteccion)
        assertEquals(Prioridad.BAJA, viewModel.uiState.value.prioridad)
        assertEquals("Observaciones", viewModel.uiState.value.observaciones)
    }

    @Test
    fun guardar_nuevaOportunidad_exito_muestraMensajeYTerminaCarga() = runTest {
        coEvery { repositorio.insertar(any()) } returns 11L
        val viewModel = OportunidadFormViewModel(repositorio, null)
        llenarFormulario(viewModel)

        viewModel.guardar()
        advanceUntilIdle()

        assertEquals("Oportunidad registrada", viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.esGuardando)
        coVerify(exactly = 1) { repositorio.insertar(any()) }
    }

    @Test
    fun guardar_oportunidadExistente_exito_actualizaYMuestraMensaje() = runTest {
        coEvery { repositorio.actualizar(any()) } returns Unit
        val viewModel = OportunidadFormViewModel(repositorio, 5L)
        llenarFormulario(viewModel)

        viewModel.guardar()
        advanceUntilIdle()

        assertEquals("Oportunidad registrada", viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.esGuardando)
        coVerify(exactly = 1) { repositorio.actualizar(any()) }
    }
}
