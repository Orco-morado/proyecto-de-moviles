package com.example.proyectoappmovil.data.model

import java.util.EnumSet

object RolePermissions {

    private val permissionsByRole: Map<PerfilUsuario, Set<Permission>> = mapOf(
        PerfilUsuario.EJECUTIVO to EnumSet.of(
            Permission.VER_OPORTUNIDADES_PROPIAS,
            Permission.CREAR_OPORTUNIDAD,
            Permission.VER_SEGUIMIENTOS,
            Permission.CREAR_SEGUIMIENTO,
            Permission.VER_ORGANIZACIONES_CONTACTOS,
            Permission.VER_ALERTAS
        ),
        PerfilUsuario.SUPERVISOR to EnumSet.allOf(Permission::class.java),
        PerfilUsuario.MARKETING to EnumSet.of(
            Permission.VER_OPORTUNIDADES_PROPIAS,
            Permission.CREAR_OPORTUNIDAD,
            Permission.VER_SEGUIMIENTOS,
            Permission.VER_ORGANIZACIONES_CONTACTOS,
            Permission.VER_ALERTAS,
            Permission.VER_REPORTES_GLOBALES
        )
    )

    fun getPermissions(perfil: PerfilUsuario): Set<Permission> =
        permissionsByRole[perfil] ?: emptySet()

    fun hasPermission(perfil: PerfilUsuario, permission: Permission): Boolean =
        getPermissions(perfil).contains(permission)
}