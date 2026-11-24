package com.example.proyectoappmovil.ui.contactos

import com.example.proyectoappmovil.data.MainDispatcherRule
import com.example.proyectoappmovil.data.local.entity.Contacto
import com.example.proyectoappmovil.data.model.AreaContacto
import com.example.proyectoappmovil.data.model.EstadoContacto
import com.example.proyectoappmovil.data.model.MedioContacto
import com.example.proyectoappmovil.data.repository.ContactoRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class ContactoFormViewModelCoberturaTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repositorio = mockk<ContactoRepository>(relaxed = true)

    private fun crearContacto() = Contacto(
        id = 9L,
        nombre = "Contacto existente",
        organizacionId = 3L,
        cargo = "Encargado",
        area = AreaContacto.RECURSOS_HUMANOS,
        correo = "contacto@ejemplo.cl",
        telefono = "+56 9 1111 2222",
        medioPreferente = MedioContacto.TELEFONO,
        estado = EstadoContacto.INACTIVO,
        fechaUltimoContacto = 123L,
        observaciones = "Observaciones"
    )

    private fun llenarFormulario(viewModel: ContactoFormViewModel) {
        viewModel.onNombreChange("Nombre de prueba")
        viewModel.onCargoChange("Cargo de prueba")
        viewModel.onCorreoChange("prueba@ejemplo.cl")
        viewModel.onTelefonoChange("+56 9 3333 4444")
        viewModel.onObservacionesChange("Obs")
        viewModel.onAreaSeleccionada(AreaContacto.MARKETING)
        viewModel.onMedioSeleccionado(MedioContacto.CORREO)
        viewModel.onEstadoSeleccionado(EstadoContacto.ACTIVO)
    }

    @Test
    fun init_conId_cargaElContactoParaEditar() = runTest {
        coEvery { repositorio.obtenerPorId(9L) } returns crearContacto()

        val viewModel = ContactoFormViewModel(repositorio, 3L, 9L)
        advanceUntilIdle()

        assertEquals("Contacto existente", viewModel.uiState.value.nombre)
        assertEquals(AreaContacto.RECURSOS_HUMANOS, viewModel.uiState.value.area)
        assertEquals(EstadoContacto.INACTIVO, viewModel.uiState.value.estado)
        coVerify(exactly = 1) { repositorio.obtenerPorId(9L) }
    }

    @Test
    fun init_sinId_noConsultaRepositorio() = runTest {
        val viewModel = ContactoFormViewModel(repositorio, 3L, null)
        advanceUntilIdle()

        coVerify(exactly = 0) { repositorio.obtenerPorId(any()) }
    }

    @Test
    fun cambiosLimpanErroresYActualizanSelecciones() = runTest {
        val viewModel = ContactoFormViewModel(repositorio, 3L, null)
        viewModel.guardar()
        assertTrue(viewModel.uiState.value.errores.isNotEmpty())

        viewModel.onNombreChange("Nombre")
        viewModel.onCargoChange("Cargo")
        viewModel.onCorreoChange("nombre@ejemplo.cl")
        viewModel.onTelefonoChange("+56 9 5555 6666")
        viewModel.onObservacionesChange("Obs")
        viewModel.onAreaSeleccionada(AreaContacto.BIENESTAR)
        viewModel.onMedioSeleccionado(MedioContacto.REUNION)
        viewModel.onEstadoSeleccionado(EstadoContacto.INACTIVO)

        assertEquals(AreaContacto.BIENESTAR, viewModel.uiState.value.area)
        assertEquals(MedioContacto.REUNION, viewModel.uiState.value.medioPreferente)
        assertEquals(EstadoContacto.INACTIVO, viewModel.uiState.value.estado)
        assertFalse(viewModel.uiState.value.errores.containsKey("nombre"))
        assertFalse(viewModel.uiState.value.errores.containsKey("cargo"))
        assertFalse(viewModel.uiState.value.errores.containsKey("correo"))
        assertFalse(viewModel.uiState.value.errores.containsKey("telefono"))
    }

    @Test
    fun guardar_conErrores_noLlamaAlRepositorio() = runTest {
        val viewModel = ContactoFormViewModel(repositorio, 3L, null)

        viewModel.guardar()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.errores.containsKey("nombre"))
        assertTrue(viewModel.uiState.value.errores.containsKey("correo"))
        assertFalse(viewModel.uiState.value.esGuardando)
        coVerify(exactly = 0) { repositorio.insertar(any()) }
    }

    @Test
    fun guardar_nuevoContacto_registra() = runTest {
        coEvery { repositorio.insertar(any()) } returns 12L
        val viewModel = ContactoFormViewModel(repositorio, 3L, null)
        llenarFormulario(viewModel)

        viewModel.guardar()
        advanceUntilIdle()

        assertEquals("Contacto registrado", viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.esGuardando)
        coVerify(exactly = 1) {
            repositorio.insertar(match { it.organizacionId == 3L && it.nombre == "Nombre de prueba" })
        }
    }

    @Test
    fun guardar_nuevoContacto_cuandoInsertarFalla_muestraMensaje() = runTest {
        coEvery { repositorio.insertar(any()) } returns 0L
        val viewModel = ContactoFormViewModel(repositorio, 3L, null)
        llenarFormulario(viewModel)

        viewModel.guardar()
        advanceUntilIdle()

        assertEquals("No se pudo registrar", viewModel.uiState.value.mensaje)
    }

    @Test
    fun guardar_contactoExistente_actualizaConservandoFechaUltimoContacto() = runTest {
        coEvery { repositorio.obtenerPorId(9L) } returns crearContacto()
        val viewModel = ContactoFormViewModel(repositorio, 3L, 9L)
        advanceUntilIdle()
        llenarFormulario(viewModel)

        viewModel.guardar()
        advanceUntilIdle()

        assertEquals("Contacto actualizado", viewModel.uiState.value.mensaje)
        coVerify(exactly = 1) {
            repositorio.actualizar(match { it.id == 9L && it.fechaUltimoContacto == 123L })
        }
        coVerify(exactly = 0) { repositorio.insertar(any()) }
    }
}
