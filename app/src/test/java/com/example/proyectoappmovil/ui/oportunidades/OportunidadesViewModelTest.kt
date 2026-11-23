package com.example.proyectoappmovil.ui.oportunidades

import com.example.proyectoappmovil.data.MainDispatcherRule
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.local.entity.Usuario
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.EstadoUsuario
import com.example.proyectoappmovil.data.model.PerfilUsuario
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.data.repository.OportunidadRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OportunidadesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repositorio: OportunidadRepository
    private lateinit var viewModel: OportunidadesViewModel

    private fun crearOportunidad(id: Long = 1L, responsableId: Long? = 10L) = Oportunidad(
        id = id,
        nombre = "Oportunidad $id",
        tipo = TipoEvento.entries.first(),
        descripcion = "Desc",
        fuenteInformacion = "Web",
        fechaDeteccion = 1000L,
        responsableId = responsableId,
        prioridad = Prioridad.entries.first(),
        potencialComercial = "Medio",
        estado = EstadoOportunidad.entries.first(),
        eventoId = null,
        fechaActualizacion = 2000L,
        observaciones = ""
    )

    private fun crearUsuario(id: Long, perfil: PerfilUsuario) = Usuario(
        id = id,
        username = "user$id",
        nombre = "Usuario $id",
        perfil = perfil,
        area = "Comercial",
        zona = "Centro",
        estadoCuenta = EstadoUsuario.entries.first(),
        fechaCreacion = 1000L,
        fechaActualizacion = 1000L
    )

    @Before
    fun setUp() {
        repositorio = mockk(relaxed = true)
        every { repositorio.observarTodas() } returns flowOf(emptyList())
        viewModel = OportunidadesViewModel(repositorio)
    }

    @Test
    fun refrescar_exito_limpiaErrorYFinalizaCarga() = runTest {
        coEvery { repositorio.refrescar() } returns Unit

        viewModel.refrescar()
        advanceUntilIdle()

        assertFalse(viewModel.cargando.value)
        assertNull(viewModel.error.value)
        coVerify(exactly = 1) { repositorio.refrescar() }
    }

    @Test
    fun refrescar_error_exponeMensajeYFinalizaCarga() = runTest {
        coEvery { repositorio.refrescar() } throws RuntimeException("Sin conexión")

        viewModel.refrescar()
        advanceUntilIdle()

        assertFalse(viewModel.cargando.value)
        assertEquals("Sin conexión", viewModel.error.value)
    }

    @Test
    fun refrescar_error_servidorCaido_mantieneListaLocal() = runTest {
        val local = crearOportunidad(7L)
        every { repositorio.observarTodas() } returns flowOf(listOf(local))
        coEvery { repositorio.refrescar() } throws RuntimeException("Sin conexión")
        val vm = OportunidadesViewModel(repositorio)

        vm.refrescar()
        advanceUntilIdle()

        assertEquals("Sin conexión", vm.error.value)
        assertFalse(vm.cargando.value)
        assertEquals(listOf(local), vm.oportunidades.first())
    }

    @Test
    fun eliminarOportunidad_exito_llamaRepositorioYCallback() = runTest {
        val op = crearOportunidad(3L)
        var callbackEjecutado = false
        coEvery { repositorio.eliminar(op) } returns Unit

        viewModel.eliminarOportunidad(op) { callbackEjecutado = true }
        advanceUntilIdle()

        assertTrue(callbackEjecutado)
        assertNull(viewModel.error.value)
        coVerify(exactly = 1) { repositorio.eliminar(op) }
    }

    @Test
    fun eliminarOportunidad_error_servidorCaido_mantieneListaLocal() = runTest {
        val local = crearOportunidad(8L)
        every { repositorio.observarTodas() } returns flowOf(listOf(local))
        coEvery { repositorio.eliminar(local) } throws RuntimeException("Sin conexión")
        val vm = OportunidadesViewModel(repositorio)

        vm.eliminarOportunidad(local)
        advanceUntilIdle()

        assertEquals("Sin conexión", vm.error.value)
        assertFalse(vm.cargando.value)
        assertEquals(listOf(local), vm.oportunidades.first())
    }

    @Test
    fun puedeEliminar_permiteAdminYCreador_bloqueaOtros() {
        val supervisor = crearUsuario(1L, PerfilUsuario.SUPERVISOR)
        val creador = crearUsuario(10L, PerfilUsuario.EJECUTIVO)
        val otroUsuario = crearUsuario(20L, PerfilUsuario.EJECUTIVO)
        val op = crearOportunidad(id = 1L, responsableId = 10L)

        assertTrue(viewModel.puedeEliminar(op, supervisor))
        assertTrue(viewModel.puedeEliminar(op, creador))
        assertFalse(viewModel.puedeEliminar(op, otroUsuario))
        assertFalse(viewModel.puedeEliminar(op, null))
    }
}