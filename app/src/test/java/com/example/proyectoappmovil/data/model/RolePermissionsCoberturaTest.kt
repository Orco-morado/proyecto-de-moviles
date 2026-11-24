package com.example.proyectoappmovil.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RolePermissionsCoberturaTest {

    @Test
    fun supervisor_tieneTodosLosPermisos() {
        val permisos = RolePermissions.getPermissions(PerfilUsuario.SUPERVISOR)

        assertEquals(Permission.entries.toSet(), permisos)
        Permission.entries.forEach {
            assertTrue(RolePermissions.hasPermission(PerfilUsuario.SUPERVISOR, it))
        }
    }

    @Test
    fun ejecutivo_tienePermisosDeGestionPropia() {
        val permisos = RolePermissions.getPermissions(PerfilUsuario.EJECUTIVO)

        assertTrue(permisos.contains(Permission.VER_OPORTUNIDADES_PROPIAS))
        assertTrue(permisos.contains(Permission.CREAR_OPORTUNIDAD))
        assertTrue(permisos.contains(Permission.VER_SEGUIMIENTOS))
        assertTrue(permisos.contains(Permission.CREAR_SEGUIMIENTO))
        assertTrue(permisos.contains(Permission.VER_ORGANIZACIONES_CONTACTOS))
        assertTrue(permisos.contains(Permission.VER_ALERTAS))
        assertFalse(permisos.contains(Permission.VER_TODAS_OPORTUNIDADES))
        assertFalse(permisos.contains(Permission.GESTIONAR_USUARIOS))
        assertFalse(RolePermissions.hasPermission(PerfilUsuario.EJECUTIVO, Permission.CONFIGURAR_APP))
    }

    @Test
    fun marketing_tieneReportesPeroNoGestionDeUsuarios() {
        val permisos = RolePermissions.getPermissions(PerfilUsuario.MARKETING)

        assertTrue(permisos.contains(Permission.VER_REPORTES_GLOBALES))
        assertFalse(permisos.contains(Permission.CREAR_EDITAR_ORGANIZACIONES_CONTACTOS))
        assertFalse(RolePermissions.hasPermission(PerfilUsuario.MARKETING, Permission.GESTIONAR_USUARIOS))
    }
}
