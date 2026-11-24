package com.example.proyectoappmovil.ui.login

import com.example.proyectoappmovil.data.MainDispatcherRule
import com.example.proyectoappmovil.data.local.entity.Usuario
import com.example.proyectoappmovil.data.model.EstadoUsuario
import com.example.proyectoappmovil.data.model.PerfilUsuario
import com.example.proyectoappmovil.data.repository.AuthRepository
import com.example.proyectoappmovil.data.repository.AuthResult
import com.example.proyectoappmovil.data.repository.SessionManager
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
class LoginViewModelCoberturaTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository = mockk<AuthRepository>(relaxed = true)
    private val sessionManager = mockk<SessionManager>(relaxed = true)

    private fun crearUsuario() = Usuario(
        id = 4L,
        username = "usuario",
        nombre = "Nombre Usuario",
        perfil = PerfilUsuario.EJECUTIVO,
        area = "Comercial",
        zona = "Metropolitana",
        estadoCuenta = EstadoUsuario.ACTIVO,
        passwordHash = "hash",
        salt = "salt",
        fechaCreacion = 1000L,
        fechaActualizacion = 2000L
    )

    @Test
    fun submit_conCredencialesValidas_guardaSesionYLlamaAlCallback() = runTest {
        val usuario = crearUsuario()
        coEvery { authRepository.login("usuario", "clave") } returns AuthResult.Success(usuario)
        val viewModel = LoginViewModel(authRepository, sessionManager)
        viewModel.onUsernameChange("  usuario  ")
        viewModel.onPasswordChange("clave")

        var nombreRecibido: String? = null
        viewModel.submit { nombreRecibido = it }
        advanceUntilIdle()

        assertEquals("Nombre Usuario", nombreRecibido)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
        coVerify(exactly = 1) { sessionManager.saveUser(usuario) }
    }

    @Test
    fun submit_cuandoFallautenticacion_exponeElMotivo() = runTest {
        coEvery { authRepository.login(any(), any()) } returns AuthResult.Failure("Credenciales inválidas")
        val viewModel = LoginViewModel(authRepository, sessionManager)
        viewModel.onUsernameChange("usuario")
        viewModel.onPasswordChange("clave")

        viewModel.submit {}
        advanceUntilIdle()

        assertEquals("Credenciales inválidas", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
        coVerify(exactly = 0) { sessionManager.saveUser(any()) }
    }

    @Test
    fun cambiosDeCampos_limpianElError() = runTest {
        coEvery { authRepository.login(any(), any()) } returns AuthResult.Failure("Error previo")
        val viewModel = LoginViewModel(authRepository, sessionManager)
        viewModel.submit {}
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.error != null)

        viewModel.onUsernameChange("nuevo")
        assertNull(viewModel.uiState.value.error)

        viewModel.submit {}
        advanceUntilIdle()
        viewModel.onPasswordChange("nueva")
        assertNull(viewModel.uiState.value.error)
    }
}
