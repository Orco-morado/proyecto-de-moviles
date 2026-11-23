package com.example.proyectoappmovil.ui.oportunidades

import com.example.proyectoappmovil.data.MainDispatcherRule
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.data.repository.OportunidadRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
}
