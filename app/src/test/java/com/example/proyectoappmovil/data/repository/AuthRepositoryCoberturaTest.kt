package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.entity.Usuario
import com.example.proyectoappmovil.data.model.EstadoUsuario
import com.example.proyectoappmovil.data.model.PerfilUsuario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mindrot.jbcrypt.BCrypt

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryCoberturaTest {

    private val usuarioRepository = mockk<UsuarioRepository>(relaxed = true)
    private val repositorio = AuthRepository(usuarioRepository)
    private val clavePrueba = "clave-prueba"

    private fun crearUsuario(
        estado: EstadoUsuario = EstadoUsuario.ACTIVO,
        passwordHash: String = BCrypt.hashpw(clavePrueba, BCrypt.gensalt())
    ) = Usuario(
        id = 1L,
        username = "usuario",
        nombre = "Usuario",
        perfil = PerfilUsuario.EJECUTIVO,
        area = "Comercial",
        zona = "Metropolitana",
        estadoCuenta = estado,
        passwordHash = passwordHash,
        salt = "salt",
        fechaCreacion = 1000L,
        fechaActualizacion = 2000L
    )

    @Test
    fun login_usuarioInexistente_devuelveFailure() = runTest {
        coEvery { usuarioRepository.obtenerPorUsername(any()) } returns null

        val resultado = repositorio.login("  usuario  ", clavePrueba)

        assertEquals("Usuario no encontrado", (resultado as AuthResult.Failure).reason)
        coVerify(exactly = 1) { usuarioRepository.obtenerPorUsername("usuario") }
    }

    @Test
    fun login_cuentaInactiva_devuelveFailure() = runTest {
        coEvery { usuarioRepository.obtenerPorUsername(any()) } returns crearUsuario(EstadoUsuario.INACTIVO)

        val resultado = repositorio.login("usuario", clavePrueba)

        assertEquals("Cuenta inactiva", (resultado as AuthResult.Failure).reason)
    }

    @Test
    fun login_claveIncorrecta_devuelveFailure() = runTest {
        coEvery { usuarioRepository.obtenerPorUsername(any()) } returns crearUsuario()

        val resultado = repositorio.login("usuario", "clave-erronea")

        assertEquals("Contraseña incorrecta", (resultado as AuthResult.Failure).reason)
    }

    @Test
    fun login_hashInvalido_devuelveFailure() = runTest {
        coEvery { usuarioRepository.obtenerPorUsername(any()) } returns crearUsuario(passwordHash = "hash-invalido")

        val resultado = repositorio.login("usuario", clavePrueba)

        assertEquals("Contraseña incorrecta", (resultado as AuthResult.Failure).reason)
    }

    @Test
    fun login_credencialesValidas_devuelveSuccess() = runTest {
        val usuario = crearUsuario()
        coEvery { usuarioRepository.obtenerPorUsername(any()) } returns usuario

        val resultado = repositorio.login(" usuario ", clavePrueba)

        assertTrue(resultado is AuthResult.Success)
        assertEquals(usuario, (resultado as AuthResult.Success).usuario)
    }
}
