package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.entity.Usuario
import com.example.proyectoappmovil.data.model.EstadoUsuario
import com.example.proyectoappmovil.data.model.PerfilUsuario
import com.example.proyectoappmovil.data.model.Permission
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionManagerCoberturaTest {

    private fun crearUsuario(perfil: PerfilUsuario) = Usuario(
        id = 1L,
        username = "usuario",
        nombre = "Usuario",
        perfil = perfil,
        area = "Comercial",
        zona = "Metropolitana",
        estadoCuenta = EstadoUsuario.ACTIVO,
        passwordHash = "hash",
        salt = "salt",
        fechaCreacion = 1000L,
        fechaActualizacion = 2000L
    )

    private fun crearManager(usuario: Usuario?): PermissionManager {
        val sessionManager = mockk<SessionManager>(relaxed = true)
        every { sessionManager.currentUser } returns MutableStateFlow(usuario)
        return PermissionManager(sessionManager)
    }

    @Test
    fun sinSesion_noTienePermisosYNoEstaLogueado() = runBlocking {
        val manager = crearManager(null)

        assertEquals(null, manager.getCurrentUser())
        assertNull(manager.getUserPerfil())
        assertFalse(manager.isLoggedIn())
        assertFalse(manager.hasPermission(Permission.VER_ALERTAS))
        assertFalse(manager.hasAnyPermission(setOf(Permission.VER_ALERTAS, Permission.VER_SEGUIMIENTOS)))
    }

    @Test
    fun conSupervisor_reflejaLaSesionYTodosLosPermisos() = runBlocking {
        val usuario = crearUsuario(PerfilUsuario.SUPERVISOR)
        val manager = crearManager(usuario)
        withTimeout(2000) { manager.currentUser.first { it != null } }

        assertEquals(usuario, manager.getCurrentUser())
        assertEquals(PerfilUsuario.SUPERVISOR, manager.getUserPerfil())
        assertTrue(manager.isLoggedIn())
        assertTrue(manager.hasPermission(Permission.GESTIONAR_USUARIOS))
        assertTrue(manager.hasAnyPermission(setOf(Permission.VER_SEGUIMIENTOS, Permission.GESTIONAR_USUARIOS)))
    }

    @Test
    fun conEjecutivo_respetaLosPermisosDelRol() = runBlocking {
        val manager = crearManager(crearUsuario(PerfilUsuario.EJECUTIVO))
        withTimeout(2000) { manager.currentUser.first { it != null } }

        assertTrue(manager.hasPermission(Permission.CREAR_SEGUIMIENTO))
        assertFalse(manager.hasPermission(Permission.GESTIONAR_USUARIOS))
        assertFalse(manager.hasAnyPermission(setOf(Permission.GESTIONAR_USUARIOS, Permission.CONFIGURAR_APP)))
    }
}
