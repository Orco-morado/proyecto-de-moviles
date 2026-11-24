package com.example.proyectoappmovil.ui.organizaciones

import com.example.proyectoappmovil.data.MainDispatcherRule
import com.example.proyectoappmovil.data.local.entity.Organizacion
import com.example.proyectoappmovil.data.model.TipoOrganizacion
import com.example.proyectoappmovil.data.repository.OrganizacionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrganizacionFormViewModelCoberturaTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repositorio = mockk<OrganizacionRepository>(relaxed = true)

    private fun crearOrganizacion() = Organizacion(
        id = 5L,
        nombre = "Organización existente",
        tipo = TipoOrganizacion.EMPRESA,
        rubro = "Tecnología",
        comuna = "Providencia",
        region = "Metropolitana",
        sitioWeb = "https://ejemplo.cl",
        publicoAsociado = "Trabajadores",
        observaciones = "Observaciones",
        latitud = -33.4,
        longitud = -70.6
    )

    private fun llenarFormulario(viewModel: OrganizacionFormViewModel) {
        viewModel.onNombreChange("Organización de prueba")
        viewModel.onTipoSeleccionado(TipoOrganizacion.EMPRESA)
        viewModel.onRubroChange("Servicios")
        viewModel.onComunaChange("Providencia")
        viewModel.onRegionChange("Metropolitana")
        viewModel.onSitioWebChange("https://prueba.cl")
        viewModel.onPublicoAsociadoChange("Público")
        viewModel.onObservacionesChange("Obs")
    }

    @Test
    fun init_conId_cargaLaOrganizacionParaEditar() = runTest {
        coEvery { repositorio.obtenerPorId(5L) } returns crearOrganizacion()

        val viewModel = OrganizacionFormViewModel(repositorio, 5L)
        advanceUntilIdle()

        assertEquals("Organización existente", viewModel.uiState.value.nombre)
        assertEquals(TipoOrganizacion.EMPRESA, viewModel.uiState.value.tipo)
        assertEquals(-33.4, viewModel.uiState.value.latitud)
        coVerify(exactly = 1) { repositorio.obtenerPorId(5L) }
    }

    @Test
    fun init_sinId_noConsultaRepositorio() = runTest {
        val viewModel = OrganizacionFormViewModel(repositorio, null)
        advanceUntilIdle()

        coVerify(exactly = 0) { repositorio.obtenerPorId(any()) }
        assertNull(viewModel.uiState.value.mensaje)
    }

    @Test
    fun cambiosDeUbicacionYLimpiezaDeErrores_actualizanEstado() = runTest {
        val viewModel = OrganizacionFormViewModel(repositorio, null)
        viewModel.guardar()
        assertTrue(viewModel.uiState.value.errores.isNotEmpty())

        viewModel.onNombreChange("Nombre")
        viewModel.onComunaChange("Comuna")
        viewModel.onRegionChange("Región")
        viewModel.onSitioWebChange("https://sitio.cl")
        viewModel.onTipoSeleccionado(TipoOrganizacion.EMPRESA)
        viewModel.onUbicacionCapturada(-33.5, -70.5)

        assertEquals(-33.5, viewModel.uiState.value.latitud)
        assertEquals(-70.5, viewModel.uiState.value.longitud)
        assertFalse(viewModel.uiState.value.errores.containsKey("nombre"))
        assertFalse(viewModel.uiState.value.errores.containsKey("comuna"))
        assertFalse(viewModel.uiState.value.errores.containsKey("region"))
        assertFalse(viewModel.uiState.value.errores.containsKey("sitioWeb"))
        assertFalse(viewModel.uiState.value.errores.containsKey("tipo"))

        viewModel.onQuitarUbicacion()
        assertNull(viewModel.uiState.value.latitud)
        assertNull(viewModel.uiState.value.longitud)
    }

    @Test
    fun guardar_conErrores_noLlamaAlRepositorio() = runTest {
        val viewModel = OrganizacionFormViewModel(repositorio, null)

        viewModel.guardar()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.errores.containsKey("nombre"))
        assertTrue(viewModel.uiState.value.errores.containsKey("tipo"))
        assertFalse(viewModel.uiState.value.esGuardando)
        coVerify(exactly = 0) { repositorio.insertar(any()) }
    }

    @Test
    fun guardar_nuevaOrganizacion_registraYRecortaEspacios() = runTest {
        coEvery { repositorio.insertar(any()) } returns 10L
        val viewModel = OrganizacionFormViewModel(repositorio, null)
        llenarFormulario(viewModel)

        viewModel.guardar()
        advanceUntilIdle()

        assertEquals("Organización registrada", viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.esGuardando)
        coVerify(exactly = 1) {
            repositorio.insertar(match { it.nombre == "Organización de prueba" && it.comuna == "Providencia" })
        }
    }

    @Test
    fun guardar_nuevaOrganizacion_cuandoInsertarFalla_muestraMensaje() = runTest {
        coEvery { repositorio.insertar(any()) } returns 0L
        val viewModel = OrganizacionFormViewModel(repositorio, null)
        llenarFormulario(viewModel)

        viewModel.guardar()
        advanceUntilIdle()

        assertEquals("No se pudo registrar", viewModel.uiState.value.mensaje)
    }

    @Test
    fun guardar_organizacionExistente_actualiza() = runTest {
        val viewModel = OrganizacionFormViewModel(repositorio, 5L)
        advanceUntilIdle()
        llenarFormulario(viewModel)

        viewModel.guardar()
        advanceUntilIdle()

        assertEquals("Organización actualizada", viewModel.uiState.value.mensaje)
        coVerify(exactly = 1) { repositorio.actualizar(match { it.id == 5L }) }
        coVerify(exactly = 0) { repositorio.insertar(any()) }
    }
}
